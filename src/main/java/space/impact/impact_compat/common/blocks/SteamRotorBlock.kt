package space.impact.impact_compat.common.blocks

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import software.bernie.geckolib3.item.GeoItemBlock
import space.impact.impact_compat.common.tiles.models.SteamRotorModelTile
import space.impact.impact_compat.common.util.translate.Translate.translate

class SteamRotorBlock : ModelBlockBase("steam_rotor", Material.wood, SteamRotorItemBlock::class.java), ITileEntityProvider {

    companion object {
        val INSTANCE = SteamRotorBlock()
    }

    override fun createNewTileEntity(w: World, meta: Int): TileEntity {
        return SteamRotorModelTile()
    }

    class SteamRotorItemBlock(block: Block) : GeoItemBlock(block) {
        @SideOnly(Side.CLIENT)
        override fun addInformation(stack: ItemStack?, player: EntityPlayer?, tt: MutableList<Any?>, f3: Boolean) {
            super.addInformation(stack, player, tt, f3)
            tt.add(String.format("impact.tt.steam.rotor.0".translate(), "${SteamRotorModelTile.LOW_BOUND}"))
            tt.add(String.format("impact.tt.steam.rotor.1".translate(), "${SteamRotorModelTile.MEDIUM_BOUND}"))
            tt.add(String.format("impact.tt.steam.rotor.2".translate(), "${SteamRotorModelTile.HIGH_BOUND}"))
        }
    }
}
