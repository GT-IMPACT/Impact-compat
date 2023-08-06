package space.impact.impact_compat.common.item.materials

import gregtech.api.interfaces.IIconContainer
import space.impact.impact_compat.addon.gt.util.textures.CustomTextures

class TextureCompatMaterial(
    prefix: String,
    onReplace: ((OreDictionary) -> TextureType?)? = null
) {

    companion object {
        private const val FOLDER_MATERIALS = "material_icons/"
        private const val VOID = "VOID"
        private val VOID_TEXTURE = CustomTextures(FOLDER_MATERIALS + VOID)
        private const val DEFAULT_PREFIX = ""

        val NONE: TextureCompatMaterial = TextureCompatMaterial(VOID)
        val DEFAULT: TextureCompatMaterial = TextureCompatMaterial(DEFAULT_PREFIX)
    }

    val textures = arrayOfNulls<IIconContainer>(OreDictionary.values().size)

    init {
        val pref = if (prefix.isNotEmpty()) "_${prefix.lowercase()}" else prefix
        for (ore in OreDictionary.values()) {
            val type = onReplace?.invoke(ore) ?: ore.textureDefault
            textures[ore.ordinal] = setItemIcon(type.name, pref)
        }
    }

    private fun setItemIcon(name: String, prefix: String): CustomTextures {
        if (prefix == VOID) return VOID_TEXTURE
        return CustomTextures(FOLDER_MATERIALS + "$name$prefix".uppercase())
    }
}


enum class TextureType {
    INGOT,
    PLATE,
    VOID;
}
