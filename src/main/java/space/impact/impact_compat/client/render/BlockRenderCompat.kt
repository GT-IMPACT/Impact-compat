package space.impact.impact_compat.client.render

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.util.ForgeDirection.*
import org.lwjgl.opengl.GL11
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer
import software.bernie.geckolib3.util.MatrixStack
import space.impact.impact_compat.client.interfaces.IRenderedModel
import space.impact.impact_compat.client.models.Models
import space.impact.impact_compat.common.tiles.base.BaseCompatTileEntity

@Suppress("UNCHECKED_CAST")
@SideOnly(Side.CLIENT)
class BlockRenderCompat<T>(provider: Models<T>) : GeoBlockRenderer<T>(provider) where T : TileEntity, T : IAnimatable {
    override fun renderEarly(animatable: T, poseStack: MatrixStack, partialTick: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        if (animatable is BaseCompatTileEntity) {
            rotate(animatable.getFrontFacing().ordinal, poseStack)
        } else {
            rotate(animatable.getBlockMetadata(), poseStack)
        }
        super.renderEarly(animatable, poseStack, partialTick, red, green, blue, alpha)
    }

    private fun rotate(faceOrdinal: Int, poseStack: MatrixStack) {
        when (faceOrdinal) {
            NORTH.ordinal -> rotateBlock(EnumFacing.NORTH, poseStack)
            SOUTH.ordinal -> rotateBlock(EnumFacing.SOUTH, poseStack)
            WEST.ordinal -> rotateBlock(EnumFacing.WEST, poseStack)
            EAST.ordinal -> rotateBlock(EnumFacing.EAST, poseStack)
            else -> Unit
        }
    }

    override fun renderTileEntityAt(te: TileEntity, x: Double, y: Double, z: Double, partialTick: Float) {
        this.render(te as T, MATRIX_STACK, x.toFloat(), y.toFloat(), z.toFloat(), partialTick)
    }
}

@SideOnly(Side.CLIENT)
class BlockItemRenderCompat<T>(provider: Models<T>) : GeoItemRenderer<T>(provider) where T : IAnimatable, T : Item {
    override fun renderEarly(animatable: T, poseStack: MatrixStack, partialTick: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        (geoModelProvider as? IRenderedModel)?.scaleItem()?.also { scale ->
            GL11.glScalef(scale.x, scale.y, scale.z)
        }
        super.renderEarly(animatable, poseStack, partialTick, red, green, blue, alpha)
    }
}
