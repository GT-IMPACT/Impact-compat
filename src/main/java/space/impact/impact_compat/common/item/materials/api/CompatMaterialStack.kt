package space.impact.impact_compat.common.item.materials.api

import gregtech.api.enums.Materials
import gregtech.api.util.GT_Utility
import space.impact.impact_compat.common.item.materials.GeneratedMaterials

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
