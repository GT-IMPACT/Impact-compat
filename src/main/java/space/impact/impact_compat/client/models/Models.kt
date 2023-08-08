package space.impact.impact_compat.client.models

import net.minecraft.util.ResourceLocation
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.libs.vecmath.Vector3f
import space.impact.impact_compat.MODID
import space.impact.impact_compat.client.interfaces.IRenderedModel

sealed class Models<T : IAnimatable>(
    name: String,
    private val scale: Vector3f = Vector3f(1f, 1f, 1f)
) : AnimatedGeoModel<T>(), IRenderedModel {

    private val modelResource = ResourceLocation(MODID, "models/$name.geo.json")
    private val textureResource = ResourceLocation(MODID, "textures/blocks/$name.png")
    private val animationResource = ResourceLocation(MODID, "animations/$name.animation.geo.json")

    override fun getModelLocation(anim: T) = modelResource
    override fun getTextureLocation(anim: T) = textureResource
    override fun getAnimationFileLocation(anim: T) = animationResource
    override fun scaleItem(): Vector3f = scale

    class WaterWhealModel<T : IAnimatable> : Models<T>("water_wheal", Vector3f(.5f, .5f, .5f))
    class WindWhealModel<T : IAnimatable> : Models<T>("wind_wheal", Vector3f(.25f, .25f, .25f))
    class SteamRotorModel<T : IAnimatable> : Models<T>("steam_rotor")
    class SmelterContentModel<T : IAnimatable> : Models<T>("smelter_content", Vector3f(.25f, .25f, .25f))
}
