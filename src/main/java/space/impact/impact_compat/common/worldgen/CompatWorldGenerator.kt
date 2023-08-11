@file:Suppress("NAME_SHADOWING")

package space.impact.impact_compat.common.worldgen

import cpw.mods.fml.common.IWorldGenerator
import cpw.mods.fml.common.registry.GameRegistry
import gregtech.api.objects.XSTR
import net.minecraft.world.ChunkCoordIntPair
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*

class CompatWorldGenerator : IWorldGenerator {

    init {
        GameRegistry.registerWorldGenerator(this, 1073741823)
    }

    override fun generate(
        random: Random, chunkX: Int, chunkZ: Int, world: World,
        chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider
    ) {
        val tempDimensionId: Int = world.provider.dimensionId
        WorldGenContainer(
            chunkX * 16, chunkZ * 16, tempDimensionId, world, chunkGenerator, chunkProvider,
            world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8).biomeName,
        ).run()
    }
}

class WorldGenContainer(
    private var mX: Int, private var mZ: Int, private val mDimensionType: Int, private val mWorld: World,
    private val mChunkGenerator: IChunkProvider, private val mChunkProvider: IChunkProvider,
    private val mBiome: String
) : Runnable {

    companion object {
        var mGenerated = HashSet<ChunkCoordIntPair>(2000)
    }

    private fun getVeinCenterCoordinate(c: Int): Int {
        var c = c
        c += if (c < 0) 1 else 3
        return c - c % 3 - 2
    }

    private fun surroundingChunksLoaded(xCenter: Int, zCenter: Int): Boolean {
        return mWorld.checkChunksExist(xCenter - 16, 0, zCenter - 16, xCenter + 16, 0, zCenter + 16)
    }

    private fun getRandom(xChunk: Int, zChunk: Int): Random {
        val worldSeed = mWorld.seed
        val fmlRandom = Random(worldSeed)
        val xSeed = fmlRandom.nextLong() shr (2 + 1L).toInt()
        val zSeed = fmlRandom.nextLong() shr (2 + 1L).toInt()
        val chunkSeed = xSeed * xChunk + zSeed * zChunk xor worldSeed
        fmlRandom.setSeed(chunkSeed)
        return XSTR(fmlRandom.nextInt().toLong())
    }

    override fun run() {
        var xCenter = getVeinCenterCoordinate(mX shr 4)
        var zCenter = getVeinCenterCoordinate(mZ shr 4)
        val random = getRandom(xCenter, zCenter)
        xCenter = xCenter shl 4
        zCenter = zCenter shl 4

        val centerChunk = ChunkCoordIntPair(xCenter, zCenter)

        if (!mGenerated.contains(centerChunk) && surroundingChunksLoaded(xCenter, zCenter)) {
            mGenerated.add(centerChunk)

            //WORLD GEN ORES
            if (CompatWorldGenOreLayer.sWeight > 0 && WorldGenInit.sWorldOreGenList.isNotEmpty()) {
                var tRandomWeight: Int
                loop@for (i in 0..256) {
                    tRandomWeight = random.nextInt(CompatWorldGenOreLayer.sWeight)
                    for (tWorldGen in WorldGenInit.sWorldOreGenList) {
                        tRandomWeight -= (tWorldGen as CompatWorldGenOreLayer).weight
                        if (tRandomWeight <= 0) {
                            try {
                                if (tWorldGen.executeWorldGen(mWorld, random, mBiome, mDimensionType, xCenter, zCenter, mChunkGenerator, mChunkProvider)) {
                                    break@loop
                                }
                                break
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            //WORLD GEN DEFAULT
            var i = 0
            var tX = xCenter - 16
            while (i < 3) {
                var j = 0
                var tZ = zCenter - 16
                while (j < 3) {
                    try {
                        for (tWorldGen in WorldGenInit.sWorldGenList) {
                            tWorldGen.executeWorldGen(mWorld, random, mBiome, mDimensionType, tX, tZ, mChunkGenerator, mChunkProvider)
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    j++
                    tZ += 16
                }
                i++
                tX += 16
            }
        }
        val tChunk = mWorld.getChunkFromBlockCoords(mX, mZ)
        if (tChunk != null) {
            tChunk.isModified = true
        }
    }
}
