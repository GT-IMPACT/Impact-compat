package space.impact.impact_compat.common.worldgen

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import space.impact.impact_compat.addon.gt.items.CompatBlocks

object WorldGenInit {

    val sWorldGenList: ArrayList<CompatWorldGenBase> = ArrayList()
    val sWorldOreGenList: ArrayList<CompatWorldGenBase> = ArrayList()
    val sBlockReplaceableList: ArrayList<Block> = ArrayList()
    val sBlockUnlocalizedReplaceableList: ArrayList<String> = ArrayList()

    fun init() {
        initBlockReplace()
        CompatWorldGenerator()

        CompatWorldGenOreLayer("ore.nothing", true, 0, 256, 120, 8, 32, Blocks.air, 0, 0)

        CompatWorldGenOreLayer("ore.coal", true, 50, 200, 15, 8, 32, CompatBlocks.WORLD_COAL.block!!, CompatBlocks.WORLD_COAL.meta, 0)
        CompatWorldGenOreLayer("ore.cassiterite", true, 60, 150, 8, 4, 26, CompatBlocks.WORLD_CASSITERITE.block!!, CompatBlocks.WORLD_CASSITERITE.meta, 0)
        CompatWorldGenOreLayer("ore.malachite", true, 80, 170, 10, 4, 26, CompatBlocks.WORLD_MALACHITE.block!!, CompatBlocks.WORLD_MALACHITE.meta, 0)
        CompatWorldGenOreLayer("ore.hematite", true, 90, 200, 12, 5, 26, CompatBlocks.WORLD_HEMATITE.block!!, CompatBlocks.WORLD_HEMATITE.meta, 0)
    }

    private fun initBlockReplace() {
        sBlockReplaceableList += listOf(
            Blocks.stone,
            Blocks.hardened_clay,
            Blocks.stained_hardened_clay,
        )
        sBlockUnlocalizedReplaceableList += listOf(
            "tile.igneousStone",
            "tile.igneousStone",
            "tile.sedimentaryStone",
        )
    }
}
