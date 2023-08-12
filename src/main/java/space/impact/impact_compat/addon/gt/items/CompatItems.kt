package space.impact.impact_compat.addon.gt.items

import net.minecraft.item.ItemStack
import space.impact.impact_compat.common.item.CompatItemsPagination
import space.impact.impact_compat.core.Strings

private const val PAGE_1 = 1

enum class CompatItems(private val itemPage: Int = -1) : CompatBaseItemContainer {

    CRUCIBLE_SINGLE(PAGE_1),
    CRUCIBLE_MULTI(PAGE_1),

    TOOL_UNIVERSAL_WRENCH,
    TOOL_FORGE_HAMMER,
    ;

    override var mHasNotBeenSet: Boolean = false
    override lateinit var mStack: ItemStack
    override val nameItem: String
        get() = this.name

    fun set(aID: Int, aEnglish: String, aToolTip: String = Strings.E) {
        this set when(itemPage) {
            PAGE_1 -> INSTANCE_PAGE1.addItem(aID, aEnglish, aToolTip)
            else -> throw IndexOutOfBoundsException("Not Found Page $itemPage")
        }
    }
    companion object {
        val INSTANCE_PAGE1 = CompatItemsPagination("custom.items.01")

    }
}
