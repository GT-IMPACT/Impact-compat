package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.pipe.PrimitiveFluidPipe
import space.impact.impact_compat.addon.gt.items.CompatSingle
import space.impact.impact_compat.core.CLog

fun initSingles() {
    var offsetId = GTAddon.SINGLE_ID_OFFSET

    CompatSingle.PRIMITIVE_PIPE set PrimitiveFluidPipe(offsetId++, "primitive.pipe", "Primitive Pipe", 5, 3000).getStackForm(1)

    CLog.i("Finish Registered Singles. Last ID = $offsetId")
}
