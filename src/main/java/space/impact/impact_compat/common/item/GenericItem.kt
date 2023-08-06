package space.impact.impact_compat.common.item

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.util.GT_LanguageManager
import ic2.api.item.IElectricItem
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import space.impact.impact_compat.MODID
import java.lang.String.format

open class GenericItem @JvmOverloads constructor(unLocalName: String, englishName: String?, englishTooltip: String?, needTooltipToLang: Boolean = true) : Item() {

    protected val name: String
    protected var tooltip: String? = null

    @SideOnly(Side.CLIENT)
    protected var icon: IIcon? = null

    init {
        name = "compat.$unLocalName"
        GT_LanguageManager.addStringLocalization("$name.name", englishName)
        if (!englishTooltip.isNullOrEmpty()) {
            GT_LanguageManager.addStringLocalization("$name.tooltip_main".also { tooltip = it }, englishTooltip, needTooltipToLang)
        } else {
            tooltip = null
        }
        GameRegistry.registerItem(this, name, MODID)
    }

    override fun setUnlocalizedName(name: String): Item {
        return this
    }

    override fun getUnlocalizedName(): String {
        return name
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return if (getHasSubtypes()) name + "." + getDamage(stack) else name
    }

    @SideOnly(Side.CLIENT)
    override fun registerIcons(aIconRegister: IIconRegister) {
        icon = aIconRegister.registerIcon("$MODID:$name")
    }

    override fun doesSneakBypassUse(world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
        return true
    }

    override fun getIconFromDamage(meta: Int): IIcon? {
        return icon
    }

    fun getTier(stack: ItemStack?): Int {
        return 0
    }

    override fun addInformation(stack: ItemStack, player: EntityPlayer, tooltips: MutableList<Any?>, f3: Boolean) {
        if (maxDamage > 0 && !getHasSubtypes()) tooltips.add((stack.maxDamage - getDamage(stack)).toString() + " / " + stack.maxDamage)
        if (tooltip != null) {
            tooltips.add(GT_LanguageManager.getTranslation(tooltip))
        }
        if (isElectricItem(stack, 10000)) {
            tooltips.add(format(GT_LanguageManager.getTranslation("Item_DESCRIPTION_Index_019"), getTier(stack).toString() + ""))
        }
        addToolTip(tooltips, stack, player)
    }

    protected open fun addToolTip(tooltips: MutableList<Any?>, stack: ItemStack, player: EntityPlayer) {}

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        isItemStackUsable(stack)
    }

    fun isItemStackUsable(stack: ItemStack?): Boolean {
        return true
    }

    override fun getContainerItem(stack: ItemStack): ItemStack? {
        return null
    }

    override fun hasContainerItem(stack: ItemStack): Boolean {
        return getContainerItem(stack) != null
    }

    companion object {
        fun isElectricItem(stack: ItemStack?, tier: Int = 10000): Boolean {
            return stack != null && stack.item is IElectricItem && (stack.item as IElectricItem).getTier(stack) <= tier
        }

        fun transItem(key: String, english: String?): String {
            return GT_LanguageManager.addStringLocalization("Item_DESCRIPTION_Index_$key", english, false)
        }
    }
}
