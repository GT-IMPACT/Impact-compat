package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.gt.features.primitive.PrimitiveWhealRotor
import space.impact.impact_compat.addon.gt.features.steam_age.machines.hatch.PumpUnderwaterHatch
import space.impact.impact_compat.addon.gt.features.steam_age.machines.hatch.SteamRotorHatch
import space.impact.impact_compat.addon.gt.items.CompatHatches
import space.impact.impact_compat.core.CLog

fun initHatches() {
    var offsetId = GTAddon.HATCH_ID_OFFSET
    CompatHatches.PRIMITIVE_WHEAL_ROTOR.set(PrimitiveWhealRotor(offsetId++, "Primitive Wheal Rotor").getStackForm(1L))
    CompatHatches.STEAM_ROTOR_HATCH.set(SteamRotorHatch(offsetId++, "Machine Rotor Hatch").getStackForm(1L))
    CompatHatches.UNDERWATER_PUMP_HATCH.set(PumpUnderwaterHatch(offsetId++, "Underwater Pump Part").getStackForm(1L))

    CLog.i("Finish Registered Hatches. Last ID = $offsetId")
}
