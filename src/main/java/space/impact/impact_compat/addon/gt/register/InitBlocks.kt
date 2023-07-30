package space.impact.impact_compat.addon.gt.register

import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.common.blocks.SteamRotorBlock
import space.impact.impact_compat.common.blocks.WaterWhealBlock
import space.impact.impact_compat.common.blocks.WindWhealBlock

fun initBlocks() {
    CompatBlocks.WATER_WHEAL_BLOCK.set(WaterWhealBlock.INSTANCE)
    CompatBlocks.WIND_WHEAL_BLOCK.set(WindWhealBlock.INSTANCE)
    CompatBlocks.STEAM_ROTOR_BLOCK.set(SteamRotorBlock.INSTANCE)
}
