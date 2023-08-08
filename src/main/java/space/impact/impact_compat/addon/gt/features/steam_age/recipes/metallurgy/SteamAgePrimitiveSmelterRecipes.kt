package space.impact.impact_compat.addon.gt.features.steam_age.recipes.metallurgy

import gregtech.api.enums.GT_Values
import gregtech.api.enums.ItemList
import gregtech.api.util.GT_RecipeBuilder
import net.minecraft.item.ItemStack
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.PRIMITIVE_CASTER_RECIPE_MAP
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.PRIMITIVE_SMELTER_RECIPE_MAP
import space.impact.impact_compat.addon.gt.items.CompatItems
import space.impact.impact_compat.common.item.materials.GeneratedMaterials
import space.impact.impact_compat.common.item.materials.api.CompatMaterial
import space.impact.impact_compat.common.item.materials.api.OreDictionary
import space.impact.impact_compat.common.item.materials.api.SubTags


private const val COUNT_ORE = 1
private const val COUNT_MOLTEN = 144
private const val ZERO_EU = 0
private const val DURATION_SMELTER = GT_RecipeBuilder.SECONDS * 20
private const val DURATION_CASTER = GT_RecipeBuilder.SECONDS * 5

enum class MoldRecipe(val stack: ItemStack, val prefix: OreDictionary, val amountMolten: Int) {
    INGOT(ItemList.Shape_Mold_Ingot.get(0), OreDictionary.ingot, 144),
//    GEAR(ItemList.Shape_Mold_Gear.get(0), OreDictionary.gearGt, 576),
//    GEAR_SMALL(ItemList.Shape_Mold_Gear_Small.get(0), OreDictionary.gearGtSmall, 144),
//    ROD(ItemList.Shape_Mold_Rod.get(0), OreDictionary.rod, 72),
//    ROTOR(ItemList.Shape_Mold_Rotor.get(0), OreDictionary.rotor, 720),
}

fun addSteamAgePrimitiveSmelterRecipes() {
    for (mat in CompatMaterial.getAll()) {
        val isSmelter = mat.subTags.contains(SubTags.SMELTER)
        val isCaster = mat.subTags.contains(SubTags.CASTER)
        if (isSmelter || isCaster) {
            val ore = mat.get(OreDictionary.crushed, COUNT_ORE)
            if (mat.mStandardMoltenFluid != null) {

                if (isSmelter && ore != null) addSmelterMaterialRecipe(
                    inputs = arrayOf(mat to 1),
                    outPut = mat to 144,
                )

                if (isCaster) for (mold in MoldRecipe.values()) {
                    val stack = mat.get(mold.prefix, COUNT_ORE)
                    if (stack != null) GT_Values.RA.stdBuilder()
                        .itemInputs(mold.stack)
                        .itemOutputs(stack)
                        .noFluidOutputs()
                        .fluidInputs(mat.getMolten(mold.amountMolten))
                        .duration(DURATION_CASTER)
                        .eut(ZERO_EU)
                        .noOptimize()
                        .addTo(PRIMITIVE_CASTER_RECIPE_MAP)
                }
            }
        }
    }

    addSmelterMaterialRecipe(
        inputs = arrayOf(GeneratedMaterials.COPPER to 3, GeneratedMaterials.TIN to 1),
        outPut = GeneratedMaterials.BRONZE to 576,
    )
}

fun addSmelterMaterialRecipe(outPut: Pair<CompatMaterial, Int>, vararg inputs: Pair<CompatMaterial, Int>) {
    val molten = outPut.first.getMolten(outPut.second)
    if (molten != null) {
        val crucible = (if (inputs.size > 1) CompatItems.CRUCIBLE_MULTI else CompatItems.CRUCIBLE_SINGLE).get(0)
        val stacks = arrayOf(crucible) + inputs.mapNotNull { it.first.get(OreDictionary.crushed, it.second) }
        GT_Values.RA.stdBuilder()
            .itemInputs(*stacks)
            .noItemOutputs()
            .fluidOutputs(molten)
            .noFluidInputs()
            .duration(DURATION_SMELTER)
            .eut(ZERO_EU)
            .noOptimize()
            .addTo(PRIMITIVE_SMELTER_RECIPE_MAP)
    }
}
