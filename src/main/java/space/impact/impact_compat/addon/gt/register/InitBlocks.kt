package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeBlocks
import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeGlassBlocks
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.common.blocks.SteamRotorBlock
import space.impact.impact_compat.common.blocks.WaterWhealBlock
import space.impact.impact_compat.common.blocks.WindWhealBlock

fun initBlocks() {
    initBlockModels()
    initStructureBlocks()
}

private fun initBlockModels() {
    CompatBlocks.WATER_WHEAL_BLOCK.set(WaterWhealBlock.INSTANCE)
    CompatBlocks.WIND_WHEAL_BLOCK.set(WindWhealBlock.INSTANCE)
    CompatBlocks.STEAM_ROTOR_BLOCK.set(SteamRotorBlock.INSTANCE)
}

private fun initStructureBlocks() {
    CompatBlocks.BRONZE_MACHINE_CASING.set(SteamAgeBlocks.INSTANCE.register(SteamAgeBlocks.META_BRONZE_MACHINE_CASING))
    CompatBlocks.BRONZE_FIREBOX_CASING.set(SteamAgeBlocks.INSTANCE.register(SteamAgeBlocks.META_BRONZE_FIREBOX_CASING))
    CompatBlocks.BRONZE_GLASS_CASING.set(SteamAgeGlassBlocks.INSTANCE.register(SteamAgeGlassBlocks.META_BRONZE_GLASS_MACHINE_CASING))
}
