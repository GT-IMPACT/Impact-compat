@file:Suppress("LeakingThis")

package space.impact.impact_compat.common.item.tool

import net.minecraft.item.ItemStack
import space.impact.impact_compat.common.item.GenericItem

open class ToolBaseItem(
    unLocalName: String, englishName: String?, englishTooltip: String?,
    needTooltipToLang: Boolean = englishTooltip != null,
) : GenericItem(unLocalName, englishName, englishTooltip, needTooltipToLang) {

    fun register(): ItemStack = ItemStack(this, 1)
}
