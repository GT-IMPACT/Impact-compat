package space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch

import com.gtnewhorizons.modularui.api.drawable.IDrawable
import com.gtnewhorizons.modularui.api.screen.ModularWindow
import com.gtnewhorizons.modularui.api.screen.UIBuildContext
import com.gtnewhorizons.modularui.common.widget.SlotGroup
import gregtech.GT_Mod
import gregtech.api.enums.Textures.BlockIcons
import gregtech.api.gui.modularui.GT_UIInfos
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.modularui.IAddUIWidgets
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch
import gregtech.api.render.TextureFactory
import gregtech.api.util.GT_Utility
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.common.util.kotlin.Array3
import space.impact.impact_compat.common.util.merch.Tags
import kotlin.math.min

class PrimitiveOutputBus : GT_MetaTileEntity_Hatch, IAddUIWidgets {

    companion object {
        private const val NAME = "compat.hatch.primitive.output_bus"
        private val DESCRIPTION = arrayOf("Item Output for Multiblocks", Tags.IMPACT_GREGTECH)
    }

    constructor(id: Int, localName: String) : super(id, NAME, localName, 0, 4, DESCRIPTION)
    constructor(aName: String, aDescription: Array<String>, aTextures: Array3<ITexture>) : super(aName, 0, 4, aDescription, aTextures)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): MetaTileEntity {
        return PrimitiveOutputBus(mName, mDescriptionArray, mTextures)
    }

    override fun getTexturesActive(aBaseTexture: ITexture): Array<ITexture> {
        return if (GT_Mod.gregtechproxy.mRenderIndicatorsOnHatch)
            arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_OUT), TextureFactory.of(BlockIcons.ITEM_OUT_SIGN))
        else arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_OUT))
    }

    override fun getTexturesInactive(aBaseTexture: ITexture): Array<ITexture> {
        return if (GT_Mod.gregtechproxy.mRenderIndicatorsOnHatch)
            arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_OUT), TextureFactory.of(BlockIcons.ITEM_OUT_SIGN))
        else arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_OUT))
    }

    override fun isSimpleMachine(): Boolean {
        return true
    }

    override fun isFacingValid(facing: ForgeDirection?): Boolean {
        return true
    }

    override fun isAccessAllowed(aPlayer: EntityPlayer?): Boolean {
        return true
    }

    override fun isValidSlot(aIndex: Int): Boolean {
        return true
    }

    override fun onRightclick(aBaseMetaTileEntity: IGregTechTileEntity?, aPlayer: EntityPlayer?): Boolean {
        GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer)
        return true
    }

    override fun getBaseTexture(aTier: Int, colorIndex: Int): ITexture {
        return CompatTextures.CASE_PRIMITIVE_HATCH.factory()
    }

    fun storeAll(aStack: ItemStack?): Boolean {
        if (aStack == null) return false
        markDirty()
        var i = 0
        val mInventoryLength = mInventory.size
        while (i < mInventoryLength && aStack.stackSize > 0) {
            val tSlot = mInventory[i]
            if (GT_Utility.isStackInvalid(tSlot)) {
                if (aStack.stackSize <= inventoryStackLimit) {
                    mInventory[i] = aStack
                    return true
                }
                mInventory[i] = aStack.splitStack(inventoryStackLimit)
            } else {
                val tRealStackLimit = min(inventoryStackLimit.toDouble(), tSlot.maxStackSize.toDouble()).toInt()
                if (tSlot.stackSize < tRealStackLimit && tSlot.isItemEqual(aStack)
                    && ItemStack.areItemStackTagsEqual(tSlot, aStack)
                ) {
                    if (aStack.stackSize + tSlot.stackSize <= tRealStackLimit) {
                        mInventory[i].stackSize += aStack.stackSize
                        return true
                    } else {
                        // more to serve
                        aStack.stackSize -= tRealStackLimit - tSlot.stackSize
                        mInventory[i].stackSize = tRealStackLimit
                    }
                }
            }
            i++
        }
        return false
    }

    override fun allowPullStack(te: IGregTechTileEntity, aIndex: Int, side: ForgeDirection, aStack: ItemStack?): Boolean {
        return side == te.frontFacing
    }

    override fun allowPutStack(te: IGregTechTileEntity?, aIndex: Int, side: ForgeDirection?, aStack: ItemStack?): Boolean {
        return false
    }

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(te, aTick)
        if (te.isServerSide && te.isAllowedToWork && aTick and 0x7L == 0L) {
            val tTileEntity = te.getIInventoryAtSide(te.frontFacing)
            if (tTileEntity != null) {
                GT_Utility.moveMultipleItemStacks(te, tTileEntity, te.frontFacing, te.backFacing,
                    null, false, 64.toByte(), 1.toByte(), 64.toByte(), 1.toByte(), mInventory.size)
                for (i in mInventory.indices) if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null
            }
        }
    }

    override fun useModularUI(): Boolean {
        return true
    }

    override fun addUIWidgets(builder: ModularWindow.Builder, ctx: UIBuildContext) {
        val inventoryHandler = getInventoryHandler() ?: return
        val background = arrayOf<IDrawable>(guiTextureSet.itemSlot)
        builder.widget(
            SlotGroup.ofItemHandler(inventoryHandler, 2)
                .startFromSlot(0)
                .endAtSlot(3)
                .background(*background)
                .build()
                .setPos(79 - 9, 34 - 9)
        )
    }
}
