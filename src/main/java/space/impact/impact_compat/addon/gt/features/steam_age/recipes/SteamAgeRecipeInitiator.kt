package space.impact.impact_compat.addon.gt.features.steam_age.recipes

import space.impact.impact_compat.addon.gt.features.steam_age.recipes.processing.addSteamAgeCompressorRecipes
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.processing.addSteamAgeCrusherRecipes
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.processing.addSteamAgeExtractorRecipes
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.processing.addSteamAgeForgeHammerRecipes

fun initSteamAgeRecipes() {
    addSteamAgeForgeHammerRecipes()
    addSteamAgeCrusherRecipes()
    addSteamAgeCompressorRecipes()
    addSteamAgeExtractorRecipes()
}
