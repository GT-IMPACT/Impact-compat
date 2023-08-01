package space.impact.impact_compat.addon.gt.util.tooltip

import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import net.minecraft.util.EnumChatFormatting

object TooltipExt {

    fun String.colored(enum: EnumChatFormatting): String = "${enum}${this}"

    fun Int.toCount(): String = this.toString().colored(EnumChatFormatting.GOLD)

    fun GT_Multiblock_Tooltip_Builder.addOtherStructurePartCount(structure: String, count: Int = 1, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOtherStructurePart(structure, count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addInputBusCount(count: Int = 1, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addInputBus(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addOutputBusCount(count: Int = 1, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOutputBus(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addInputHatchCount(count: Int = 1, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addInputHatch(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addOutputHatchCount(count: Int = 1, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOutputHatch(count.toCount(), dot)
    }
}
