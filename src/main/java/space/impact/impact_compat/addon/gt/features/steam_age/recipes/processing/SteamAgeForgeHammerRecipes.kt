package space.impact.impact_compat.addon.gt.features.steam_age.recipes.processing

import gregtech.api.enums.GT_Values
import gregtech.api.enums.Materials
import gregtech.api.enums.OrePrefixes
import gregtech.api.util.GT_OreDictUnificator
import gregtech.api.util.GT_RecipeBuilder
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.STEAM_FORGE_HAMMER_RECIPE_MAP

fun addSteamAgeForgeHammerRecipes() {
    GT_Values.RA.stdBuilder()
        .itemInputs(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 3))
        .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 2))
        .noFluidInputs()
        .noFluidOutputs()
        .duration(GT_RecipeBuilder.MINUTES)
        .eut(0)
        .addTo(STEAM_FORGE_HAMMER_RECIPE_MAP)
}
