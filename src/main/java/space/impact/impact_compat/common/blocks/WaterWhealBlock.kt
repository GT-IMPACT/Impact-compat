package space.impact.impact_compat.common.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import space.impact.impact_compat.common.init.tileentities.WaterWhealTE

class WaterWhealBlock : ModelBlockBase("water_wheal", Material.wood), ITileEntityProvider {

    companion object {
        val INSTANCE = WaterWhealBlock()
    }

    override fun createNewTileEntity(w: World, meta: Int): TileEntity {
        return WaterWhealTE()
    }
}
