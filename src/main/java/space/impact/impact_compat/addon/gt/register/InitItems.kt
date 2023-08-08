package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.items.CompatItems
import space.impact.impact_compat.core.CLog

fun initItems() {
    var offsetId = 0

    CompatItems.CRUCIBLE_SINGLE.set(offsetId++, "Crucible for Smelting")
    CompatItems.CRUCIBLE_MULTI.set(offsetId++, "Crucible for Smelting Alloy")

    CLog.i("Finish Registered Items Page 1. Last ID = $offsetId")
}
