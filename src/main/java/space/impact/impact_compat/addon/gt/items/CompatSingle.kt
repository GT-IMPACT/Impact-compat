package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack

enum class CompatSingle : CompatBaseItemContainer {

    PRIMITIVE_PIPE,


    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}