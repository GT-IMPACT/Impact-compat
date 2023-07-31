package space.impact.impact_compat.addon.gt.features.steam_age.hatches

import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch
import gregtech.api.render.TextureFactory
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.features.steam_age.api.IKinetic
import space.impact.impact_compat.addon.gt.features.steam_age.api.KineticLogicStore
import space.impact.impact_compat.addon.gt.features.steam_age.api.KineticSpeed
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.core.NBT

class SteamRotorHatch : GT_MetaTileEntity_Hatch, IKinetic {

    companion object {
        private const val LOCAL_NAME = "compat.hatch.primitive_mill_rotor"
    }

    private var speed: KineticSpeed = KineticSpeed.STOP
    private val store = KineticLogicStore(this)

    constructor(aID: Int, aNameRegional: String)
            : super(aID, LOCAL_NAME, aNameRegional, 0, 4, arrayOf(Tags.IMPACT_GREGTECH, "Rotor Hatch for Multiblocks"))

    constructor(aName: String, aTier: Int, aDescription: Array<String>, aTextures: Array<Array<Array<ITexture?>?>?>)
            : super(aName, aTier, 4, aDescription, aTextures)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): MetaTileEntity {
        return SteamRotorHatch(mName, mTier.toInt(), mDescriptionArray, mTextures)
    }

    override fun getTexturesActive(aBaseTexture: ITexture): Array<ITexture> {
        return arrayOf(aBaseTexture, Textures.BlockIcons.OVERLAY_ME_INPUT_HATCH_ACTIVE.factory())
    }

    override fun getTexturesInactive(aBaseTexture: ITexture): Array<ITexture> {
        return arrayOf(aBaseTexture, Textures.BlockIcons.OVERLAY_ME_INPUT_HATCH.factory())
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, aFacing: ForgeDirection,
        colorIndex: Int, aActive: Boolean, redstoneLevel: Boolean
    ): Array<ITexture> {
        val base = CompatTextures.MACHINE_CASE_BRONZE.factory()
        if (side == aFacing) return if (aActive) getTexturesActive(base) else getTexturesInactive(base)
        return arrayOf(base)
    }

    override fun isSimpleMachine() = true
    override fun isAccessAllowed(aPlayer: EntityPlayer?) = true
    override fun isFacingValid(facing: ForgeDirection) = true
    override fun isOutputFacing(side: ForgeDirection) = side == baseMetaTileEntity.frontFacing

    override fun hasInput(): Boolean = true
    override fun hasOutput(): Boolean = false

    override fun inputSide(): ForgeDirection = baseMetaTileEntity.frontFacing

    override fun currentSpeed(): KineticSpeed = speed

    override fun changeSpeed(speed: KineticSpeed) {
        this.speed = speed
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        speed = KineticSpeed.typeOf(aNBT.getInteger(NBT.NBT_SPEED))
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        aNBT.setInteger(NBT.NBT_SPEED, speed.speed)
    }

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(te, aTick)
        store.injectSpeedFromRotor(te, aTick)
    }

    override fun getWailaBody(itemStack: ItemStack, currenttip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler) {
        super.getWailaBody(itemStack, currenttip, accessor, config)
        wailaBody(currenttip, accessor)
    }

    override fun getWailaNBTData(player: EntityPlayerMP, tile: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) {
        wailaNBTData(tag)
    }
}
