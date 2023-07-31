package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack

enum class CompatMultis : CompatBaseItemContainer {

    STEAM_KINETIC_FORGE_HAMMER
    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}
