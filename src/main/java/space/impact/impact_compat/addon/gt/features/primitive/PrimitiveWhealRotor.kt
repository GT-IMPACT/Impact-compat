package space.impact.impact_compat.addon.gt.features.primitive

import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch
import gregtech.api.render.TextureFactory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.common.network.Network
import space.impact.impact_compat.addon.gt.util.world.GTWorldUtil
import space.impact.impact_compat.common.tiles.WaterWhealTE
import space.impact.impact_compat.common.tiles.WindWhealTE
import space.impact.packet_network.network.NetworkHandler.sendToAllAround

class PrimitiveWhealRotor : GT_MetaTileEntity_Hatch {

    private var isActive: Boolean = false

    constructor(aID: Int, aName: String)
            : super(aID, "compact.hatch.primitive_mill_rotor", aName, 0, 0, arrayOf(""))

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

    override fun onScrewdriverRightClick(side: ForgeDirection?, aPlayer: EntityPlayer?, aX: Float, aY: Float, aZ: Float) {
        super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ)
        val te = baseMetaTileEntity
        val pos = GTWorldUtil.vectorOffset(te, 0, 0, 1)
        GTWorldUtil.getTile<WaterWhealTE>(te, pos.x, pos.y, pos.z)?.also { wheal ->
            if (te.isServerSide) {
                wheal.isAnimated = !wheal.isAnimated
                wheal.sendToAllAround(Network.PacketUpdateWhealMill.transaction(wheal.isAnimated), 64)
            }
        }
        GTWorldUtil.getTile<WindWhealTE>(te, pos.x, pos.y, pos.z)?.also { wheal ->
            if (te.isServerSide) {
                wheal.isAnimated = !wheal.isAnimated
                wheal.sendToAllAround(Network.PacketUpdateWhealMill.transaction(wheal.isAnimated), 64)
            }
        }
    }
}
