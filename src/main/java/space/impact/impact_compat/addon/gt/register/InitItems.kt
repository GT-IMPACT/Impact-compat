@file:Suppress("SameParameterValue")

package space.impact.impact_compat.addon.gt.register

import gregtech.api.GregTech_API
import gregtech.api.objects.GT_HashSet
import gregtech.api.objects.GT_ItemStack
import gregtech.api.util.GT_Utility
import net.minecraft.item.ItemStack
import space.impact.impact_compat.addon.gt.items.CompatItems
import space.impact.impact_compat.common.item.tool.ToolForgeHammer
import space.impact.impact_compat.common.item.tool.ToolWrenchUniversal
import space.impact.impact_compat.core.CLog

fun initItems() {
    var offsetId = 0

    CompatItems.CRUCIBLE_SINGLE.set(offsetId++, "Crucible for Smelting")
    CompatItems.CRUCIBLE_MULTI.set(offsetId++, "Crucible for Smelting Alloy")

    CompatItems.TOOL_UNIVERSAL_WRENCH set ToolWrenchUniversal.INSTANCE.register()
    CompatItems.TOOL_FORGE_HAMMER set ToolForgeHammer.INSTANCE.register()

    CLog.i("Finish Registered Items. Last ID = $offsetId")
    registerToolGT()
}

private fun registerToolGT() {
    registerGTTool(CompatItems.TOOL_UNIVERSAL_WRENCH.getWildcard(1), GregTech_API.sWrenchList)
}

private fun registerGTTool(stack: ItemStack?, toolList: GT_HashSet<GT_ItemStack>) {
    if (stack == null) return
    val tool = GT_ItemStack(GT_Utility.copyAmount(1, stack))
    if (!GregTech_API.sToolList.contains(tool)) {
        toolList.add(tool)
        GregTech_API.sToolList.add(tool)
    }
}
