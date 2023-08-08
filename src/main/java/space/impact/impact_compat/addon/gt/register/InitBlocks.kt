package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeBlocks
import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeGlassBlocks
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.common.blocks.SmelterContentBlock
import space.impact.impact_compat.common.blocks.SteamRotorBlock
import space.impact.impact_compat.common.blocks.WaterWhealBlock
import space.impact.impact_compat.common.blocks.WindWhealBlock
import space.impact.impact_compat.common.tiles.models.SmelterContentModelTile
import space.impact.impact_compat.common.tiles.models.SteamRotorModelTile
import space.impact.impact_compat.common.tiles.models.WaterRotorModelTile
import space.impact.impact_compat.common.tiles.models.WindRotorModelTile

fun initBlocks() {
    initBlockModels()
    initStructureBlocks()
}

private fun initBlockModels() {
    CompatBlocks.WATER_WHEAL_BLOCK set WaterRotorModelTile("water_wheal").getStackForm(WaterWhealBlock.INSTANCE)
    CompatBlocks.WIND_WHEAL_BLOCK set WindRotorModelTile("wind_wheal").getStackForm(WindWhealBlock.INSTANCE)
    CompatBlocks.STEAM_ROTOR_BLOCK set SteamRotorModelTile("steam_rotor").getStackForm(SteamRotorBlock.INSTANCE)
    CompatBlocks.SMELTER_CONTENT_BLOCK set SmelterContentModelTile("smelter_content").getStackForm(SmelterContentBlock.INSTANCE)
}

private fun initStructureBlocks() {
    CompatBlocks.BRONZE_MACHINE_CASING set SteamAgeBlocks.INSTANCE.register(CompatBlocks.BRONZE_MACHINE_CASING.meta)
    CompatBlocks.BRONZE_FIREBOX_CASING set SteamAgeBlocks.INSTANCE.register(CompatBlocks.BRONZE_FIREBOX_CASING.meta)
    CompatBlocks.BRONZE_BRICK_CASING set SteamAgeBlocks.INSTANCE.register(CompatBlocks.BRONZE_BRICK_CASING.meta)
    CompatBlocks.PRIMITIVE_SMELTER_CASING set SteamAgeBlocks.INSTANCE.register(CompatBlocks.PRIMITIVE_SMELTER_CASING.meta)

    CompatBlocks.BRONZE_GLASS_CASING set SteamAgeGlassBlocks.INSTANCE.register(CompatBlocks.BRONZE_GLASS_CASING.meta)
}
