package space.impact.impact_compat.common.item

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.util.GT_LanguageManager
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import space.impact.impact_compat.MODID
import space.impact.impact_compat.common.item.meta.MetaBaseItem
import space.impact.impact_compat.core.Strings
import java.util.*

class CompatItemsPagination(name: String) : MetaBaseItem(name) {

    companion object {
        private const val COUNT_ITEMS = 32000
    }

    val mEnabledItems: BitSet
    val mVisibleItems: BitSet
    val mItemAmount: Short
    val mIconList: Array<Array<IIcon?>>

    init {
        setHasSubtypes(true)
        setMaxDamage(0)
        mEnabledItems = BitSet(COUNT_ITEMS)
        mVisibleItems = BitSet(COUNT_ITEMS)
        mItemAmount = COUNT_ITEMS.toShort()
        mIconList = Array(32000) { arrayOfNulls(1) }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(aItem: Item, aCreativeTab: CreativeTabs?, aList: MutableList<Any?>) {
        val j = mEnabledItems.length()
        for (i in 0 until j) {
            if (mVisibleItems[i] || mEnabledItems[i]) {
                val tStack = ItemStack(this, 1, i)
                isItemStackUsable(tStack)
                aList.add(tStack)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun registerIcons(aIconRegister: IIconRegister) {
        val j = mEnabledItems.length().toShort()
        for (i in 0 until j) {
            if (mEnabledItems[i]) {
                for (k in 1 until mIconList[i].size) {
                    mIconList[i][k] = aIconRegister.registerIcon("$MODID:$unlocalizedName/$i/$k")
                }
                mIconList[i][0] = aIconRegister.registerIcon("$MODID:$unlocalizedName/$i")
            }
        }
    }

    override fun getItemEnchantability(): Int {
        return 0
    }

    override fun isBookEnchantable(stack: ItemStack, aBook: ItemStack): Boolean {
        return false
    }

    override fun getIsRepairable(stack: ItemStack, aMaterial: ItemStack): Boolean {
        return false
    }

    fun addItem(aID: Int, aEnglish: String, aToolTip: String = Strings.E): ItemStack? {
        if (aID in 0 until mItemAmount) {
            val rStack = ItemStack(this, 1, aID)
            require(!mEnabledItems[aID]) { String.format("ID %s is already reserved for %s!", aID, rStack.getDisplayName()) }
            mEnabledItems.set(aID)
            mVisibleItems.set(aID)
            GT_LanguageManager.addStringLocalization(getUnlocalizedName(rStack) + ".name", aEnglish)
            GT_LanguageManager.addStringLocalization(getUnlocalizedName(rStack) + ".tooltip", aToolTip)
            return rStack
        }
        return null
    }
}
