package space.impact.impact_compat.common.util.forge

import cpw.mods.fml.client.registry.ClientRegistry
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.MinecraftForgeClient

object RenderModelUtil {

    fun registerRenderModel(
        teClass: Class<out TileEntity>? = null,
        renderTE: TileEntitySpecialRenderer? = null,
        item: Item? = null,
        renderItem: IItemRenderer? = null,
    ) {
        if (item != null && renderItem != null)
            MinecraftForgeClient.registerItemRenderer(item, renderItem)
        if (teClass != null && renderTE != null)
            ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderTE)
    }
}
