package space.impact.impact_compat.addon.gt.base.multi

import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import net.minecraft.item.ItemStack
import space.impact.impact_compat.common.util.world.Vec3
import space.impact.impact_compat.core.Config

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

    private fun clearError() {
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

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(te, aTick)
        if (Config.isEnabledLowPerformance) performanceLogic(te, aTick)
    }

    open fun performanceLogic(te: IGregTechTileEntity, tick: Long) = Unit

    final override fun doRandomMaintenanceDamage(): Boolean {
        return if (isEnabledMaintenance) super.doRandomMaintenanceDamage()
        else if (!isCorrectMachinePart(mInventory[1])) {
            stopMachine(); false
        } else true
    }

    fun checkPiece(piece: String, offset: Vec3): Boolean {
        return checkPiece(piece, offset.x, offset.y, offset.z)
    }

    fun buildPiece(piece: String, trigger: ItemStack?, hintOnly: Boolean, offset: Vec3): Boolean {
        return buildPiece(piece, trigger, hintOnly, offset.x, offset.y, offset.z)
    }

    fun survivalBuildPiece(
        piece: String, trigger: ItemStack?, offset: Vec3, elementsBudget: Int,
        env: ISurvivalBuildEnvironment?, check: Boolean = false, checkIfPlaced: Boolean = true
    ): Int {
        return survivialBuildPiece(piece, trigger, offset.x, offset.y, offset.z, elementsBudget, env, check, checkIfPlaced)
    }
}
