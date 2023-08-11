@file:Suppress("unused")

package space.impact.impact_compat.addon.rwg

import net.minecraft.world.biome.BiomeGenBase

enum class RwgBiomes(val id: String) {
    RWG_RIVER_ICE("rwg_riverIce"),
    RWG_RIVER_COLD("rwg_riverCold"),
    RWG_RIVER_TEMPERATE("rwg_riverTemperate"),
    RWG_RIVER_HOT("rwg_riverHot"),
    RWG_RIVER_WET("rwg_riverWet"),
    RWG_RIVER_OASIS("rwg_riverOasis"),
    RWG_OCEAN_ICE("rwg_oceanIce"),
    RWG_OCEAN_COLD("rwg_oceanCold"),
    RWG_OCEAN_TEMPERATE("rwg_oceanTemperate"),
    RWG_OCEAN_HOT("rwg_oceanHot"),
    RWG_OCEAN_WET("rwg_oceanWet"),
    RWG_OCEAN_OASIS("rwg_oceanOasis"),
    RWG_SNOW_DESERT("rwg_snowDesert"),
    RWG_SNOW_FOREST("rwg_snowForest"),
    RWG_COLD_PLAINS("rwg_coldPlains"),
    RWG_COLD_FOREST("rwg_coldForest"),
    RWG_HOT_PLAINS("rwg_hotPlains"),
    RWG_HOT_FOREST("rwg_hotForest"),
    RWG_HOT_DESERT("rwg_hotDesert"),
    RWG_PLAINS("rwg_plains"),
    RWG_TROPICAL_ISLAND("rwg_tropical"),
    RWG_REDWOOD("rwg_redwood"),
    RWG_JUNGLE("rwg_jungle"),
    RWG_OASIS("rwg_oasis"),
    RWG_TEMPERATE_FOREST("rwg_temperateForest");
}

fun BiomeGenBase.isRwgBiome(vararg biomes: RwgBiomes): Boolean {
    return biomes.any { it.id == biomeName }
}

fun BiomeGenBase.isRiverBiome(): Boolean {
    return isRwgBiome(
        RwgBiomes.RWG_RIVER_ICE, RwgBiomes.RWG_RIVER_COLD, RwgBiomes.RWG_RIVER_TEMPERATE,
        RwgBiomes.RWG_RIVER_HOT, RwgBiomes.RWG_RIVER_WET, RwgBiomes.RWG_RIVER_OASIS
    ) || this == BiomeGenBase.river || this == BiomeGenBase.frozenRiver
}

fun BiomeGenBase.isOceanBiome(): Boolean {
    return isRwgBiome(
        RwgBiomes.RWG_OCEAN_ICE, RwgBiomes.RWG_OCEAN_COLD, RwgBiomes.RWG_OCEAN_TEMPERATE,
        RwgBiomes.RWG_OCEAN_HOT, RwgBiomes.RWG_OCEAN_WET, RwgBiomes.RWG_OCEAN_OASIS
    ) || this == BiomeGenBase.deepOcean || this == BiomeGenBase.frozenOcean || this == BiomeGenBase.ocean
}