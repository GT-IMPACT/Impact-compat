@file:Suppress("EnumEntryName")

package space.impact.impact_compat.common.item.materials.api

import space.impact.impact_compat.common.item.materials.GeneratedMaterials
import space.impact.impact_compat.common.item.materials.texture.TextureType

enum class OreDictionary(
    val regularName: String,
    val prefix: String,
    val postfix: String,
    val stackSize: Int,
    val textureDefault: TextureType,
) {

    crushed("Crushed", "", " Ore", 64, TextureType.CRUSHED_ORE),
    ingot("Ingots", "", " Ingot", 64, TextureType.INGOT),
    plate("Plate", "", " Plate", 32, TextureType.PLATE),
    ;

    fun getDefaultLocalNameFormatForItem(material: CompatMaterial): String {
        return "$prefix%material$postfix"
    }

    fun getDefaultLocalNameForItem(material: CompatMaterial): String {
        return material.getDefaultLocalizedNameForItem(getDefaultLocalNameFormatForItem(material))
    }

    fun doGenerateItem(material: CompatMaterial?): Boolean {
        return material != null && material != GeneratedMaterials.NULL && material.orePrefixes.contains(this)
    }

    operator fun get(material: CompatMaterial?): String {
        return if (material != null) "$name${material.name}" else name
    }
}
