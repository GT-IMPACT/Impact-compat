package space.impact.impact_compat.addon.gt.features.steam_age.machines.pump.hatch

import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.translate.Translate.translate
import space.impact.impact_compat.core.NBT

class PumpUnderwaterHatch : GT_MetaTileEntity_Hatch {

    companion object {
        private const val LOCAL_NAME = "compat.hatch.underwater_pump"
        private val WAILA_INFO = "waila.underwater_hatch_problem".translate()
    }

    var isDoneToWork: Boolean = false
        private set

    constructor(aID: Int, aNameRegional: String)
            : super(aID, LOCAL_NAME, aNameRegional, 0, 4, arrayOf(Tags.IMPACT_GREGTECH))

    constructor(aName: String, aTier: Int, aDescription: Array<String>, aTextures: Array<Array<Array<ITexture?>?>?>)
            : super(aName, aTier, 4, aDescription, aTextures)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): MetaTileEntity {
        return PumpUnderwaterHatch(mName, mTier.toInt(), mDescriptionArray, mTextures)
    }

    override fun getTexturesActive(aBaseTexture: ITexture): Array<ITexture> {
        return arrayOf()
    }

    override fun getTexturesInactive(aBaseTexture: ITexture): Array<ITexture> {
        return arrayOf()
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, aFacing: ForgeDirection,
        colorIndex: Int, aActive: Boolean, redstoneLevel: Boolean
    ): Array<ITexture> {
        val base = CompatTextures.CASE_MACHINE_BRONZE.factory()
        if (side == aFacing) return arrayOf(base, Textures.BlockIcons.OVERLAY_PUMP.factory())
        if (side.ordinal >= 2) return arrayOf(base, Textures.BlockIcons.OVERLAY_DRAIN.factory())
        return arrayOf(base)
    }

    override fun onFirstTick(te: IGregTechTileEntity) {
        super.onFirstTick(te)
        if (te.isServerSide) {
            if (te.biome == BiomeGenBase.river || te.biome == BiomeGenBase.ocean ||
                te.biome == BiomeGenBase.deepOcean || te.biome == BiomeGenBase.swampland
            ) {
                isDoneToWork = true
                for (side in ForgeDirection.values()) {
                    if (side == ForgeDirection.UP || side == ForgeDirection.DOWN || side == ForgeDirection.UNKNOWN) continue
                    val block = te.getBlockAtSide(side)
                    isDoneToWork = block == Blocks.water || block == Blocks.flowing_water
                    if (!isDoneToWork) break
                }
            }
        }
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        aNBT.setBoolean(NBT.NBT_DONE_TO_WORK, isDoneToWork)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        isDoneToWork = aNBT.getBoolean(NBT.NBT_DONE_TO_WORK)
    }

    override fun isLiquidInput(side: ForgeDirection?): Boolean = false
    override fun isLiquidOutput(side: ForgeDirection?): Boolean = side == ForgeDirection.UP
    override fun isSimpleMachine() = true
    override fun isAccessAllowed(aPlayer: EntityPlayer?) = true
    override fun isFacingValid(facing: ForgeDirection) = facing == ForgeDirection.UP
    override fun isOutputFacing(side: ForgeDirection) = side == baseMetaTileEntity.frontFacing
    override fun getCapacity(): Int = 8000 * (1 shl mTier.toInt())

    override fun getWailaBody(itemStack: ItemStack, currentTip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler) {
        super.getWailaBody(itemStack, currentTip, accessor, config)
        val tag = accessor.nbtData
        val isDoneToWork = tag.getBoolean(NBT.NBT_DONE_TO_WORK)
        if (!isDoneToWork) currentTip.add(EnumChatFormatting.RED.toString() + WAILA_INFO)
    }

    override fun getWailaNBTData(player: EntityPlayerMP, tile: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
        tag.setBoolean(NBT.NBT_DONE_TO_WORK, isDoneToWork)
    }
}
