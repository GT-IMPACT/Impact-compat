package space.impact.impact_compat.addon.gt.util.textures

import gregtech.api.enums.Textures
import gregtech.api.util.GT_Utility

const val HATCH_INDEX_MACHINE_CASE_BRONZE = 256

private const val PAGE_2 = 2

fun initTexturePage2() {
    if (!GT_Utility.addTexturePage(PAGE_2.toByte())) throw IndexOutOfBoundsException("Texture page $PAGE_2 already added")
    Textures.BlockIcons.casingTexturePages[PAGE_2][0] = CompatTextures.CASE_MACHINE_BRONZE.factory()
}