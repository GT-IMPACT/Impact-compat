package space.impact.impact_compat.common.util.sound

import net.minecraft.util.ResourceLocation
import space.impact.impact_compat.MODID

enum class SoundRes {

    FORGE_HAMMER(0, MODID, "machine.forge_hammer"),
    ;

    val id: Int
    val resourceLocation: ResourceLocation
    constructor(id: Int, resourcePath: String) :  this(id, ResourceLocation(resourcePath))
    constructor(id: Int, resourceDomain: String, resourcePath: String) : this(id, ResourceLocation(resourceDomain.lowercase(), resourcePath))
    constructor(id: Int, resourceLocation: ResourceLocation) {
        this.id = id
        this.resourceLocation = resourceLocation
    }
}
