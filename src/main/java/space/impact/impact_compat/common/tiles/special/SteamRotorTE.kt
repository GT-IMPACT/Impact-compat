package space.impact.impact_compat.common.tiles.special

import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.*
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.manager.AnimationData
import space.impact.impact_compat.addon.gt.features.steam_age.api.IKinetic
import space.impact.impact_compat.addon.gt.features.steam_age.api.KineticSpeed
import space.impact.impact_compat.common.network.Network
import space.impact.impact_compat.common.tiles.BaseTileRotationEntityModel
import space.impact.impact_compat.core.NBT
import space.impact.impact_compat.core.WorldAround
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of
import space.impact.packet_network.network.NetworkHandler.sendToAllAround

class SteamRotorTE : BaseTileRotationEntityModel(), IFluidTank, IFluidHandler, IKinetic {

    companion object {
        private const val WORK_SPEED4 = "work.speed.4"
        private const val WORK_SPEED2 = "work.speed.2"
        private const val WORK_SPEED1 = "work.speed.1"
        private val LOW_BOUND = 50..249
        private val MEDIUM_BOUND = 250..999
        private val HIGH_BOUND = 1000..Int.MAX_VALUE
        private const val ZERO = 0
        private const val CAPACITY = 10_000
        private const val STEAM_FLUID = "steam"
    }

    private var mFluid: FluidStack? = null
    private var speed: KineticSpeed = KineticSpeed.STOP
    private var oSpeed: KineticSpeed = KineticSpeed.STOP

    override fun registerControllers(data: AnimationData) {
        val high = AnimationBuilder().addAnimation(WORK_SPEED1, ILoopType.EDefaultLoopTypes.LOOP)
        val medium = AnimationBuilder().addAnimation(WORK_SPEED2, ILoopType.EDefaultLoopTypes.LOOP)
        val low = AnimationBuilder().addAnimation(WORK_SPEED4, ILoopType.EDefaultLoopTypes.LOOP)

        data.addAnimationController(AnimationController(this, CONTROLLER, TICK) {
            when (speed) {
                KineticSpeed.HIGH -> {
                    it.controller.setAnimation(high); PlayState.CONTINUE
                }

                KineticSpeed.MEDIUM -> {
                    it.controller.setAnimation(medium); PlayState.CONTINUE
                }

                KineticSpeed.LOW -> {
                    it.controller.setAnimation(low); PlayState.CONTINUE
                }

                else -> PlayState.STOP
            }
        })
    }

    override fun hasInput(): Boolean = false
    override fun hasOutput(): Boolean = true

    override fun inputSide(): ForgeDirection = getFrontFacing()

    override fun currentSpeed(): KineticSpeed = speed

    override fun changeSpeed(speed: KineticSpeed) {
        this.speed = speed
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        speed = KineticSpeed.typeOf(data.getInteger(NBT.NBT_SPEED))
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger(NBT.NBT_SPEED, speed.speed)
    }

    override fun onPostTick(tick: Long) {
        if (isServerSide() && tick of WorldTick.SECOND) {
            if (mFluid?.amount == ZERO && speed != KineticSpeed.STOP) {
                speed = KineticSpeed.STOP
            }
            if (speed != KineticSpeed.STOP) {
                mFluid?.also {
                    it.amount = -when (speed) {
                        KineticSpeed.HIGH -> HIGH_BOUND.first
                        KineticSpeed.MEDIUM -> MEDIUM_BOUND.first
                        KineticSpeed.LOW -> LOW_BOUND.first
                        else -> ZERO
                    }
                    if (it.amount <= ZERO) it.amount = ZERO
                }
            }
            updateSpeed()
        }
    }

    private fun updateSpeed() {
        if (oSpeed != speed) {
            sendToAllAround(Network.PacketSteamRotor.transaction(speed.speed), WorldAround.CHUNK_4)
            oSpeed = speed
        }
    }

    override fun canUpdate(): Boolean {
        return true
    }

    override fun getTankInfo(from: ForgeDirection?): Array<FluidTankInfo> {
        val tList = ArrayList<FluidTankInfo>()
        if (from == getFrontFacing()) {
            tList.add(FluidTankInfo(mFluid, HIGH_BOUND.first))
        }
        return tList.toTypedArray<FluidTankInfo>()
    }

    override fun canFill(from: ForgeDirection?, fluid: Fluid?): Boolean {
        return from == getFrontFacing() && fluid?.name?.contains(STEAM_FLUID, true) == true
    }

    override fun canDrain(from: ForgeDirection?, fluid: Fluid?): Boolean {
        return from == getFrontFacing() && fluid?.name?.contains(STEAM_FLUID, true) == true
    }

    override fun drain(from: ForgeDirection?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun drain(from: ForgeDirection?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun fill(from: ForgeDirection?, resource: FluidStack?, doFill: Boolean): Int {
        if (from == getFrontFacing() && resource != null) {
            if (mFluid == null && resource.getFluid().name.contains(STEAM_FLUID, true)) {
                mFluid = resource.copy()
            }
            return when (resource.amount) {
                in HIGH_BOUND -> {
                    speed = KineticSpeed.HIGH
                    mFluid?.amount = mFluid?.amount?.plus(HIGH_BOUND.first)
                    HIGH_BOUND.first
                }

                in MEDIUM_BOUND -> {
                    speed = KineticSpeed.MEDIUM
                    mFluid?.amount = mFluid?.amount?.plus(resource.amount)
                    resource.amount
                }

                in LOW_BOUND -> {
                    speed = KineticSpeed.LOW
                    mFluid?.amount = mFluid?.amount?.plus(resource.amount)
                    resource.amount
                }

                else -> {
                    speed = KineticSpeed.STOP
                    ZERO
                }
            }
        }
        return ZERO
    }

    override fun getWailaBody(itemStack: ItemStack, currenttip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler) {
        super.getWailaBody(itemStack, currenttip, accessor, config)
        wailaBody(currenttip, accessor)
    }

    override fun getWailaNBTData(player: EntityPlayerMP, tile: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) {
        wailaNBTData(tag)
    }

    override fun getFluid(): FluidStack? {
        return mFluid
    }

    override fun getFluidAmount(): Int {
        return mFluid?.amount ?: ZERO
    }

    override fun getCapacity(): Int {
        return CAPACITY
    }

    override fun getInfo(): FluidTankInfo {
        return FluidTankInfo(mFluid, CAPACITY)
    }

    override fun fill(resource: FluidStack?, doFill: Boolean): Int {
        return ZERO
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        return null
    }
}
