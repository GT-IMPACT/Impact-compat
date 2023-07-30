package space.impact.impact_compat.common.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import space.impact.impact_compat.common.tiles.special.SteamRotorTE

class SteamRotorBlock : ModelBlockBase("steam_rotor", Material.wood), ITileEntityProvider {

    companion object {
        val INSTANCE = SteamRotorBlock()
    }

    override fun createNewTileEntity(w: World, meta: Int): TileEntity {
        return SteamRotorTE()
    }
}
