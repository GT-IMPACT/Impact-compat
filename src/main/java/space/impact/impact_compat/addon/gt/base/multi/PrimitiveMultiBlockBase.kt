package space.impact.impact_compat.addon.gt.base.multi

import gregtech.api.interfaces.fluid.IFluidStore
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output
import gregtech.api.util.GT_Recipe
import gregtech.api.util.GT_Single_Recipe_Check
import gregtech.api.util.GT_Utility
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveInputBus
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveInputHatch
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveOutputBus
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveOutputHatch

abstract class PrimitiveMultiBlockBase<T> : CompatMultiBlockBase<T> where T : CompatMultiBlockBase<T> {

    companion object {
        @JvmStatic
        private fun dumpFluids(
            aOutputHatches: List<GT_MetaTileEntity_Hatch_Output>, copiedFluidStack: FluidStack,
            restrictiveHatchesOnly: Boolean
        ): Boolean {
            for (tHatch in aOutputHatches) {
                if (!isValidMetaTileEntity(tHatch) || restrictiveHatchesOnly && tHatch.mMode.toInt() == 0) continue
                if (!tHatch.canStoreFluid(copiedFluidStack)) continue
                val tAmount = tHatch.fill(copiedFluidStack, false)
                if (tAmount >= copiedFluidStack.amount) {
                    return tHatch.fill(copiedFluidStack, true) >= copiedFluidStack.amount
                } else if (tAmount > 0) {
                    copiedFluidStack.amount -= tHatch.fill(copiedFluidStack, true)
                }
            }
            return false
        }
    }

    @JvmField
    var inputBusses = ArrayList<PrimitiveInputBus>()

    @JvmField
    var outputBusses = ArrayList<PrimitiveOutputBus>()

    @JvmField
    var inputHatches = ArrayList<PrimitiveInputHatch>()

    @JvmField
    var outputHatches = ArrayList<PrimitiveOutputHatch>()

    constructor(id: Int, name: String, unlocalized: String) : super(id, name, unlocalized, false)
    constructor(name: String) : super(name, false)

    override fun useModularUI(): Boolean {
        return false
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        val tRecipe: GT_Recipe?

        if (mLockedToSingleRecipe && mSingleRecipeCheck != null) {
            if (!mSingleRecipeCheck.checkRecipeInputsSingleStack(true)) return false
            tRecipe = mSingleRecipeCheck.recipe
        } else {
            val tInputList = getStoredInputs()
            val tFluidList = getStoredFluids()
            val inputs = tInputList.toTypedArray<ItemStack>()
            val fluids = tFluidList.toTypedArray<FluidStack>()

            if (inputs.isEmpty() && fluids.isEmpty()) return false

            var tSingleRecipeCheckBuilder: GT_Single_Recipe_Check.Builder? = null

            if (mLockedToSingleRecipe) {
                tSingleRecipeCheckBuilder = GT_Single_Recipe_Check.builder(this).setBefore(inputs, fluids)
            }

            tRecipe = recipeMap?.findRecipe(baseMetaTileEntity, false, false, 0, fluids, *inputs)

            if (tRecipe == null || !canOutputAll(tRecipe) || !tRecipe.isRecipeInputEqual(true, fluids, *inputs)) return false

            if (mLockedToSingleRecipe) {
                mSingleRecipeCheck = tSingleRecipeCheckBuilder?.setAfter(inputs, fluids)?.setRecipe(tRecipe)?.build()
            }
        }

        mEfficiency = 10000
        mEfficiencyIncrease = 1000

        mMaxProgresstime = tRecipe!!.mDuration
        mOutputItems = tRecipe.mOutputs
        mOutputFluids = tRecipe.mFluidOutputs
        updateSlots()
        return true
    }

    override fun getStoredInputs(): ArrayList<ItemStack> {
        val rList = super.getStoredInputs()
        for (tHatch in inputBusses) {
            tHatch.mRecipeMap = recipeMap
            if (isValidMetaTileEntity(tHatch)) {
                for (i in tHatch.baseMetaTileEntity.sizeInventory - 1 downTo 0) {
                    if (tHatch.baseMetaTileEntity.getStackInSlot(i) != null)
                        rList.add(tHatch.baseMetaTileEntity.getStackInSlot(i))
                }
            }
        }
        if (getStackInSlot(1) != null && getStackInSlot(1)
                .unlocalizedName.startsWith("gt.integrated_circuit")
        ) rList.add(getStackInSlot(1))
        return rList
    }

    override fun getStoredFluids(): ArrayList<FluidStack> {
        val rList = super.getStoredFluids()
        for (tHatch in inputHatches) {
            tHatch.mRecipeMap = recipeMap
            if (isValidMetaTileEntity(tHatch) && tHatch.fillableStack != null) {
                rList.add(tHatch.fillableStack)
            }
        }
        return rList
    }

    override fun getStoredOutputs(): ArrayList<ItemStack> {
        val rList = super.getStoredOutputs()
        for (tHatch in outputBusses) {
            if (isValidMetaTileEntity(tHatch)) {
                for (i in tHatch.baseMetaTileEntity.sizeInventory - 1 downTo 0) {
                    rList.add(tHatch.baseMetaTileEntity.getStackInSlot(i))
                }
            }
        }
        return rList
    }

    override fun updateSlots() {
        for (tHatch in inputHatches) if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots()
        for (tHatch in inputBusses)
            if (isValidMetaTileEntity(tHatch))
                tHatch.updateSlots()
        super.updateSlots()
    }

    fun addInputBus(te: IGregTechTileEntity?, base: Short): Boolean {
        if (te == null) return false
        val mte = te.metaTileEntity ?: return false
        if (mte is PrimitiveInputBus) {
            mte.updateTexture(base.toInt())
            mte.updateCraftingIcon(getMachineCraftingIcon())
            mte.mRecipeMap = recipeMap
            return inputBusses.add(mte)
        }
        return false
    }

    fun addOutputBus(te: IGregTechTileEntity?, base: Short): Boolean {
        if (te == null) return false
        val mte = te.metaTileEntity ?: return false
        if (mte is PrimitiveOutputBus) {
            mte.updateTexture(base.toInt())
            mte.updateCraftingIcon(getMachineCraftingIcon())
            return outputBusses.add(mte)
        }
        return false
    }

    fun addInputHatch(te: IGregTechTileEntity?, base: Short): Boolean {
        if (te == null) return false
        val mte = te.metaTileEntity ?: return false
        if (mte is PrimitiveInputHatch) {
            mte.updateTexture(base.toInt())
            mte.updateCraftingIcon(getMachineCraftingIcon())
            mte.mRecipeMap = recipeMap
            return inputHatches.add(mte)
        }
        return false
    }

    fun addOutputHatch(te: IGregTechTileEntity?, base: Short): Boolean {
        if (te == null) return false
        val mte = te.metaTileEntity ?: return false
        if (mte is PrimitiveOutputHatch) {
            mte.updateTexture(base.toInt())
            mte.updateCraftingIcon(getMachineCraftingIcon())
            return outputHatches.add(mte)
        }
        return false
    }

    override fun clearHatches() {
        inputBusses.clear()
        outputBusses.clear()
        inputHatches.clear()
        outputHatches.clear()
        super.clearHatches()
    }

    override fun depleteInput(aLiquid: FluidStack?, simulate: Boolean): Boolean {
        if (aLiquid == null) return false
        for (tHatch in inputHatches) {
            tHatch.mRecipeMap = recipeMap
            if (isValidMetaTileEntity(tHatch)) {
                var tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, false)
                if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                    if (simulate) {
                        return true
                    }
                    tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, true)
                    return tLiquid != null && tLiquid.amount >= aLiquid.amount
                }
            }
        }
        return super.depleteInput(aLiquid, simulate)
    }

    override fun addOutput(aLiquid: FluidStack?): Boolean {
        if (aLiquid == null) return false
        val copiedFluidStack = aLiquid.copy()
        if (!dumpFluids(outputHatches, copiedFluidStack, true)) {
            dumpFluids(outputHatches, copiedFluidStack, false)
        }
        return super.addOutput(aLiquid)
    }


    override fun depleteInput(aStack: ItemStack?): Boolean {
        if (GT_Utility.isStackInvalid(aStack)) return false
        val aLiquid = GT_Utility.getFluidForFilledItem(aStack, true)
        if (aLiquid != null) return depleteInput(aLiquid)
        for (tHatch in inputHatches) {
            tHatch.mRecipeMap = recipeMap
            if (isValidMetaTileEntity(tHatch)) {
                if (GT_Utility.areStacksEqual(aStack, tHatch.baseMetaTileEntity.getStackInSlot(0))) {
                    if (tHatch.baseMetaTileEntity.getStackInSlot(0).stackSize >= aStack!!.stackSize) {
                        tHatch.baseMetaTileEntity.decrStackSize(0, aStack.stackSize)
                        return true
                    }
                }
            }
        }
        for (tHatch in inputBusses) {
            tHatch.mRecipeMap = recipeMap
            if (isValidMetaTileEntity(tHatch)) {
                for (i in tHatch.baseMetaTileEntity.sizeInventory - 1 downTo 0) {
                    if (GT_Utility.areStacksEqual(aStack, tHatch.baseMetaTileEntity.getStackInSlot(i))) {
                        if (tHatch.baseMetaTileEntity.getStackInSlot(i).stackSize >= aStack!!.stackSize) {
                            tHatch.baseMetaTileEntity.decrStackSize(i, aStack.stackSize)
                            return true
                        }
                    }
                }
            }
        }
        return super.depleteInput(aStack)
    }

    override fun addOutput(aStack: ItemStack?): Boolean {
        var stack = aStack
        if (GT_Utility.isStackInvalid(stack)) return false
        stack = GT_Utility.copyOrNull(stack)
        for (tHatch in outputBusses) {
            if (isValidMetaTileEntity(tHatch) && tHatch.storeAll(stack)) {
                return true
            }
        }
        return super.addOutput(stack)
    }

    override fun getFluidOutputSlots(toOutput: Array<FluidStack?>?): List<IFluidStore?> {
        return super.getFluidOutputSlots(toOutput) + filterValidMetaTileEntities(outputHatches)
    }
}
