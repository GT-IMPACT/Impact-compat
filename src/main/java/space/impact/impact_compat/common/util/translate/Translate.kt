package space.impact.impact_compat.common.util.translate

import net.minecraft.util.StatCollector

object Translate {
    fun String.translate(): String {
        return StatCollector.translateToLocal(this)
    }

    fun String.translate(separate: String, nextKey: String): String {
        return "${StatCollector.translateToLocal(this)}${separate}${StatCollector.translateToLocal(nextKey)}"
    }
}
