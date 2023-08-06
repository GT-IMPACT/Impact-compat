package space.impact.impact_compat.common.item.materials

import gregtech.api.enums.*
import gregtech.api.fluid.GT_FluidFactory
import gregtech.api.interfaces.IColorModulationContainer
import gregtech.api.util.GT_RecipeBuilder
import gregtech.api.util.GT_RecipeConstants
import gregtech.api.util.GT_Utility
import gregtech.common.items.GT_FluidDisplayItem
import gregtech.common.render.GT_FluidDisplayStackRenderer
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.core.Config
import java.awt.Color

data class CompatMaterial(
    val id: Int = -1,
    val unificatable: Boolean = false,
    val localName: String = "NOTHING",
    val chemicalFormula: String = "Empty",
    val color: Color = Color.WHITE,
    val icon: TextureCompatMaterial = TextureCompatMaterial.DEFAULT,
    val orePrefixes: List<OreDictionary> = arrayListOf()
) : IColorModulationContainer, IMaterial {

    @JvmField
    val name: String = localName.replace(" ", "").replace("-", "")
    var mFluid: Fluid? = null
    var mGas: Fluid? = null
    var mStandardMoltenFluid: Fluid? = null
    var mMaterialList: ArrayList<CompatMaterialStack> = ArrayList()

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
    }

    init {
        MATERIALS_MAP[name] = this
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

    override fun getName(): String {
        return name
    }
}

class CompatMaterialBuilder {

    private lateinit var material: CompatMaterial

    fun start(id: Int, localName: String, unificatable: Boolean = false): CompatMaterialBuilder {
        material = CompatMaterial()
        material = material.copy(id = id, localName = localName, unificatable = unificatable)
        return this
    }

    fun addColor(color: Color): CompatMaterialBuilder {
        material = material.copy(color = color)
        return this
    }

    fun addTypes(vararg ores: OreDictionary): CompatMaterialBuilder {
        material = material.copy(orePrefixes = ores.toList())
        return this
    }

    fun addTexture(texture: TextureCompatMaterial): CompatMaterialBuilder {
        material = material.copy(icon = texture)
        return this
    }

    fun addChemicalFormula(formula: String): CompatMaterialBuilder {
        material = material.copy(chemicalFormula = formula)
        return this
    }

    fun addElement(element: Element): CompatMaterialBuilder {
        val formula = if (element == Element._NULL) "Empty" else element.toString().replace("_".toRegex(), "-")
        material = material.copy(chemicalFormula = formula)
        return this
    }

    fun addElements(vararg pairs: Pair<CompatMaterial, Int>): CompatMaterialBuilder {
        val matStack = pairs.map { CompatMaterialStack(it.first, it.second.toLong()) }
        material.mMaterialList.addAll(matStack)
        val formula = if (material.mMaterialList.size == 1)
            material.mMaterialList.first().toString(true)
        else
            material.mMaterialList.joinToString("") { it.toString() }.replace("_", "-")
        material = material.copy(chemicalFormula = formula)
        return this
    }

    fun addFluid(type: FluidType, temperature: Int = 300, isCustom: Boolean = false): CompatMaterialBuilder {
        val prefixLocal: String
        val prefixUnLocal: String
        val texture: String
        when (type) {
            FluidType.GAS -> {
                prefixUnLocal = ""; prefixLocal = ""
                texture = if (isCustom) "gas.${material.name.lowercase()}" else "autogenerated"
            }

            FluidType.MOLTEN -> {
                prefixUnLocal = "molten."; prefixLocal = "Molten "
                texture = if (isCustom) "molten.${material.name.lowercase()}" else "molten.autogenerated"
            }

            else -> {
                prefixUnLocal = ""; prefixLocal = ""
                texture = if (isCustom) material.name.lowercase() else "autogenerated"
            }
        }
        val rFluid = GT_FluidFactory.builder(prefixUnLocal + material.name.lowercase())
            .withLocalizedName(prefixLocal + material.localName)
            .withColorRGBA(material.rgba)
            .withTextureName(texture)
            .withStateAndTemperature(if (type == FluidType.GAS) FluidState.GAS else FluidState.LIQUID, temperature)
            .buildAndRegister()
            .asFluid()

        when (type) {
            FluidType.LIQUID -> material.mFluid = rFluid
            FluidType.GAS -> material.mGas = rFluid
            FluidType.MOLTEN -> material.mStandardMoltenFluid = rFluid
        }
        GT_FluidDisplayItem.addChemicalFormula(rFluid, material.chemicalFormula)
        return this
    }

    fun create(): CompatMaterial {
        return material.copy()
    }

    enum class FluidType(
        @JvmField val value: Int,
        @JvmField val state: String,
        isRegister: Boolean = false
    ) : IFluidState {

        GAS(FluidState.GAS),
        LIQUID(FluidState.LIQUID),
        MOLTEN(FluidState.MOLTEN);

        constructor(state: IFluidState) : this(state.value, state.state, false)

        init {
            if (isRegister) FluidState.VALID_STATES += this
        }

        override fun getState() = name
        override fun getValue() = value
    }
}

class CompatMaterialStack(aMaterial: CompatMaterial?, var mAmount: Long) : Cloneable {
    var mMaterial: CompatMaterial

    init {
        mMaterial = aMaterial ?: GeneratedMaterials.NULL
    }

    public override fun clone(): CompatMaterialStack {
        return try {
            super.clone() as CompatMaterialStack
        } catch (e: Exception) {
            CompatMaterialStack(mMaterial, mAmount)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == this) return true
        if (other == null) return false
        if (other is Materials) return other == mMaterial
        return if (other is CompatMaterialStack) other.mMaterial == mMaterial && (mAmount < 0 || other.mAmount < 0 || other.mAmount == mAmount) else false
    }

    override fun toString(): String {
        return toString(false)
    }

    fun toString(single: Boolean): String {
        var temp1 = ""
        val temp2 = mMaterial.getToolTip(true)
        var temp3 = ""
        var temp4 = ""
        if (mAmount > 1) {
            temp4 = GT_Utility.toSubscript(mAmount)
        }
        if ((!single || mAmount > 1) && isMaterialListComplex(this)) {
            temp1 = "("
            temp3 = ")"
        }
        return temp1 + temp2 + temp3 + temp4
    }

    private fun isMaterialListComplex(materialStack: CompatMaterialStack): Boolean {
        if (materialStack.mMaterial.mMaterialList.size > 1) return true
        return if (materialStack.mMaterial.mMaterialList.isEmpty()) false
        else isMaterialListComplex(materialStack.mMaterial.mMaterialList[0])
    }

    override fun hashCode(): Int {
        return mMaterial.hashCode()
    }
}
