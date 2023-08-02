package space.impact.impact_compat.addon.gt.features.steam_age.recipes

import gregtech.api.enums.Mods
import gregtech.api.util.GT_Recipe.GT_Recipe_Map_Fuel

private const val TEXTURES_GUI_BASIC_MACHINES = "textures/gui/basicmachines"

val STEAM_FORGE_HAMMER_RECIPE_MAP = GT_Recipe_Map_Fuel(
    HashSet(10), "impact.recipe.steam.forge_hammer", "Steam Bronze Forge Hammer",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, "Default"),
    1, 1, 0, 0, 1,
    "", 0, "", false, false
)

val STEAM_CRUSHER_RECIPE_MAP = GT_Recipe_Map_Fuel(
    HashSet(10), "impact.recipe.steam.crusher", "Steam Bronze Crusher",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, "Default"),
    1, 1, 0, 0, 1,
    "", 0, "", false, true
)

val STEAM_COMPRESSOR_RECIPE_MAP = GT_Recipe_Map_Fuel(
    HashSet(10), "impact.recipe.steam.compressor", "Steam Bronze Compressor",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, "Default"),
    1, 1, 0, 0, 1,
    "", 0, "", false, true
)

val STEAM_EXTRACTOR_RECIPE_MAP = GT_Recipe_Map_Fuel(
    HashSet(10), "impact.recipe.steam.extractor", "Steam Bronze Extractor",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, "Default"),
    1, 1, 0, 0, 1,
    "", 0, "", false, true
)
