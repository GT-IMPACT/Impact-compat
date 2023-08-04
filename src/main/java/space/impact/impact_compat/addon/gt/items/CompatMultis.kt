package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack

enum class CompatMultis : CompatBaseItemContainer {

    STEAM_BRONZE_BOILER,
    STEAM_KINETIC_FORGE_HAMMER,
    STEAM_KINETIC_CRUSHER,
    STEAM_KINETIC_EXTRACTOR,
    STEAM_KINETIC_COMPRESSOR,
    STEAM_KINETIC_CENTRIFUGE,
    STEAM_KINETIC_FURNACE,
    STEAM_KINETIC_SIFTER,
    STEAM_KINETIC_ORE_WASHER,
    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}
