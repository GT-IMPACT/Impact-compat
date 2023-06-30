package space.impact.impact_compat.common.blocks

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.IIcon
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import software.bernie.geckolib3.item.GeoItemBlock
import space.impact.impact_compat.MODID

abstract class ModelBlockBase(
    private val blockName: String,
    material: Material,
) : Block(material) {

    init {
        setBlockName(blockName)
        GameRegistry.registerBlock(this, GeoItemBlock::class.java, blockName)
    }

    @SideOnly(Side.CLIENT)
    override fun registerBlockIcons(reg: IIconRegister) {
        blockIcon = reg.registerIcon("$MODID:$blockName")
    }

    @SideOnly(Side.CLIENT)
    override fun getIcon(side: Int, meta: Int): IIcon {
        return blockIcon
    }

    override fun renderAsNormalBlock(): Boolean {
        return false
    }

    override fun isOpaqueCube(): Boolean {
        return false
    }

    override fun getMobilityFlag(): Int {
        return 2
    }

    override fun getRenderType(): Int {
        return -1
    }

    override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase, stack: ItemStack) {
        when (MathHelper.floor_double(placer.rotationYaw.toDouble() * 4.0 / 360.0 + 0.5) and 3) {
            0 -> world.setBlockMetadataWithNotify(x, y, z, 2, 2)
            1 -> world.setBlockMetadataWithNotify(x, y, z, 5, 2)
            2 -> world.setBlockMetadataWithNotify(x, y, z, 3, 2)
            3 -> world.setBlockMetadataWithNotify(x, y, z, 4, 2)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }
}
