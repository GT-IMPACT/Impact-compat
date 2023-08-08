package space.impact.impact_compat.addon.gt.features.steam_age.recipes

import gregtech.api.enums.Mods
import gregtech.api.util.GT_Recipe
import gregtech.api.util.GT_Recipe.GT_Recipe_Map_Fuel

private const val TEXTURES_GUI_BASIC_MACHINES = "textures/gui/basicmachines"
private const val DEFAULT_TEXTURE = "Default"
private const val EMPTY = ""

val STEAM_FORGE_HAMMER_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.forge_hammer", "Steam Bronze Forge Hammer",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, false
)

val STEAM_CRUSHER_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.crusher", "Steam Bronze Crusher",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val STEAM_COMPRESSOR_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.compressor", "Steam Bronze Compressor",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val STEAM_EXTRACTOR_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.extractor", "Steam Bronze Extractor",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val STEAM_ORE_WASHER_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.ore_washer", "Steam Bronze Ore Washer",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val STEAM_CENTRIFUGE_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.centrifuge", "Steam Bronze Centrifuge",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val STEAM_SIFTER_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.steam.sifter", "Steam Bronze Sifting Machine",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val PRIMITIVE_SMELTER_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.primitive.smelter", "Primitive Smelter",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    3, 0, 1, 0, 1,
    EMPTY, 0, EMPTY, false, true
)

val PRIMITIVE_CASTER_RECIPE_MAP = GT_Recipe.GT_Recipe_Map(
    HashSet(10), "impact.recipe.primitive.caster", "Primitive Caster",
    null, Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, DEFAULT_TEXTURE),
    1, 1, 0, 1, 1,
    EMPTY, 0, EMPTY, false, true
)