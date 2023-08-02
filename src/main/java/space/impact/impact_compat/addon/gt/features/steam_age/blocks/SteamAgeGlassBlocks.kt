@file:Suppress("LeakingThis")

package space.impact.impact_compat.addon.gt.features.steam_age.blocks

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import space.impact.impact_compat.addon.gt.base.block.StructureCasingBlockBase
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.common.util.translate.Translate.translate

class SteamAgeGlassBlocks : StructureCasingBlockBase() {

    companion object {
        val INSTANCE = SteamAgeGlassBlocks()
        const val META_BRONZE_GLASS_MACHINE_CASING = 0
        private const val NAME = "compat.steam_age.glass"
        private const val BLOCK_COUNT = 1
    }

    init {
        setBlockName(NAME)
        setHardness(5.0f)
        setResistance(6.0f)
        setStepSound(soundTypeGlass)
        GameRegistry.registerBlock(this, SteamAgeGlassBlocksItem::class.java, NAME)
    }

    override fun getSubBlocks(par1: Item?, par2CreativeTabs: CreativeTabs?, list: MutableList<Any?>?) {
        repeat(BLOCK_COUNT) { list?.add(ItemStack(par1, 1, it)) }
    }

    override fun getIcon(side: Int, meta: Int): IIcon? {
        return when (meta) {
            META_BRONZE_GLASS_MACHINE_CASING -> CompatTextures.CASE_GLASS_BRONZE.icon
            else -> null
        }
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(
        worldClient: IBlockAccess, xCoord: Int, yCoord: Int, zCoord: Int,
        aSide: Int
    ): Boolean {
        return if (worldClient.getBlock(xCoord, yCoord, zCoord) == this) false
        else super.shouldSideBeRendered(worldClient, xCoord, yCoord, zCoord, aSide)
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBlockPass(): Int = 1
    override fun isOpaqueCube(): Boolean = false
    override fun renderAsNormalBlock(): Boolean = false
    override fun canSilkHarvest(): Boolean = false

    class SteamAgeGlassBlocksItem(block: Block) : ItemBlock(block) {

        companion object {
            private const val NO_MOB = "gt.nomobspawnsonthisblock"
            private const val NOT_TE = "gt.notileentityinthisblock"
        }

        override fun getMetadata(meta: Int): Int = meta
        override fun getHasSubtypes(): Boolean = true
        override fun getUnlocalizedName(stack: ItemStack): String = super.getUnlocalizedName() + "." + stack.itemDamage
        override fun addInformation(aStack: ItemStack, aPlayer: EntityPlayer?, aList: MutableList<Any?>, af3H: Boolean) {
            aList.add(NO_MOB.translate())
            aList.add(NOT_TE.translate())
        }
    }
}
