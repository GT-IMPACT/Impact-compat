package space.impact.impact_compat.addon.gt.util.world

import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import space.impact.impact_compat.common.util.world.Vec3

@Suppress("unused")
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

    fun vectorOffset(te: IGregTechTileEntity, vec: Vec3): Vec3 {
        return vectorOffset(te, vec.x, vec.y, vec.z)
    }

    fun getBlockOffset(gte: IGregTechTileEntity, pos: Vec3): Block? {
        return gte.getBlockOffset(pos.x, pos.y, pos.z)
    }

    fun setBlockOffset(gte: IGregTechTileEntity, pos: Vec3, block: Block, meta: Int = 0, flagBlock: Int = 3) {
        gte.world.setBlock(pos.x + gte.xCoord, pos.y + gte.yCoord, pos.z + gte.zCoord, block, meta, flagBlock)
    }

    inline fun <reified T : TileEntity> getTileOffset(gte: IGregTechTileEntity?, x: Int, y: Int, z: Int): T? {
        return gte?.getTileEntityOffset(x, y, z) as? T
    }

    inline fun <reified T : TileEntity> getTileOffset(gte: IGregTechTileEntity?, pos: Vec3): T? {
        return gte?.getTileEntityOffset(pos.x, pos.y, pos.z) as? T
    }

    inline fun <reified T : IMetaTileEntity> getMTile(gte: IGregTechTileEntity?, x: Int, y: Int, z: Int): T? {
        return gte?.getIGregTechTileEntity(x + gte.xCoord, y + gte.yCoord, z + gte.zCoord)?.metaTileEntity as? T
    }

    inline fun <reified T : IMetaTileEntity> getMTile(gte: IGregTechTileEntity?, pos: Vec3): T? {
        return gte?.getIGregTechTileEntityOffset(pos.x, pos.y, pos.z)?.metaTileEntity as? T
    }

    fun createAABBOffset(gte: IGregTechTileEntity, offset: Vec3, xzRange: Int, yRange: Int): AxisAlignedBB {
        val vec = vectorOffset(gte, offset).let {
            Vec3(it.x + gte.xCoord, it.y + gte.yCoord, it.z + gte.zCoord)
        }
        return createAABB(
            vec.x - xzRange, vec.y - yRange, vec.z - xzRange,
            vec.x + xzRange, vec.y + yRange, vec.z + xzRange,
        )
    }

    fun createAABB(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(x1 + .5, y1 + .5, z1 + .5, x2 + .5, y2 + .5, z2 + .5)
    }
}
