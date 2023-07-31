package space.impact.impact_compat.addon.gt.features.steam_age.recipes

import gregtech.api.enums.GT_Values
import gregtech.api.enums.Materials
import gregtech.api.util.GT_RecipeBuilder

object SteamAgeForgeHammerRecipes {

    fun addRecipes() {
        GT_Values.RA.stdBuilder()
            .itemInputs(Materials.Steel.getIngots(2))
            .itemOutputs(Materials.Steel.getPlates(3))
            .noFluidInputs()
            .noFluidOutputs()
            .duration(GT_RecipeBuilder.MINUTES)
            .eut(0)
            .addTo(SteamForgeHammerRecipeMap)
    }
}