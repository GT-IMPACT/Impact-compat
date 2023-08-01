package space.impact.impact_compat.common.init.tileentities

import space.impact.impact_compat.common.tiles.BaseTileRotationEntityModel
import space.impact.impact_compat.common.tiles.special.SteamRotorTE
import space.impact.impact_compat.common.util.forge.RegisterUtil

fun registerTEModels() {
    RegisterUtil.registerTE(WaterWhealTE::class.java, "water_wheal")
    RegisterUtil.registerTE(WindWhealTE::class.java, "wind_wheal")
    RegisterUtil.registerTE(SteamRotorTE::class.java, "steam_rotor")
}

class WaterWhealTE : BaseTileRotationEntityModel()

class WindWhealTE : BaseTileRotationEntityModel()
