package space.impact.impact_compat.addon.waila

import gregtech.api.interfaces.tileentity.IGregtechWailaProvider
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class CompactWailaDataProvider : IWailaDataProvider {

    override fun getWailaStack(accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): ItemStack? {
        return null
    }

    override fun getWailaHead(
        itemStack: ItemStack?, currenttip: List<String>, accessor: IWailaDataAccessor?,
        config: IWailaConfigHandler?
    ): List<String> {
        return currenttip
    }

    override fun getWailaBody(
        itemStack: ItemStack?, currenttip: List<String>, accessor: IWailaDataAccessor,
        config: IWailaConfigHandler?
    ): List<String> {
        val tile = accessor.tileEntity
        if (tile is IGregtechWailaProvider) {
            (tile as IGregtechWailaProvider).getWailaBody(itemStack, currenttip, accessor, config)
        }
        return currenttip
    }

    override fun getWailaTail(
        itemStack: ItemStack?, currenttip: List<String>, accessor: IWailaDataAccessor?,
        config: IWailaConfigHandler?
    ): List<String> {
        return currenttip
    }

    override fun getNBTData(
        player: EntityPlayerMP?, tile: TileEntity?, tag: NBTTagCompound,
        world: World?, x: Int, y: Int, z: Int
    ): NBTTagCompound {
        if (tile is IGregtechWailaProvider) {
            (tile as IGregtechWailaProvider).getWailaNBTData(player, tile, tag, world, x, y, z)
        }
        return tag
    }
}
