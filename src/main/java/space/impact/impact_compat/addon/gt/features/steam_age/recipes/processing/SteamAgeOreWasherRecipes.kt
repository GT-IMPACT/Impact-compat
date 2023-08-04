package space.impact.impact_compat.addon.gt.features.steam_age.recipes.processing

import gregtech.api.enums.GT_Values
import gregtech.api.enums.Materials
import gregtech.api.enums.OrePrefixes
import gregtech.api.util.GT_OreDictUnificator
import gregtech.api.util.GT_RecipeBuilder
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.STEAM_FORGE_HAMMER_RECIPE_MAP
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.STEAM_ORE_WASHER_RECIPE_MAP

fun addSteamAgeOreWasherRecipes() {
    GT_Values.RA.stdBuilder()
        .itemInputs(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 3))
        .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 2))
        .fluidInputs(Materials.Water.getFluid(100))
        .fluidOutputs(Materials.Water.getFluid(20))
        .duration(GT_RecipeBuilder.MINUTES)
        .eut(0)
        .addTo(STEAM_ORE_WASHER_RECIPE_MAP)
}
