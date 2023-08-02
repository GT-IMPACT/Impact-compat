package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack

enum class CompatBlocks : CompatBaseItemContainer {

    //Block Models
    WATER_WHEAL_BLOCK,
    WIND_WHEAL_BLOCK,
    STEAM_ROTOR_BLOCK,

    //Block Structure
    BRONZE_MACHINE_CASING,
    BRONZE_FIREBOX_CASING,
    BRONZE_GLASS_CASING,

    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}
