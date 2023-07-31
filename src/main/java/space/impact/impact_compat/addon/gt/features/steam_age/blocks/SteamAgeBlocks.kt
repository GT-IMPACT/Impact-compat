@file:Suppress("LeakingThis")

package space.impact.impact_compat.addon.gt.features.steam_age.blocks

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import space.impact.impact_compat.addon.gt.base.block.StructureCasingBlockBase
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.common.util.translate.Translate.translate

class SteamAgeBlocks : StructureCasingBlockBase() {

    companion object {
        val INSTANCE = SteamAgeBlocks()
        const val META_BRONZE_MACHINE_CASING = 0

        private const val NAME = "compat.steam_age.block"
        private const val BLOCK_COUNT = 1
    }

    init {
        setBlockName(NAME)
        setHardness(5.0f)
        setResistance(6.0f)
        GameRegistry.registerBlock(this, SteamAgeBlocksItem::class.java, NAME)
    }

    override fun getSubBlocks(par1: Item?, par2CreativeTabs: CreativeTabs?, list: MutableList<Any?>?) {
        repeat(BLOCK_COUNT) { list?.add(ItemStack(par1, 1, it)) }
    }

    override fun getIcon(side: Int, meta: Int): IIcon? {
        return when(meta) {
            META_BRONZE_MACHINE_CASING -> CompatTextures.MACHINE_CASE_BRONZE.icon
            else -> null
        }
    }

    class SteamAgeBlocksItem(block: Block) : ItemBlock(block) {

        companion object {
            private const val NO_MOB = "gt.nomobspawnsonthisblock"
            private const val NOT_TE = "gt.notileentityinthisblock"
        }

        override fun getMetadata(meta: Int): Int = meta
        override fun getHasSubtypes(): Boolean = true
        override fun getUnlocalizedName(stack: ItemStack): String = super.getUnlocalizedName() + "." + stack.itemDamage
        override fun addInformation(aStack: ItemStack?, aPlayer: EntityPlayer?, aList: MutableList<Any?>, af3H: Boolean) {
            aList.addAll(listOf(NO_MOB.translate(), NOT_TE.translate()))
        }
    }
}