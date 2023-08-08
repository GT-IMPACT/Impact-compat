@file:Suppress("LeakingThis")

package space.impact.impact_compat.common.blocks

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.GregTech_API
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.BaseTileEntity
import gregtech.api.util.GT_Utility
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import software.bernie.geckolib3.item.GeoItemBlock
import space.impact.impact_compat.MODID
import space.impact.impact_compat.common.tiles.base.BaseCompatTileEntity

abstract class ModelBlockBase(
    private val blockName: String,
    material: Material,
    item: Class<out ItemBlock> = GeoItemBlock::class.java,
    isDefaultSettings: Boolean = true
) : Block(material) {

    companion object {
        protected const val WRENCH = "wrench"
    }

    init {
        if (isDefaultSettings) {
            super.setHarvestLevel(WRENCH, 1)
            setHardness(5.0f)
            setResistance(6.0f)
        }
        setBlockName(blockName)
        GameRegistry.registerBlock(this, item, blockName)
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

    override fun onBlockPlacedBy(aWorld: World, aX: Int, aY: Int, aZ: Int, aPlayer: EntityLivingBase?, aStack: ItemStack?) {
        val tTileEntity = aWorld.getTileEntity(aX, aY, aZ)
        if (tTileEntity !is BaseCompatTileEntity) return
        tTileEntity.setFrontFacing(BaseTileEntity.getSideForPlayerPlacing(aPlayer, ForgeDirection.UP, tTileEntity.getValidFacings()))
    }

    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    override fun onBlockClicked(aWorld: World, aX: Int, aY: Int, aZ: Int, aPlayer: EntityPlayer?) {
        val tTileEntity = aWorld.getTileEntity(aX, aY, aZ)
        if (tTileEntity is IGregTechTileEntity) {
            tTileEntity.onLeftclick(aPlayer)
        }
    }

    override fun onBlockActivated(
        aWorld: World, aX: Int, aY: Int, aZ: Int, aPlayer: EntityPlayer, ordinalSide: Int,
        aOffsetX: Float, aOffsetY: Float, aOffsetZ: Float
    ): Boolean {
        val tTileEntity = aWorld.getTileEntity(aX, aY, aZ) ?: return false
        if (aPlayer.isSneaking) {
            val tCurrentItem = aPlayer.inventory.getCurrentItem()
            if (tCurrentItem != null && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWrenchList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWireCutterList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSolderingToolList)
            ) return false
        }
        if (tTileEntity is IGregTechTileEntity) {
            return if (!aWorld.isRemote && !tTileEntity.isUseableByPlayer(aPlayer)) true
            else tTileEntity.onRightclick(aPlayer, ForgeDirection.getOrientation(ordinalSide), aOffsetX, aOffsetY, aOffsetZ)
        }
        return false
    }
}
