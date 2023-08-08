package space.impact.impact_compat.common.item.materials

import gregtech.api.enums.Element
import space.impact.impact_compat.common.item.materials.api.CompatMaterial
import space.impact.impact_compat.common.item.materials.api.CompatMaterialBuilder.FluidType
import space.impact.impact_compat.common.item.materials.api.OreDictionary
import space.impact.impact_compat.common.item.materials.api.SubTags
import space.impact.impact_compat.common.item.materials.texture.TextureCompatMaterial
import java.awt.Color

object GeneratedMaterials {

    val NULL = CompatMaterial.BUILDER.start(-1, "NULL")
        .addColor(Color(0, 0, 0, 0)).addTexture(TextureCompatMaterial.NONE)
        .addTypes().create()

    val COPPER = CompatMaterial.BUILDER.start(1, "CopperTest")
        .addColor(Color(255, 100, 0)).addTexture(TextureCompatMaterial.DEFAULT).addElement(Element.Cu)
        .addSubTags(SubTags.SMELTER, SubTags.CASTER)
        .addTypes(OreDictionary.ingot, OreDictionary.plate, OreDictionary.crushed)
        .addFluid(FluidType.MOLTEN, 1085).create()

    val TIN = CompatMaterial.BUILDER.start(2, "TinTest")
        .addColor(Color(220, 220, 220)).addTexture(TextureCompatMaterial.DEFAULT).addElement(Element.Sn)
        .addSubTags(SubTags.SMELTER, SubTags.CASTER)
        .addTypes(OreDictionary.ingot, OreDictionary.plate, OreDictionary.crushed)
        .addFluid(FluidType.MOLTEN, 231).create()

    val BRONZE = CompatMaterial.BUILDER.start(3, "BronzeTeste")
        .addColor(Color(255, 128, 0)).addTexture(TextureCompatMaterial.DEFAULT).addElements(TIN to 1, COPPER to 3)
        .addSubTags(SubTags.CASTER)
        .addTypes(OreDictionary.ingot, OreDictionary.plate)
        .addFluid(FluidType.MOLTEN, 1140).create()

    val HYDROGEN = CompatMaterial.BUILDER.start(4, "Hydrogennnnnnnnnnnnn")
        .addColor(Color(0, 0, 255)).addTexture(TextureCompatMaterial.DEFAULT).addElement(Element.H)
        .addTypes().addFluid(FluidType.GAS).create()
}
