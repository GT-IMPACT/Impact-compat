package space.impact.impact_compat.common.worldgen

import net.minecraft.block.Block
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import space.impact.impact_compat.common.worldgen.WorldGenHelper.replaceableBlocks
import java.util.*

class CompatWorldGenSphere(
    name: String, isDefault: Boolean,
    block: Block, meta: Int, amount: Int,
    size: Int, minY: Int, maxY: Int,
    probability: Int, dimensionType: Int,
    biomeList: Collection<String> = emptyList(), allowGenVoid: Boolean = false,
) : CompatWorldGenBlocks(name, isDefault, block, meta, amount, size, minY, maxY, probability, dimensionType, biomeList, allowGenVoid) {

    override fun executeWorldGen(
        aWorld: World, aRandom: Random,
        aBiome: String, aDimensionType: Int,
        aChunkX: Int, aChunkZ: Int,
        aChunkGenerator: IChunkProvider, aChunkProvider: IChunkProvider
    ): Boolean {
        if ((isGenerationAllowed(aWorld, aDimensionType, dimensionType)) &&
            ((biomeList.isEmpty()) || (biomeList.contains(aBiome))) &&
            ((probability <= 1) || (aRandom.nextInt(probability) == 0))
        ) {
            for (i in 0 until amount) {
                val tX = aChunkX + aRandom.nextInt(16)
                val tY: Int = minY + aRandom.nextInt(maxY - minY)
                val tZ = aChunkZ + aRandom.nextInt(16)

                if ((allowGenVoid) || (!aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
                    val math_pi = Math.PI.toFloat()
                    val var6 = aRandom.nextFloat() * math_pi
                    val var1d: Float = this.size / 8.0f
                    val var2d = tX + 8
                    val var3d = tZ + 8
                    val var4d = tY - 2
                    val mh_s_0 = MathHelper.sin(var6) * var1d
                    val mh_c_0 = MathHelper.cos(var6) * var1d
                    val var7 = var2d + mh_s_0
                    val var11 = var3d + mh_c_0
                    val var15r = aRandom.nextInt(3)
                    val var17r = aRandom.nextInt(3)
                    val var15 = var4d + var15r
                    val mh_n_4 = var17r - var15r
                    val mh_n_0 = -2 * mh_s_0
                    val mh_n_1 = -2 * mh_c_0

                    for (var19 in 0..size) {
                        val var5d: Float = (var19 / this.size).toFloat()
                        val var20 = var7 + mh_n_0 * var5d
                        val var22 = var15 + mh_n_4 * var5d
                        val var24 = var11 + mh_n_1 * var5d
                        val var6d: Float = var19 * math_pi / this.size
                        val var26: Float = aRandom.nextFloat() * this.size / 16.0f
                        val var28 = ((MathHelper.sin(var6d) + 1.0f) * var26 + 1.0f) / 2.0f

                        val tMinX = MathHelper.floor_float(var20 - var28)
                        val tMinY = MathHelper.floor_float(var22 - var28)
                        val tMinZ = MathHelper.floor_float(var24 - var28)
                        val tMaxX = MathHelper.floor_float(var20 + var28)
                        val tMaxY = MathHelper.floor_float(var22 + var28)
                        val tMaxZ = MathHelper.floor_float(var24 + var28)

                        for (eX in tMinX..tMaxX) {
                            val var39 = (eX + 0.5f - var20) / var28
                            val var10d = var39 * var39
                            if (var10d < 1.0f) {
                                for (eY in tMinY..tMaxY) {
                                    val var42 = (eY + 0.5f - var22) / var28
                                    val var12d = var10d + var42 * var42
                                    if (var12d < 1.0f) {
                                        for (eZ in tMinZ..tMaxZ) {
                                            val var45 = (eZ + 0.5f - var24) / var28
                                            if (var12d + var45 * var45 < 1.0f) {
                                                val tTargetedBlock = aWorld.getBlock(eX, eY, eZ)
                                                if (this.allowGenVoid && aWorld.getBlock(eX, eY, eZ).isAir(aWorld, eX, eY, eZ) || tTargetedBlock != null && tTargetedBlock.replaceableBlocks(aWorld, eX, eY, eZ)) {
                                                    aWorld.setBlock(eX, eY, eZ, this.block, this.meta, 0)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true
        }
        return false
    }
}
