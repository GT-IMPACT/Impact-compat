package space.impact.impact_compat.common.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import space.impact.impact_compat.common.tiles.models.SmelterContentModelTile

class SmelterContentBlock : ModelBlockBase("smelter_content", Material.wood, isDefaultSettings = false), ITileEntityProvider {
    companion object {
        val INSTANCE = SmelterContentBlock()
    }

    init {
        disableStats()
        setBlockUnbreakable()
        setResistance(6000000.0F)
    }

    override fun createNewTileEntity(w: World, meta: Int): TileEntity {
        return SmelterContentModelTile()
    }

    override fun getCollisionBoundingBoxFromPool(w: World?, x: Int, y: Int, z: Int): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(x - 1.5, y.toDouble(), z - 1.5, x + 1.5, y.toDouble(), z + 1.5)
    }
}


