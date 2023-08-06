package space.impact.impact_compat.common.item.materials

import gregtech.api.enums.Element
import space.impact.impact_compat.common.item.materials.CompatMaterialBuilder.FluidType
import java.awt.Color

object GeneratedMaterials {

    val NULL = CompatMaterial.BUILDER.start(-1, "NULL")
        .addColor(Color(0, 0, 0, 0)).addTexture(TextureCompatMaterial.NONE)
        .addTypes().create()

    val COPPER = CompatMaterial.BUILDER.start(1, "CopperTest")
        .addColor(Color(255, 100, 0)).addTexture(TextureCompatMaterial.DEFAULT).addElement(Element.Cu)
        .addTypes(OreDictionary.ingot, OreDictionary.plate).addFluid(FluidType.MOLTEN).create()

    val TIN = CompatMaterial.BUILDER.start(2, "TinTest")
        .addColor(Color(220, 220, 220)).addTexture(TextureCompatMaterial.DEFAULT).addElement(Element.Sn)
        .addTypes(OreDictionary.ingot, OreDictionary.plate).addFluid(FluidType.MOLTEN).create()

    val BRONZE = CompatMaterial.BUILDER.start(3, "BronzeTeste")
        .addColor(Color(255, 128, 0)).addTexture(TextureCompatMaterial.DEFAULT).addElements(TIN to 1, COPPER to 3)
        .addTypes(OreDictionary.ingot, OreDictionary.plate).addFluid(FluidType.MOLTEN).create()

    val HYDROGEN = CompatMaterial.BUILDER.start(4, "Hydrogennnnnnnnnnnnn")
        .addColor(Color(0, 0, 255)).addTexture(TextureCompatMaterial.DEFAULT).addElement(Element.H)
        .addTypes().addFluid(FluidType.GAS).create()
}