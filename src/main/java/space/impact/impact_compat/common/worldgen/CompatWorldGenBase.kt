package space.impact.impact_compat.common.worldgen

import gregtech.api.GregTech_API
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

abstract class CompatWorldGenBase(val name: String, aList: ArrayList<CompatWorldGenBase>, isDefault: Boolean) {

    private val mDimensionMap: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap()

    init {
        if (isDefault) aList.add(this)
    }

    open fun executeWorldGen(
        aWorld: World, aRandom: Random,
        aBiome: String, aDimensionType: Int,
        aChunkX: Int, aChunkZ: Int,
        aChunkGenerator: IChunkProvider, aChunkProvider: IChunkProvider
    ): Boolean {
        return false
    }

    open fun executeCaveGen(
        aWorld: World, aRandom: Random,
        aBiome: String, aDimensionType: Int,
        aChunkX: Int, aChunkZ: Int,
        aChunkGenerator: IChunkProvider, aChunkProvider: IChunkProvider
    ): Boolean {
        return false
    }

    fun isGenerationAllowed(aWorld: World, aDimensionType: Int, aAllowedDimensionType: Int): Boolean {
        val aDimName = aWorld.provider.dimensionName
        val tAllowed = mDimensionMap[aDimName]
        if (tAllowed == null) {
            val isAllowed = aDimensionType == aAllowedDimensionType
            mDimensionMap[aDimName] = isAllowed
            return isAllowed
        }
        return tAllowed
    }
}

abstract class CompatWorldGenBlocks(
    name: String, isDefault: Boolean,
    val block: Block, val meta: Int, val amount: Int,
    val size: Int, val minY: Int, val maxY: Int,
    val probability: Int, val dimensionType: Int,
    val biomeList: Collection<String>, val allowGenVoid: Boolean,
) : CompatWorldGenBase(name, WorldGenInit.sWorldGenList, isDefault)
