package space.impact.impact_compat.addon.gt.util.world

import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import net.minecraft.tileentity.TileEntity
import space.impact.impact_compat.common.util.world.Vec3

object GTWorldUtil {

    fun vectorOffset(te: IGregTechTileEntity, x: Int, y: Int, z: Int): Vec3 {
        val forgeDirection = Vec3(te.backFacing.offsetX, te.backFacing.offsetY, te.backFacing.offsetZ)
        val offset = Vec3.create()
        if (forgeDirection.x == 0 && forgeDirection.z == -1) {
            offset.x = x; offset.y = y; offset.z = z
        }
        if (forgeDirection.x == 0 && forgeDirection.z == 1) {
            offset.x = -x; offset.y = y; offset.z = -z
        }
        if (forgeDirection.x == -1 && forgeDirection.z == 0) {
            offset.x = z; offset.y = y; offset.z = -x
        }
        if (forgeDirection.x == 1 && forgeDirection.z == 0) {
            offset.x = -z; offset.y = y; offset.z = x
        }
        if (forgeDirection.y == -1) {
            offset.x = x; offset.y = z; offset.z = y
        }
        return offset
    }

    inline fun <reified T : TileEntity> getTile(gte: IGregTechTileEntity?, x: Int, y: Int, z: Int): T? {
        return gte?.getTileEntity(x + gte.xCoord, y + gte.yCoord, z + gte.zCoord) as? T
    }

    inline fun <reified T : IMetaTileEntity> getMTile(gte: IGregTechTileEntity?, x: Int, y: Int, z: Int): T? {
        return gte?.getIGregTechTileEntity(x + gte.xCoord, y + gte.yCoord, z + gte.zCoord)?.metaTileEntity as? T
    }
}
