package space.impact.impact_compat.common.item.tool

import buildcraft.api.tools.IToolWrench
import cpw.mods.fml.common.Optional
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

@Optional.InterfaceList(
    Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraftAPI|tools"),
)
class ToolWrenchUniversal : ToolBaseItem("wrench_universal", "Universal Wrench", "Wrench to all"), IToolWrench {

    companion object {
        val INSTANCE = ToolWrenchUniversal()
    }

    init {
        setFull3D()
        setMaxStackSize(1)
        setHarvestLevel("wrench", Int.MAX_VALUE)
    }

    override fun canWrench(player: EntityPlayer?, x: Int, y: Int, z: Int): Boolean {
        return true
    }

    override fun wrenchUsed(player: EntityPlayer?, x: Int, y: Int, z: Int) {
        player?.swingItem()
    }

    override fun getDigSpeed(itemstack: ItemStack?, block: Block?, metadata: Int): Float {
        return 5f
    }
}
