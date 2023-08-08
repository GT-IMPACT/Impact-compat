package space.impact.impact_compat.client.register

import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.client.models.Models

import space.impact.impact_compat.client.render.BlockItemRenderCompat
import space.impact.impact_compat.client.render.BlockRenderCompat
import space.impact.impact_compat.common.tiles.models.SmelterContentModelTile
import space.impact.impact_compat.common.tiles.models.SteamRotorModelTile
import space.impact.impact_compat.common.tiles.models.WaterRotorModelTile
import space.impact.impact_compat.common.tiles.models.WindRotorModelTile
import space.impact.impact_compat.common.util.forge.RenderModelUtil

fun initModels() {
    RenderModelUtil.registerRenderModel(
        teClass = WaterRotorModelTile::class.java, renderTE = BlockRenderCompat(Models.WaterWhealModel()),
        item = CompatBlocks.WATER_WHEAL_BLOCK.item, renderItem = BlockItemRenderCompat(Models.WaterWhealModel()),
    )
    RenderModelUtil.registerRenderModel(
        teClass = WindRotorModelTile::class.java, renderTE = BlockRenderCompat(Models.WindWhealModel()),
        item = CompatBlocks.WIND_WHEAL_BLOCK.item, renderItem = BlockItemRenderCompat(Models.WindWhealModel()),
    )

    RenderModelUtil.registerRenderModel(
        teClass = SteamRotorModelTile::class.java, renderTE = BlockRenderCompat(Models.SteamRotorModel()),
        item = CompatBlocks.STEAM_ROTOR_BLOCK.item, renderItem = BlockItemRenderCompat(Models.SteamRotorModel()),
    )
    RenderModelUtil.registerRenderModel(
        teClass = SmelterContentModelTile::class.java, renderTE = BlockRenderCompat(Models.SmelterContentModel()),
        item = CompatBlocks.SMELTER_CONTENT_BLOCK.item, renderItem = BlockItemRenderCompat(Models.SmelterContentModel()),
    )
}
