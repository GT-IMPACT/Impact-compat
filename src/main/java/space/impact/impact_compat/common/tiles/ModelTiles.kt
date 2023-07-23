package space.impact.impact_compat.common.tiles

import space.impact.impact_compat.common.tiles.BaseTileEntityModel.Companion.registerTEModel

fun registerTEModels() {
    registerTEModel(WaterWhealTE::class.java, "water_wheal")
    registerTEModel(WindWhealTE::class.java, "wind_wheal")
}

class WaterWhealTE : BaseTileRotationEntityModel()

class WindWhealTE : BaseTileRotationEntityModel()
