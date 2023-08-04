package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.gt.features.steam_age.machines.generation.MultiSteamBoiler
import space.impact.impact_compat.addon.gt.features.steam_age.machines.processing.*
import space.impact.impact_compat.addon.gt.items.CompatMultis
import space.impact.impact_compat.core.CLog

fun initMultis() {
    var offsetId = GTAddon.MULTI_ID_OFFSET
    CompatMultis.STEAM_BRONZE_BOILER.set(MultiSteamBoiler(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_FORGE_HAMMER.set(MultiSteamKineticForgeHammer(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_CRUSHER.set(MultiSteamKineticCrusher(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_COMPRESSOR.set(MultiSteamKineticCompressor(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_EXTRACTOR.set(MultiSteamKineticExtractor(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_ORE_WASHER.set(MultiSteamKineticOreWasher(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_FURNACE.set(MultiSteamFurnace(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_CENTRIFUGE.set(MultiSteamKineticCentrifuge(offsetId++).getStackForm(1))
    CompatMultis.STEAM_KINETIC_SIFTER.set(MultiSteamKineticSifter(offsetId++).getStackForm(1))

    CLog.i("Finish Registered Multis. Last ID = $offsetId")
}
