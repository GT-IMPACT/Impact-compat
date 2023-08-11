package space.impact.impact_compat.common.blocks.rock

import net.minecraft.block.material.Material
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import space.impact.impact_compat.common.blocks.base.DefaultBlockBase
import java.util.ArrayList

class WorldGenerationBlock : DefaultBlockBase("worldgen.01", 4, Material.rock) {
    companion object {
        val INSTANCE = WorldGenerationBlock()
        const val WORLD_COAL_META = 0
        const val WORLD_MALACHITE_META = 1
        const val WORLD_CASSITERITE_META = 2
        const val WORLD_HEMATITE_META = 3
    }

    override fun getDrops(world: World, x: Int, y: Int, z: Int, meta: Int, fortune: Int): ArrayList<ItemStack> {
        return if (meta == WORLD_COAL_META) arrayListOf(ItemStack(Items.coal, world.rand.nextInt(8)))
        else super.getDrops(world, x, y, z, meta, fortune)
    }
}
