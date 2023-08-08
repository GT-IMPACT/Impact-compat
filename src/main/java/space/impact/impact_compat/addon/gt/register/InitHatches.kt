package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.gt.features.primitive.PrimitiveWhealRotor
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveInputBus
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveInputHatch
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveOutputBus
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveOutputHatch
import space.impact.impact_compat.addon.gt.features.steam_age.machines.processing.hatch.SteamRotorHatch
import space.impact.impact_compat.addon.gt.features.steam_age.machines.pump.hatch.PumpUnderwaterHatch
import space.impact.impact_compat.addon.gt.items.CompatHatches
import space.impact.impact_compat.core.CLog

fun initHatches() {
    var offsetId = GTAddon.HATCH_ID_OFFSET
    CompatHatches.PRIMITIVE_WHEAL_ROTOR set PrimitiveWhealRotor(offsetId++, "Primitive Wheal Rotor").getStackForm(1L)
    CompatHatches.STEAM_ROTOR_HATCH set SteamRotorHatch(offsetId++, "Machine Rotor Hatch").getStackForm(1L)
    CompatHatches.UNDERWATER_PUMP_HATCH set PumpUnderwaterHatch(offsetId++, "Underwater Pump Part").getStackForm(1L)

    CompatHatches.PRIMITIVE_IN_BUS set PrimitiveInputBus(offsetId++, "Primitive Input Bus").getStackForm(1L)
    CompatHatches.PRIMITIVE_OUT_BUS set PrimitiveOutputBus(offsetId++, "Primitive Output Bus").getStackForm(1L)
    CompatHatches.PRIMITIVE_IN_HATCH set PrimitiveInputHatch(offsetId++, "Primitive Input Hatch").getStackForm(1L)
    CompatHatches.PRIMITIVE_OUT_HATCH set PrimitiveOutputHatch(offsetId++, "Primitive Output Hatch").getStackForm(1L)

    CLog.i("Finish Registered Hatches. Last ID = $offsetId")
}
