package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack

enum class CompatBlocks : CompatBaseItemContainer {

    WATER_WHEAL_BLOCK,
    WIND_WHEAL_BLOCK,
    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}
