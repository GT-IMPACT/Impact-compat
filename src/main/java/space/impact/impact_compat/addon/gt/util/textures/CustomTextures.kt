package space.impact.impact_compat.addon.gt.util.textures

import gregtech.api.GregTech_API
import gregtech.api.interfaces.IIconContainer
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.IIcon
import net.minecraft.util.ResourceLocation
import space.impact.impact_compat.MODID

class CustomTextures(protected var mIconName: String) : IIconContainer, Runnable {
    protected var mIcon: IIcon? = null
    protected var mOverlay: IIcon? = null

    init {
        GregTech_API.sGTItemIconload.add(this)
    }

    override fun getIcon(): IIcon? {
        return mIcon
    }

    override fun getOverlayIcon(): IIcon? {
        return mOverlay
    }

    override fun getTextureFile(): ResourceLocation {
        return TextureMap.locationItemsTexture
    }

    override fun run() {
        mIcon = GregTech_API.sItemIcons.registerIcon("$MODID:$mIconName")
        mOverlay = GregTech_API.sItemIcons.registerIcon("$MODID:$mIconName" + "_OVERLAY")
    }
}
