package space.impact.impact_compat.client.render

import gregtech.api.enums.Textures
import gregtech.api.util.GT_Utility
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper
import net.minecraftforge.client.MinecraftForgeClient
import org.lwjgl.opengl.GL11
import space.impact.impact_compat.common.item.meta.MetaGeneratedItem
import space.impact.impact_compat.common.util.render.RenderUtil

class MetaGeneratedItemRenderer : IItemRenderer {

    init {
        for (item in MetaGeneratedItem.sInstances.values) {
            MinecraftForgeClient.registerItemRenderer(item, this)
        }
    }

    override fun handleRenderType(stack: ItemStack?, aType: ItemRenderType): Boolean {
        return if (GT_Utility.isStackInvalid(stack) || stack!!.itemDamage < 0) false
        else aType == ItemRenderType.EQUIPPED_FIRST_PERSON || aType == ItemRenderType.INVENTORY || aType == ItemRenderType.EQUIPPED || aType == ItemRenderType.ENTITY
    }

    override fun shouldUseRenderHelper(aType: ItemRenderType, stack: ItemStack?, aHelper: ItemRendererHelper?): Boolean {
        return if (GT_Utility.isStackInvalid(stack)) false else aType == ItemRenderType.ENTITY
    }

    override fun renderItem(type: ItemRenderType, stack: ItemStack, vararg data: Any?) {
        if (GT_Utility.isStackInvalid(stack)) return
        val meta = stack.itemDamage
        if (meta < 0) return

        val aItem = stack.item as MetaGeneratedItem
        GL11.glEnable(GL11.GL_BLEND)

        if (type == ItemRenderType.ENTITY) {
            if (RenderItem.renderInFrame) {
                GL11.glScalef(0.85f, 0.85f, 0.85f)
                GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
                GL11.glTranslated(-0.5, -0.42, 0.0)
            } else {
                GL11.glTranslated(-0.5, -0.42, 0.0)
            }
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f)


        if (meta < aItem.mOffset) {
            val aIcon = aItem.getIconContainer(meta)
            var tOverlay: IIcon? = null
            var fluidIcon: IIcon? = null
            val tIcon: IIcon?
            if (aIcon == null) {
                tIcon = stack.iconIndex
            } else {
                tIcon = aIcon.icon
                tOverlay = aIcon.overlayIcon
            }
            if (tIcon == null) {
                return
            }
            val tFluid = GT_Utility.getFluidForFilledItem(stack, true)
            if (tOverlay != null && tFluid != null && tFluid.getFluid() != null) {
                fluidIcon = tFluid.getFluid().getIcon(tFluid)
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            if (fluidIcon == null) {
                val tModulation = aItem.getRGBa(stack)
                GL11.glColor3f(tModulation[0] / 255.0f, tModulation[1] / 255.0f, tModulation[2] / 255.0f)
            }
            if (type == ItemRenderType.INVENTORY) {
                RenderUtil.renderItemIcon(tIcon, 16.0, 0.001, 0.0f, 0.0f, -1.0f)
            } else {
                ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.maxU, tIcon.minV, tIcon.minU, tIcon.maxV, tIcon.iconWidth, tIcon.iconHeight, 0.0625f)
            }
            if (fluidIcon != null) {
                val tColor = tFluid!!.getFluid().getColor(tFluid)
                GL11.glColor3f((tColor shr 16 and 0xFF) / 255.0f, (tColor shr 8 and 0xFF) / 255.0f, (tColor and 0xFF) / 255.0f)
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glDepthFunc(GL11.GL_EQUAL)
                if (type == ItemRenderType.INVENTORY) {
                    RenderUtil.renderItemIcon(fluidIcon, 16.0, 0.001, 0.0f, 0.0f, -1.0f)
                } else {
                    ItemRenderer.renderItemIn2D(Tessellator.instance, fluidIcon.maxU, fluidIcon.minV, fluidIcon.minU, fluidIcon.maxV, fluidIcon.iconWidth, fluidIcon.iconHeight, 0.0625f)
                }
                GL11.glDepthFunc(GL11.GL_LEQUAL)
            }
            GL11.glColor3f(1.0f, 1.0f, 1.0f)
            if (tOverlay != null) {
                bindTexture(type, tOverlay)
            }
        } else {
            val tIcon: IIcon? = if (aItem.mIconList[meta - aItem.mOffset].size > 1) {
                aItem.mIconList[meta - aItem.mOffset][0]
            } else {
                aItem.mIconList[meta - aItem.mOffset][0]
            }
            bindTexture(type, tIcon ?: Textures.ItemIcons.RENDERING_ERROR.icon)
        }

        GL11.glDisable(GL11.GL_BLEND)
    }

    companion object {
        private fun bindTexture(type: ItemRenderType, tOverlay: IIcon) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            if (type == ItemRenderType.INVENTORY) {
                RenderUtil.renderItemIcon(tOverlay, 16.0, 0.001, 0.0f, 0.0f, -1.0f)
            } else {
                ItemRenderer.renderItemIn2D(Tessellator.instance, tOverlay.maxU, tOverlay.minV, tOverlay.minU, tOverlay.maxV, tOverlay.iconWidth, tOverlay.iconHeight, 0.0625f)
            }
        }
    }
}
