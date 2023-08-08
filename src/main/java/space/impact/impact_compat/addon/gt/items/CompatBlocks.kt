package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack
import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeBlocks
import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeGlassBlocks

enum class CompatBlocks(val meta: Int = 0) : CompatBaseItemContainer {

    //Block Models
    WATER_WHEAL_BLOCK,
    WIND_WHEAL_BLOCK,
    STEAM_ROTOR_BLOCK,
    SMELTER_CONTENT_BLOCK,

    //Block Structure
    BRONZE_MACHINE_CASING(SteamAgeBlocks.META_BRONZE_MACHINE_CASING),
    BRONZE_FIREBOX_CASING(SteamAgeBlocks.META_BRONZE_FIREBOX_CASING),
    BRONZE_BRICK_CASING(SteamAgeBlocks.META_BRONZE_BRICK_CASING),
    PRIMITIVE_SMELTER_CASING(SteamAgeBlocks.META_PRIMITIVE_SMELTER_CASING),

    //Block Glass
    BRONZE_GLASS_CASING(SteamAgeGlassBlocks.META_BRONZE_GLASS_MACHINE_CASING),

    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name
}
