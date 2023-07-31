package space.impact.impact_compat.addon.gt.features.steam_age.recipes

import gregtech.api.enums.Mods
import gregtech.api.util.GT_Recipe.GT_Recipe_Map_Fuel

private const val TEXTURES_GUI_BASIC_MACHINES = "textures/gui/basicmachines"

val SteamForgeHammerRecipeMap = GT_Recipe_Map_Fuel(
    HashSet(10),
    "impact.recipe.steamforgehammer",
    "Bronze Forge Hammer",
    null,
    Mods.GregTech.getResourcePath(TEXTURES_GUI_BASIC_MACHINES, "Default"),
    1,
    1,
    0,
    0,
    1,
    "",
    1000,
    "",
    true,
    false
)
