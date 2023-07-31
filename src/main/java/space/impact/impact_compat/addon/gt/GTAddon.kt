package space.impact.impact_compat.addon.gt

import space.impact.impact_compat.addon.gt.features.steam_age.recipes.SteamAgeForgeHammerRecipes
import space.impact.impact_compat.addon.gt.register.*

object GTAddon {

    const val SINGLE_ID_OFFSET = 14_000
    const val MULTI_ID_OFFSET = 16_000
    const val HATCH_ID_OFFSET = 18_000

    fun init() {
        initBlocks()
        initItems()
        initHatches()
        initSingles()
        initMultis()
    }

    fun postInit() {
        SteamAgeForgeHammerRecipes.addRecipes()
    }
}
