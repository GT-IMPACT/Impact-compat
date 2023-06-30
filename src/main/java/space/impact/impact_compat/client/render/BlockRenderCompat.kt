package space.impact.impact_compat.client.render

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import org.lwjgl.opengl.GL11
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer
import software.bernie.geckolib3.util.MatrixStack
import space.impact.impact_compat.client.interfaces.IRenderedModel

@Suppress("UNCHECKED_CAST")
@SideOnly(Side.CLIENT)
class BlockRenderCompat<T>(provider: AnimatedGeoModel<T>) : GeoBlockRenderer<T>(provider) where T : TileEntity, T : IAnimatable {
    override fun renderEarly(animatable: T, poseStack: MatrixStack, partialTick: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        when (animatable.getBlockMetadata()) {
            2 -> rotateBlock(EnumFacing.NORTH, poseStack)
            3 -> rotateBlock(EnumFacing.SOUTH, poseStack)
            4 -> rotateBlock(EnumFacing.WEST, poseStack)
            5 -> rotateBlock(EnumFacing.EAST, poseStack)
        }
        super.renderEarly(animatable, poseStack, partialTick, red, green, blue, alpha)
    }

    override fun renderTileEntityAt(te: TileEntity, x: Double, y: Double, z: Double, partialTick: Float) {
        this.render(te as T, MATRIX_STACK, x.toFloat(), y.toFloat(), z.toFloat(), partialTick)
    }
}

@SideOnly(Side.CLIENT)
class BlockItemRenderCompat<T>(provider: AnimatedGeoModel<T>) : GeoItemRenderer<T>(provider) where T : IAnimatable, T : Item {
    override fun renderEarly(animatable: T, poseStack: MatrixStack, partialTick: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        (geoModelProvider as? IRenderedModel)?.scaleItem()?.also { scale ->
            GL11.glScalef(scale.x, scale.y, scale.z)
        }
        super.renderEarly(animatable, poseStack, partialTick, red, green, blue, alpha)
    }
}
