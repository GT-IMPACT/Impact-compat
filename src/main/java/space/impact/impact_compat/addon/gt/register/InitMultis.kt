package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.gt.features.steam_age.machines.MultiSteamKineticForgeHammer
import space.impact.impact_compat.addon.gt.items.CompatMultis
import space.impact.impact_compat.core.CLog

fun initMultis() {
    var offsetId = GTAddon.MULTI_ID_OFFSET
    CompatMultis.STEAM_KINETIC_FORGE_HAMMER.set(MultiSteamKineticForgeHammer(offsetId++).getStackForm(1))

    CLog.i("Finish Registered Multis. Last ID = $offsetId")
}
