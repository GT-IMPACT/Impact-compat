package space.impact.impact_compat.common.worldgen

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

@Suppress("NAME_SHADOWING")
object WorldGenHelper {

    fun setOreBlock(aWorld: World, x: Int, y: Int, z: Int, block: Block, meta: Int, air: Boolean = false): Boolean {
        if (block == Blocks.air) return false
        var y = y
        if (!air) y = min(aWorld.actualHeight.toDouble(), max(y.toDouble(), 1.0)).toInt()
        val tTargetedBlock = aWorld.getBlock(x, y, z)
        if ((tTargetedBlock != Blocks.air || air)) {
            if (tTargetedBlock.replaceableBlocks(aWorld, x, y, z)) {
                aWorld.setBlock(x, y, z, block, meta, 0)
                return true
            }
        }
        return false
    }

    fun Block.replaceableBlocks(world: World, x: Int, y: Int, z: Int): Boolean {
        return WorldGenInit.sBlockReplaceableList.any { isReplaceableOreGen(world, x, y, z, it) } ||
                WorldGenInit.sBlockUnlocalizedReplaceableList.any { unlocalizedName.equals(it) }
    }
}
