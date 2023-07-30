package space.impact.impact_compat.client.register

import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.client.models.Models

import space.impact.impact_compat.client.render.BlockItemRenderCompat
import space.impact.impact_compat.client.render.BlockRenderCompat
import space.impact.impact_compat.common.tiles.WaterWhealTE
import space.impact.impact_compat.common.tiles.WindWhealTE
import space.impact.impact_compat.common.tiles.special.SteamRotorTE
import space.impact.impact_compat.common.util.forge.RenderModelUtil

fun initModels() {
    RenderModelUtil.registerRenderModel(
        teClass = WaterWhealTE::class.java, renderTE = BlockRenderCompat(Models.WaterWhealModel()),
        item = CompatBlocks.WATER_WHEAL_BLOCK.item, renderItem = BlockItemRenderCompat(Models.WaterWhealModel()),
    )
    RenderModelUtil.registerRenderModel(
        teClass = WindWhealTE::class.java, renderTE = BlockRenderCompat(Models.WindWhealModel()),
        item = CompatBlocks.WIND_WHEAL_BLOCK.item, renderItem = BlockItemRenderCompat(Models.WindWhealModel()),
    )

    RenderModelUtil.registerRenderModel(
        teClass = SteamRotorTE::class.java, renderTE = BlockRenderCompat(Models.SteamRotorModel()),
        item = CompatBlocks.STEAM_ROTOR_BLOCK.item, renderItem = BlockItemRenderCompat(Models.SteamRotorModel()),
    )
}
