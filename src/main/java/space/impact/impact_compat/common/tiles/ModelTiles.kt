package space.impact.impact_compat.common.tiles

import space.impact.impact_compat.common.tiles.BaseTileEntityModel.Companion.registerTEModel
import space.impact.impact_compat.common.tiles.special.SteamRotorTE

fun registerTEModels() {
    registerTEModel(WaterWhealTE::class.java, "water_wheal")
    registerTEModel(WindWhealTE::class.java, "wind_wheal")
    registerTEModel(SteamRotorTE::class.java, "steam_rotor")
}

class WaterWhealTE : BaseTileRotationEntityModel()

class WindWhealTE : BaseTileRotationEntityModel()


