package space.impact.impact_compat.common.util.forge

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.tileentity.TileEntity

object RegisterUtil {

    fun registerTE(clazz: Class<out TileEntity>, tileId: String) {
        GameRegistry.registerTileEntity(clazz, tileId)
    }
}
