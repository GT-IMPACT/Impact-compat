package space.impact.impact_compat.common.item.materials

import net.minecraft.item.ItemStack
import space.impact.impact_compat.common.item.materials.api.CompatMaterial.Companion.MAX_MATERIALS
import space.impact.impact_compat.common.item.materials.api.OreDictionary
import space.impact.impact_compat.common.item.meta.MetaGeneratedItem32

class MaterialGeneratedItemsPage1 : MetaGeneratedItem32(
    "compat.meta.01", OreDictionary.ingot, OreDictionary.plate, OreDictionary.crushed
) {

    companion object {
        var INSTANCE: MaterialGeneratedItemsPage1 = MaterialGeneratedItemsPage1()
    }


    override fun getItemStackLimit(stack: ItemStack): Int {
        val tDamage = getDamage(stack)
        return if (tDamage < MAX_COUNT_AUTOGENERATED_ITEMS && mGeneratedPrefixList[tDamage / MAX_MATERIALS] != null)
            mGeneratedPrefixList[tDamage / MAX_MATERIALS]?.stackSize ?: super.getItemStackLimit(stack)
        else super.getItemStackLimit(stack)
    }
}