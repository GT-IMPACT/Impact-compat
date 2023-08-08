@file:Suppress("LeakingThis")

package space.impact.impact_compat.addon.gt.util.textures

import gregtech.api.GregTech_API
import gregtech.api.interfaces.IIconContainer
import gregtech.api.interfaces.ITexture
import gregtech.api.render.TextureFactory
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.IIcon
import net.minecraft.util.ResourceLocation
import space.impact.impact_compat.MODID

enum class CompatTextures : IIconContainer, Runnable {

    CASE_MACHINE_BRONZE,
    CASE_GLASS_BRONZE,
    CASE_FIREBOX_BRONZE, CASE_FIREBOX_BRONZE_ACTIVE,
    CASE_FIREBOX_BRONZE_DOOR_OVERLAY, CASE_FIREBOX_BRONZE_DOOR_OVERLAY_ACTIVE,

    CASE_BRONZE_BRICK,
    CASE_VANILA_BRICK,

    CASE_PRIMITIVE_HATCH,
    CASE_PRIMITIVE_SMELTER,

    OVERLAY_CENTRIFUGE, OVERLAY_CENTRIFUGE_ACTIVE,
    OVERLAY_WASHER, OVERLAY_WASHER_ACTIVE,
    OVERLAY_SIFTER, OVERLAY_SIFTER_ACTIVE,
    OVERLAY_ROTOR_HATCH,

    PIPE_SIDE_PRIMITIVE, PIPE_MEDIUM_PRIMITIVE

    ;

    private var mIcon: IIcon? = null

    init {
        GregTech_API.sGTBlockIconload.add(this)
    }

    override fun getIcon(): IIcon? {
        return mIcon
    }

    override fun getOverlayIcon(): IIcon? {
        return null
    }

    override fun getTextureFile(): ResourceLocation {
        return TextureMap.locationBlocksTexture
    }

    override fun run() {
        mIcon = GregTech_API.sBlockIcons.registerIcon("$MODID:iconsents/$this")
    }
}

fun IIconContainer.factory(): ITexture = TextureFactory.of(this)
fun IIconContainer.factoryGlow(): ITexture = TextureFactory.builder().addIcon(this).glow().build()
