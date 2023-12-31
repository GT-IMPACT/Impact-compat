@file:Suppress("LeakingThis")

package space.impact.impact_compat.common.item.meta

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.interfaces.IIconContainer
import gregtech.api.objects.ItemData
import gregtech.api.util.GT_LanguageManager
import gregtech.api.util.GT_OreDictUnificator
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import space.impact.impact_compat.MODID
import space.impact.impact_compat.common.item.materials.GeneratedMaterials
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

abstract class MetaGeneratedItem(unlocalName: String, offset: Short, itemCount: Short) : MetaBaseItem(unlocalName) {
    val mBurnValues = ConcurrentHashMap<Short, Short>()
    val mOffset: Short
    val mItemAmount: Short
    val mEnabledItems: BitSet
    val mVisibleItems: BitSet
    val mIconList: Array<Array<IIcon?>>

    init {
        setHasSubtypes(true)
        setMaxDamage(0)
        mEnabledItems = BitSet(itemCount.toInt())
        mVisibleItems = BitSet(itemCount.toInt())
        mOffset = min(32766.0, offset.toDouble()).toInt().toShort()
        mItemAmount = min(itemCount.toDouble(), (32766 - mOffset).toDouble()).toInt().toShort()
        mIconList = Array(itemCount.toInt()) { arrayOfNulls(1) }
        sInstances[unlocalizedName] = this
    }

    /**
     * Sets the Furnace Burn Value for the Item.
     *
     * @param aMetaValue the Meta Value of the Item you want to set it to. [0 - 32765]
     * @param value      200 = 1 Burn Process = 500 EU, max = 32767 (that is 81917.5 EU)
     * @return the Item itself for convenience in constructing.
     */
    fun setBurnValue(aMetaValue: Int, value: Int): MetaGeneratedItem {
        if (aMetaValue < 0 || aMetaValue >= mOffset + mEnabledItems.length() || value < 0) return this
        if (value == 0) mBurnValues.remove(aMetaValue.toShort()) else mBurnValues[aMetaValue.toShort()] = if (value > Short.MAX_VALUE) Short.MAX_VALUE else value.toShort()
        return this
    }

    /**
     * @return the Color Modulation the Material is going to be rendered with.
     */
    open fun getRGBa(stack: ItemStack?): ShortArray {
        return GeneratedMaterials.NULL.rgba
    }

    open fun getIconContainer(meta: Int): IIconContainer? {
        return null
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(aItem: Item, aCreativeTab: CreativeTabs?, aList: MutableList<Any?>) {
        val j = mEnabledItems.length()
        for (i in 0 until j) {
            if (mVisibleItems[i] || mEnabledItems[i]) {
                val tStack = ItemStack(this, 1, mOffset + i)
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

    companion object {
        val sInstances = ConcurrentHashMap<String, MetaGeneratedItem>()
        var MAX_COUNT_AUTOGENERATED_ITEMS = 32000
    }
}
