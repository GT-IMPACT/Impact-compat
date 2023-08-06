package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack

enum class CompatHatches : CompatBaseItemContainer {

    PRIMITIVE_WHEAL_ROTOR,
    STEAM_ROTOR_HATCH,
    UNDERWATER_PUMP_HATCH,
    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}