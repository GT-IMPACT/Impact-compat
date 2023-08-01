package space.impact.impact_compat.common.init.tileentities

import space.impact.impact_compat.common.tiles.NonTickableTileBlock
import space.impact.impact_compat.common.util.forge.RegisterUtil

fun registerTEDefault() {
    RegisterUtil.registerTE(NonTickableTileBlock::class.java, "NonTickableTileBlock")
}
