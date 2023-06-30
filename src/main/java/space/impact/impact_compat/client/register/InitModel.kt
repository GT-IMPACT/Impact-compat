package space.impact.impact_compat.client.register

import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.client.models.WaterWhealModel
import space.impact.impact_compat.client.models.WindWhealModel
import space.impact.impact_compat.client.render.BlockItemRenderCompat
import space.impact.impact_compat.client.render.BlockRenderCompat
import space.impact.impact_compat.common.tiles.WaterWhealTE
import space.impact.impact_compat.common.tiles.WindWhealTE
import space.impact.impact_compat.common.util.forge.RenderModelUtil

fun initModels() {
    RenderModelUtil.registerRenderModel(
        teClass = WaterWhealTE::class.java, renderTE = BlockRenderCompat(WaterWhealModel()),
        item = CompatBlocks.WATER_WHEAL_BLOCK.item, renderItem = BlockItemRenderCompat(WaterWhealModel()),
    )
    RenderModelUtil.registerRenderModel(
        teClass = WindWhealTE::class.java, renderTE = BlockRenderCompat(WindWhealModel()),
        item = CompatBlocks.WIND_WHEAL_BLOCK.item, renderItem = BlockItemRenderCompat(WindWhealModel()),
    )
}
