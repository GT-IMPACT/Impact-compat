package space.impact.impact_compat.addon.gt.util.tooltip

import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import net.minecraft.util.EnumChatFormatting
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.toCount

object TooltipExt {

    private const val TT_ROTOR_HATCH = "Machine Rotor Hatch"
    private const val TT_BRONZE_GLASS = "Bronze Glass"
    private const val TT_BRONZE_BRICK = "Bronze Brick"
    private const val TT_BRONZE_FIREBOX = "Bronze Firebox"
    private const val TT_BRONZE_CASE = "Bronze Machine Casing"
    private const val ONE_COUNT = 1

    fun String.colored(enum: EnumChatFormatting, suffix: String = ""): String = "${enum}${this}${suffix}"

    fun Int.toCount(suffix: String = ""): String = this.toString().colored(EnumChatFormatting.GOLD, suffix)

    fun GT_Multiblock_Tooltip_Builder.addOtherStructurePartCount(structure: String, count: Int = 1, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOtherStructurePart(structure, count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addInputBusCount(count: Int = ONE_COUNT, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addInputBus(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addOutputBusCount(count: Int = ONE_COUNT, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOutputBus(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addInputHatchCount(count: Int = ONE_COUNT, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addInputHatch(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addOutputHatchCount(count: Int = ONE_COUNT, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOutputHatch(count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addRotorHatch(count: Int = ONE_COUNT, dot: Int): GT_Multiblock_Tooltip_Builder {
        return addOtherStructurePart(TT_ROTOR_HATCH, count.toCount(), dot)
    }

    fun GT_Multiblock_Tooltip_Builder.addSteamMachineStructure(
        bronzeBlocks: IntRange = 0..0,
        bronzeFirebox: Int = 0,
        bronzeGlasses: Int = 0,
        bronzeBricks: Int = 0,
    ): GT_Multiblock_Tooltip_Builder {
        if (bronzeBlocks.first > 0 && bronzeBlocks.last > 0) addOtherStructurePart(TT_BRONZE_CASE, "${bronzeBlocks.first.toCount()} - ${bronzeBlocks.last.toCount()}")
        if (bronzeFirebox > 0 ) addOtherStructurePart(TT_BRONZE_FIREBOX, bronzeFirebox.toCount())
        if (bronzeGlasses > 0 ) addOtherStructurePart(TT_BRONZE_GLASS, bronzeGlasses.toCount())
        if (bronzeBricks > 0 ) addOtherStructurePart(TT_BRONZE_BRICK, bronzeBricks.toCount())
        return this
    }
}
