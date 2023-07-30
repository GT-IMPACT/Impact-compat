package space.impact.impact_compat.addon.gt.features.primitive

import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch
import gregtech.api.render.TextureFactory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.util.world.GTWorldUtil
import space.impact.impact_compat.common.network.Network
import space.impact.impact_compat.common.tiles.BaseTileRotationEntityModel
import space.impact.impact_compat.common.tiles.WaterWhealTE
import space.impact.impact_compat.common.tiles.WindWhealTE
import space.impact.impact_compat.core.NBT
import space.impact.impact_compat.core.Strings
import space.impact.impact_compat.core.WorldAround
import space.impact.impact_compat.core.WorldTick
import space.impact.packet_network.network.NetworkHandler.sendToAllAround

class PrimitiveWhealRotor : GT_MetaTileEntity_Hatch {

    companion object {
        private const val LOCAL_NAME = "compact.hatch.primitive_mill_rotor"
        private const val TARGET_MAX_AIR_BLOCKS = 64 //TODO
        private const val TARGET_MAX_WATER_BLOCKS = 64 //TODO
    }

    var active: Boolean = false
        private set
    private var lastActive: Boolean = false
    private var wheal: BaseTileRotationEntityModel? = null
    private var blocksCountToStart: Int = 0

    constructor(aID: Int, aName: String)
            : super(aID, LOCAL_NAME, aName, 0, 0, arrayOf(Strings.E))

    constructor(aName: String, aTier: Int, aDescription: Array<String>, aTextures: Array<Array<Array<ITexture?>?>?>?)
            : super(aName, aTier, 0, aDescription, aTextures)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): IMetaTileEntity =
        PrimitiveWhealRotor(mName, mTier.toInt(), mDescriptionArray, mTextures)

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, face: ForgeDirection,
        color: Int, aActive: Boolean, redstone: Boolean
    ): Array<ITexture?> {
        return if (side == face) arrayOf(TextureFactory.of(Blocks.planks, 1), TextureFactory.of(Textures.BlockIcons.OVERLAY_ENERGY_IN_POWER))
        else arrayOf(TextureFactory.of(Blocks.planks, 1))
    }

    override fun getTexturesActive(aBaseTexture: ITexture?): Array<ITexture> = arrayOf()
    override fun getTexturesInactive(aBaseTexture: ITexture?): Array<ITexture> = arrayOf()
    override fun isSimpleMachine() = true
    override fun isAccessAllowed(aPlayer: EntityPlayer?) = true
    override fun isFacingValid(facing: ForgeDirection) = true
    override fun isOutputFacing(side: ForgeDirection) = side == baseMetaTileEntity.frontFacing

    override fun saveNBTData(data: NBTTagCompound) {
        super.saveNBTData(data)
        data.setBoolean(NBT.NBT_ACTIVE, active)
        data.setInteger(NBT.NBT_BLOCKS_COUNT, blocksCountToStart)
    }

    override fun loadNBTData(data: NBTTagCompound) {
        super.loadNBTData(data)
        active = data.getBoolean(NBT.NBT_ACTIVE)
        blocksCountToStart = data.getInteger(NBT.NBT_BLOCKS_COUNT)
    }

    override fun onRemoval() {
        if (baseMetaTileEntity?.isServerSide == true) {
            active = false
            handleWheal()
        }
        super.onRemoval()
    }

    private fun countBlocks(te: IGregTechTileEntity): Int {
        if (blocksCountToStart > 0) return blocksCountToStart
        val biome = te.world.getBiomeGenForCoordsBody(te.xCoord, te.zCoord)
        var count = 0
        val block = when (wheal) {
            is WaterWhealTE -> if (biome != BiomeGenBase.river) return 0 else Blocks.water
            is WindWhealTE -> Blocks.air
            else -> return 0
        }
        for (xx in -8..8) {
            for (zz in -8..8) {
                for (yy in -8..8) {
                    if (te.getBlockOffset(xx, yy, zz) == block) count++
                }
            }
        }
        return count
    }

    override fun onPostTick(te: IGregTechTileEntity, tick: Long) {
        super.onPostTick(te, tick)
        if (te.isServerSide) {
            if (wheal != null && lastActive != active) {
                lastActive = active; handleWheal()
            }
            if (tick % WorldTick.SECOND == WorldTick.ZERO) {
                if (wheal == null) findWheal(te)
                else {
                    checkAround(te, wheal!!)
                    blocksCountToStart = countBlocks(te)
                }
            }
        }
    }

    private fun checkAround(te: IGregTechTileEntity, wheal: BaseTileRotationEntityModel) {
        when (wheal) {
            is WaterWhealTE -> {
                var isActive = true
                for (xx in -4..4) {
                    val vec = GTWorldUtil.vectorOffset(te, xx, -1, -1)
                    val block = GTWorldUtil.getBlockOffset(te, vec)
                    if (block != Blocks.water) isActive = false
                }
                active = isActive && blocksCountToStart >= TARGET_MAX_WATER_BLOCKS
            }

            is WindWhealTE -> {
                var isActive = true
                for (xx in -4..4) {
                    for (yy in -4..4) {
                        if (xx == 0 && yy == 0) continue
                        val vec = GTWorldUtil.vectorOffset(te, xx, yy, -1)
                        val block = GTWorldUtil.getBlockOffset(te, vec)
                        if (block != Blocks.air) isActive = false
                    }
                }
                active = isActive && blocksCountToStart >= TARGET_MAX_AIR_BLOCKS
            }
        }
    }

    private fun findWheal(te: IGregTechTileEntity) {
        wheal = GTWorldUtil.getTileOffset(te, GTWorldUtil.vectorOffset(te, 0, 0, -1))
    }

    private fun handleWheal() {
         wheal?.sendToAllAround(Network.PacketUpdateModelAnimate.transaction(active), WorldAround.CHUNK_4)
    }
}
