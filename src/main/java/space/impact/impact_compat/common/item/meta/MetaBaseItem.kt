@file:Suppress("LeakingThis")

package space.impact.impact_compat.common.item.meta

import net.minecraft.item.ItemStack
import space.impact.impact_compat.common.item.GenericItem

abstract class MetaBaseItem(unLocalName: String) : GenericItem(unLocalName, "Generated Item", null, false) {
    init {
        setHasSubtypes(true)
        setMaxDamage(0)
    }

    override fun getItemStackLimit(stack: ItemStack): Int {
        return if (getDamage(stack) == 32763) 1 else super.getItemStackLimit(stack)
    }

    override fun getShareTag(): Boolean {
        return true
    }

    override fun getItemEnchantability(): Int {
        return 0
    }

    override fun isBookEnchantable(stack: ItemStack, aBook: ItemStack): Boolean {
        return false
    }

    override fun getIsRepairable(stack: ItemStack, aMaterial: ItemStack): Boolean {
        return false
    }
}
