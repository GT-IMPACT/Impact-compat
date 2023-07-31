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

    MACHINE_CASE_BRONZE,

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
        init()
    }
}

fun IIconContainer.factory(): ITexture = TextureFactory.of(this)
