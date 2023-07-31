package space.impact.impact_compat.addon.gt.base.multi

import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import gregtech.api.util.GT_Utility
import net.minecraft.item.ItemStack

@Suppress("unused")
abstract class CompatMultiBlockBase<T> : GT_MetaTileEntity_EnhancedMultiBlockBase<T> where T : GT_MetaTileEntity_EnhancedMultiBlockBase<T> {

    companion object {
        @JvmStatic
        protected val MAIN = "main_structure"
    }

    private val isEnabledMaintenance: Boolean

    constructor(id: Int, name: String, unlocalized: String, isEnabledMaintenance: Boolean = true) : super(id, unlocalized, name) {
        this.isEnabledMaintenance = isEnabledMaintenance
        if (!isEnabledMaintenance) clearError()
    }
    constructor(name: String, isEnabledMaintenance: Boolean = true) : super(name) {
        this.isEnabledMaintenance = isEnabledMaintenance
        if (!isEnabledMaintenance) clearError()
    }

    protected fun clearError() {
        mWrench = true
        mScrewdriver = true
        mSoftHammer = true
        mHardHammer = true
        mSolderingTool = true
        mCrowbar = true
    }

    override fun isCorrectMachinePart(aStack: ItemStack?) = true
    override fun getDamageToComponent(aStack: ItemStack?) = 0
    override fun getMaxEfficiency(aStack: ItemStack?) = 10000
    override fun explodesOnComponentBreak(aStack: ItemStack?) = false

    fun addToDefaultHatches(te: IGregTechTileEntity, index: Short) = addToMachineList(te, index.toInt())

    override fun doRandomMaintenanceDamage(): Boolean {
        return if (isEnabledMaintenance) super.doRandomMaintenanceDamage()
        else if (!isCorrectMachinePart(mInventory[1])) {
            stopMachine(); false
        } else true
    }
}
