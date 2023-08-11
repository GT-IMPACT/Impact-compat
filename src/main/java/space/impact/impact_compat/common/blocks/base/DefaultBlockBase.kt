@file:Suppress("LeakingThis")

package space.impact.impact_compat.common.blocks.base

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import space.impact.impact_compat.MODID

open class DefaultBlockBase(
    private val blockName: String,
    private val count: Int,
    material: Material,
    isDefaultSettings: Boolean = true
) : Block(material) {

    companion object {
        protected const val TOOL = "pickaxe"
    }

    private val icons: Array<IIcon?> = arrayOfNulls(count)

    init {
        if (isDefaultSettings) {
            setHarvestLevel(TOOL, 1)
            setHardness(1.5F)
            setResistance(10.0F)
            setStepSound(soundTypeStone)
        }
        setBlockName(blockName)
        GameRegistry.registerBlock(this, DefaultItemBlock::class.java, blockName)
    }


    @SideOnly(Side.CLIENT)
    override fun registerBlockIcons(reg: IIconRegister) {
        repeat(count) {
            icons[it] = reg.registerIcon("$MODID:${blockName}/$it")
        }
    }

    override fun damageDropped(meta: Int): Int {
        return meta
    }

    @SideOnly(Side.CLIENT)
    override fun getIcon(side: Int, meta: Int): IIcon? {
        return icons[meta]
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(par1: Item?, par2CreativeTabs: CreativeTabs?, list: MutableList<Any?>?) {
        repeat(count) { list?.add(ItemStack(par1, 1, it)) }
    }

    class DefaultItemBlock(block: Block) : ItemBlock(block) {
        override fun getMetadata(meta: Int): Int = meta
        override fun getHasSubtypes(): Boolean = true
        override fun getUnlocalizedName(stack: ItemStack): String = super.getUnlocalizedName() + "." + stack.itemDamage
    }

    fun register(meta: Int): ItemStack {
        return ItemStack(this, 1, meta)
    }
}
