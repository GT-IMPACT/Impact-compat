package space.impact.impact_compat.addon.gt.base.multi

import gregtech.api.interfaces.IHatchElement
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch
import gregtech.api.util.GT_Recipe
import gregtech.api.util.GT_Single_Recipe_Check
import gregtech.api.util.IGT_HatchAdder
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.addon.gt.features.steam_age.api.IKinetic
import space.impact.impact_compat.addon.gt.features.steam_age.api.KineticSpeed
import space.impact.impact_compat.addon.gt.features.steam_age.hatches.SteamRotorHatch
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of

abstract class KineticMultiBlockBase<T> : CompatMultiBlockBase<T>, IKinetic where T : CompatMultiBlockBase<T> {

    companion object {
        @JvmStatic
        protected val RotorHatch = object : IHatchElement<KineticMultiBlockBase<*>> {
            override fun mteClasses(): MutableList<out Class<out IMetaTileEntity>> = mutableListOf(SteamRotorHatch::class.java)
            override fun adder(): IGT_HatchAdder<in KineticMultiBlockBase<*>> = IGT_HatchAdder { base, te, index -> base.addKineticHatch(te, index) }
            override fun name(): String = "RotorHatch"
            override fun count(t: KineticMultiBlockBase<*>): Long = t.rotorHatches.size.toLong()
        }
    }

    protected val rotorHatches: ArrayList<IKinetic> = arrayListOf()
    protected var speedRotor: KineticSpeed = KineticSpeed.STOP

    constructor(id: Int, name: String, unlocalized: String, isEnabledMaintenance: Boolean = true) : super(id, name, unlocalized, isEnabledMaintenance)
    constructor(name: String, isEnabledMaintenance: Boolean = true) : super(name, isEnabledMaintenance)

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        speedRotor.saveNBT(aNBT)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        speedRotor = KineticSpeed.loadNBT(aNBT)
    }

    fun addKineticHatch(te: IGregTechTileEntity?, index: Short): Boolean {
        if (te == null) return false
        val mte = te.metaTileEntity ?: return false
        if (mte is GT_MetaTileEntity_Hatch && mte is IKinetic) {
            mte.updateTexture(index.toInt())
            return rotorHatches.add(mte)
        }
        return false
    }

    override fun useModularUI(): Boolean {
        return false
    }

    override fun clearHatches() {
        super.clearHatches()
        rotorHatches.clear()
    }

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(te, aTick)
        checkSpeedRotor(te, aTick)
    }

    override fun onRunningTick(aStack: ItemStack?): Boolean {
        if (speedRotor == KineticSpeed.STOP) stopMachine()
        return super.onRunningTick(aStack)
    }

    private fun checkSpeedRotor(te: IGregTechTileEntity, tick: Long) {
        if (te.isServerSide && tick of WorldTick.SECOND) {
            speedRotor = rotorHatches.firstOrNull()?.currentSpeed() ?: KineticSpeed.STOP
            if (speedRotor == KineticSpeed.STOP) stopMachine()
        }
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        if (speedRotor.speed > 0) {

            val tRecipe: GT_Recipe?

            if (mLockedToSingleRecipe && mSingleRecipeCheck != null) {
                if (!mSingleRecipeCheck.checkRecipeInputsSingleStack(true)) return false
                tRecipe = mSingleRecipeCheck.recipe
            } else {
                val tInputList = getStoredInputs()
                val tFluidList = getStoredFluids()
                val inputs = tInputList.toTypedArray<ItemStack>()
                val fluids = tFluidList.toTypedArray<FluidStack>()

                if (inputs.isEmpty() && fluids.isEmpty()) return false

                var tSingleRecipeCheckBuilder: GT_Single_Recipe_Check.Builder? = null

                if (mLockedToSingleRecipe) {
                    tSingleRecipeCheckBuilder = GT_Single_Recipe_Check.builder(this).setBefore(inputs, fluids)
                }

                tRecipe = recipeMap?.findRecipe(baseMetaTileEntity, false, false, 0, fluids, *inputs)

                if (tRecipe == null || !canOutputAll(tRecipe) || !tRecipe.isRecipeInputEqual(true, fluids, *inputs)) return false

                if (mLockedToSingleRecipe) {
                    mSingleRecipeCheck = tSingleRecipeCheckBuilder?.setAfter(inputs, fluids)?.setRecipe(tRecipe)?.build()
                }
            }

            mEfficiency = 10000
            mEfficiencyIncrease = 1000

            mMaxProgresstime = tRecipe!!.mDuration / speedRotor.speed
            mOutputItems = tRecipe.mOutputs
            mOutputFluids = tRecipe.mFluidOutputs
            updateSlots()
            return true
        }
        return false
    }

    override fun hasInput(): Boolean {
        return false
    }

    override fun hasOutput(): Boolean {
        return false
    }

    override fun currentSpeed(): KineticSpeed {
        return speedRotor
    }

    override fun changeSpeed(speed: KineticSpeed) {
        speedRotor = speed
    }

    override fun getWailaBody(itemStack: ItemStack, currentTip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler) {
        super.getWailaBody(itemStack, currentTip, accessor, config)
        wailaBody(currentTip, accessor)
    }

    override fun getWailaNBTData(player: EntityPlayerMP, tile: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
        wailaNBTData(tag)
    }

    override fun inputSide(): ForgeDirection {
        return baseMetaTileEntity.frontFacing
    }
}
