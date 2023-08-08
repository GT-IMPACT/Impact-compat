package space.impact.impact_compat.addon.gt.util.textures

import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.render.TextureFactory
import gregtech.api.util.GT_Utility
import net.minecraft.init.Blocks

enum class HatchTexture(page: Int, val index: Int, texture: ITexture?) {

    MACHINE_CAGE_BRONZE(PAGE_2, 256, CompatTextures.CASE_MACHINE_BRONZE.factory()),
    MACHINE_CASE_BRONZE_BRICK(PAGE_2, 257, CompatTextures.CASE_BRONZE_BRICK.factory()),
    MACHINE_CASE_PRIMITIVE_BRICK(PAGE_2, 258, CompatTextures.CASE_PRIMITIVE_SMELTER.factory()),
    UNKNOWN(0, 0, null);

    init {
        if (texture != null) Textures.BlockIcons.casingTexturePages[page][ordinal] = texture
    }
}

fun initTexturePage2() {
    if (!GT_Utility.addTexturePage(PAGE_2.toByte()))
        throw IndexOutOfBoundsException("Texture page $PAGE_2 already added")
    HatchTexture.values()
}

private const val PAGE_2 = 2
