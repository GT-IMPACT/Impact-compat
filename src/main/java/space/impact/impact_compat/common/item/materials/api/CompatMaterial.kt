package space.impact.impact_compat.common.item.materials.api

import gregtech.api.enums.*
import gregtech.api.interfaces.IColorModulationContainer
import gregtech.api.util.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.common.item.materials.GeneratedMaterials
import space.impact.impact_compat.common.item.materials.texture.TextureCompatMaterial
import space.impact.impact_compat.core.Config
import java.awt.Color

data class CompatMaterial(
    val id: Int = -1,
    val unificatable: Boolean = false,
    val localName: String = "NOTHING",
    val chemicalFormula: String = "Empty",
    val color: Color = Color.WHITE,
    val icon: TextureCompatMaterial = TextureCompatMaterial.DEFAULT,
    val orePrefixes: List<OreDictionary> = arrayListOf(),
    @JvmField val name: String = localName.replace(" ", "").replace("-", ""),
    val mFluid: Fluid? = null,
    val mGas: Fluid? = null,
    val mStandardMoltenFluid: Fluid? = null,
    val mMaterialList: List<CompatMaterialStack> = arrayListOf(),
    val subTags: List<SubTags> = arrayListOf(),
) : IColorModulationContainer, IMaterial {

    constructor(material: CompatMaterial) : this(
        id = material.id,
        unificatable = material.unificatable,
        localName = material.localName,
        chemicalFormula = material.chemicalFormula,
        color = material.color,
        orePrefixes = material.orePrefixes,
    )

    companion object {
        val MATERIALS_MAP: LinkedHashMap<String, CompatMaterial> = LinkedHashMap()
        val BUILDER = CompatMaterialBuilder()

        const val MAX_MATERIALS = 2000
        val GENERATED_MATERIALS = arrayOfNulls<CompatMaterial>(MAX_MATERIALS)

        fun registerMaterials() {
            GeneratedMaterials.NULL
            for (material in MATERIALS_MAP.values) {
                if (material.id >= 0) {
                    if (GENERATED_MATERIALS[material.id] == null) {
                        GENERATED_MATERIALS[material.id] = material
                    }
                }
            }
        }

        fun getAll() = GENERATED_MATERIALS.filterNotNull()
    }

    override fun getRGBA(): ShortArray {
        return shortArrayOf(color.red.toShort(), color.green.toShort(), color.blue.toShort(), color.alpha.toShort())
    }

    fun getLocalizedNameForItem(aFormat: String): String {
        return String.format(aFormat.replace("%s", "%temp").replace("%material", "%s"), localName).replace("%temp", "%s")
    }

    fun getDefaultLocalizedNameForItem(aFormat: String): String {
        return String.format(aFormat.replace("%s", "%temp").replace("%material", "%s"), localName).replace("%temp", "%s")
    }

    fun getToolTip(aShowQuestionMarks: Boolean = Config.isDebug): String {
        if (!aShowQuestionMarks && chemicalFormula == "?") return ""
        return chemicalFormula
    }

    fun getFluid(amount: Int): FluidStack? {
        return if (mFluid == null) null else FluidStack(mFluid, amount)
    }

    fun getGas(amount: Int): FluidStack? {
        return if (mGas == null) null else FluidStack(mGas, amount)
    }

    fun getMolten(amount: Int): FluidStack? {
        return if (mStandardMoltenFluid == null) null else FluidStack(mStandardMoltenFluid, amount)
    }

    fun get(prefix: OreDictionary, amount: Int): ItemStack? {
        return GT_OreDictUnificator.getFirstOre(prefix[this], amount.toLong())
    }

    override fun getName(): String {
        return name
    }
}
