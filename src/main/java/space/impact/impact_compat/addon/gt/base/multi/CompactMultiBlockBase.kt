package space.impact.impact_compat.addon.gt.base.multi

import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import net.minecraft.item.ItemStack

@Suppress("unused")
abstract class CompactMultiBlockBase<T> : GT_MetaTileEntity_EnhancedMultiBlockBase<T> where T : GT_MetaTileEntity_EnhancedMultiBlockBase<T> {

    constructor(id: Int, name: String, unlocalized: String) : super(id, name, unlocalized)
    constructor(name: String) : super(name)

    override fun isCorrectMachinePart(aStack: ItemStack?) = true
    override fun getDamageToComponent(aStack: ItemStack?) = 0
    override fun getMaxEfficiency(aStack: ItemStack?) = 10000
    override fun explodesOnComponentBreak(aStack: ItemStack?) = false
}
