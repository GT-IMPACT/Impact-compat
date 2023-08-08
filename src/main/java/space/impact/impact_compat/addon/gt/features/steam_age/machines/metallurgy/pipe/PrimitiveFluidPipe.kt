package space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.pipe

import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Fluid
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of

class PrimitiveFluidPipe : GT_MetaPipeEntity_Fluid, IPrimitiveConnectPipe {

    constructor(aID: Int, aName: String, aNameRegional: String, aCapacity: Int, aHeatResistance: Int)
            : super(aID, aName, aNameRegional, 0.5f, null, aCapacity, aHeatResistance, false, 1)

    constructor(aName: String, aCapacity: Int, aHeatResistance: Int, aFluidTypes: Int)
            : super(aName, 0.5f, null, aCapacity, aHeatResistance, false, aFluidTypes)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): IMetaTileEntity {
        return PrimitiveFluidPipe(mName, mCapacity, mHeatResistance, mPipeAmount)
    }

    override fun getTexture(
        aBaseMetaTileEntity: IGregTechTileEntity?, side: ForgeDirection, aConnections: Int,
        colorIndex: Int, aConnected: Boolean, redstoneLevel: Boolean
    ): Array<ITexture> {
        if (side == ForgeDirection.UNKNOWN) return Textures.BlockIcons.ERROR_RENDERING
        if (mDisableInput.toInt() == 0) return arrayOf(
            if (aConnected) CompatTextures.PIPE_MEDIUM_PRIMITIVE.factory()
            else CompatTextures.CASE_PRIMITIVE_SMELTER.factory()
        )
        return arrayOf(
            if (aConnected) CompatTextures.PIPE_MEDIUM_PRIMITIVE.factory()
            else CompatTextures.CASE_PRIMITIVE_SMELTER.factory(),
        )
    }

    override fun getDescription(): Array<String> {
        return super.getDescription() + Tags.IMPACT_GREGTECH
    }

    override fun onWrenchRightClick(side: ForgeDirection?, wrenchingSide: ForgeDirection?, entityPlayer: EntityPlayer?, aX: Float, aY: Float, aZ: Float): Boolean {
        return false
    }

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(te, aTick)
        if (te.isServerSide && aTick of WorldTick.SECOND_3) {
            for (side in ForgeDirection.VALID_DIRECTIONS) {
                val tTileEntity = te.getTileEntityAtSide(side)
                if (canConnect(side, tTileEntity)) {
                    mConnections = (mConnections.toInt() or side.flag).toByte()
                } else {
                    disconnect(side)
                }
            }
        }
    }

    override fun canConnect(side: ForgeDirection, tileEntity: TileEntity?): Boolean {
        if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) return false
        if (tileEntity == null || baseMetaTileEntity == null) return false
        return tileEntity is IPrimitiveConnectPipe || (tileEntity as? IGregTechTileEntity)?.metaTileEntity is IPrimitiveConnectPipe
    }

    override fun isConnectedAtSide(side: ForgeDirection): Boolean {
        return if (side != ForgeDirection.UP && side != ForgeDirection.DOWN) super.isConnectedAtSide(side) else false
    }

    override fun connect(side: ForgeDirection): Int {
        return if (side != ForgeDirection.UP && side != ForgeDirection.DOWN) super.connect(side)
        else -1
    }
}

interface IPrimitiveConnectPipe
