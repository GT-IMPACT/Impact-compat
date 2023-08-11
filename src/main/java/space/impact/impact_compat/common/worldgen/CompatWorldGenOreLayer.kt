package space.impact.impact_compat.common.worldgen

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*
import kotlin.math.max

class CompatWorldGenOreLayer(
    name: String, isDefault: Boolean,
    val minY: Int, val maxY: Int,
    val weight: Int, val density: Int, val size: Int,
    val block: Block, val meta: Int, val dimensionId: Int
) : CompatWorldGenBase(name, WorldGenInit.sWorldOreGenList, isDefault) {

    companion object {
        var sWeight = 0
    }

    init {
        sWeight += weight
    }

    override fun executeWorldGen(
        aWorld: World, aRandom: Random,
        aBiome: String, aDimensionType: Int,
        aChunkX: Int, aChunkZ: Int,
        aChunkGenerator: IChunkProvider, aChunkProvider: IChunkProvider
    ): Boolean {
        if (!isGenerationAllowed(aWorld, aDimensionType, dimensionId)) return false
        val tMinY = this.minY + aRandom.nextInt(this.maxY - this.minY - 5)
        if (block != Blocks.air) {
            val cX = aChunkX - aRandom.nextInt(this.size)
            val eX = aChunkX + 16 + aRandom.nextInt(this.size)
            for (tX in cX..eX) {
                val cZ = aChunkZ - aRandom.nextInt(this.size)
                val eZ = aChunkZ + 16 + aRandom.nextInt(this.size)
                for (tZ in cZ..eZ) {
                    //Layer1
                    for (i in tMinY - 1 until tMinY + 16) {
                        if (aRandom.nextInt(max(1, (max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.density))) == 0 ||
                            aRandom.nextInt(max(1, (max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.density))) == 0
                        ) {
                            WorldGenHelper.setOreBlock(aWorld, tX, i, tZ, block, meta)
                        }
                    }
                    //Layer2
                    if ((aRandom.nextInt(max(1, (max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.density))) == 0 ||
                                aRandom.nextInt(max(1, (max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.density))) == 0)
                    ) {
                        WorldGenHelper.setOreBlock(aWorld, tX, tMinY + 2 + aRandom.nextInt(2), tZ, block, meta)
                    }

                    //Layer3
                    for (i in tMinY + 3 until tMinY + 10) {
                        if (aRandom.nextInt(max(1, (max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.density))) == 0 ||
                            aRandom.nextInt(max(1, (max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.density))) == 0
                        ) {
                            WorldGenHelper.setOreBlock(aWorld, tX, i, tZ, block, meta)
                        }
                    }
                    //Layer4
                    if ((aRandom.nextInt(max(1, (max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.density))) == 0 ||
                                aRandom.nextInt(max(1, (max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.density))) == 0)
                    ) {
                        WorldGenHelper.setOreBlock(aWorld, tX, tMinY - 1 + aRandom.nextInt(7), tZ, block, meta)
                    }
                }
            }
        }
        println("Generated Orevein: $name $aChunkX $tMinY $aChunkZ")
        return true
    }
}
