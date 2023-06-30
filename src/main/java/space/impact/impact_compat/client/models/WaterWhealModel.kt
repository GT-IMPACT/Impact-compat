package space.impact.impact_compat.client.models

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.util.ResourceLocation
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.libs.vecmath.Vector3f
import space.impact.impact_compat.MODID
import space.impact.impact_compat.client.interfaces.IRenderedModel

@SideOnly(Side.CLIENT)
class WaterWhealModel<T : IAnimatable> : AnimatedGeoModel<T>(), IRenderedModel {

    companion object {
        private val modelResource = ResourceLocation(MODID, "models/water_wheal.geo.json")
        private val textureResource = ResourceLocation(MODID, "textures/blocks/water_wheal.png")
        private val animationResource = ResourceLocation(MODID, "animations/water_wheal.animation.geo.json")
    }

    override fun getModelLocation(anim: T) = modelResource
    override fun getTextureLocation(anim: T) = textureResource
    override fun getAnimationFileLocation(anim: T) = animationResource
    override fun scaleItem(): Vector3f = Vector3f(.5f, .5f, .5f)
}
