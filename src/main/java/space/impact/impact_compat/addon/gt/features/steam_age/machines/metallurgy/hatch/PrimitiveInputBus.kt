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
import gregtech.api.util.GT_Recipe.GT_Recipe_Map
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.common.util.kotlin.Array3
import space.impact.impact_compat.common.util.merch.Tags

class PrimitiveInputBus : GT_MetaTileEntity_Hatch, IAddUIWidgets {

    companion object {
        private const val NAME = "compat.hatch.primitive.input_bus"
        private val DESCRIPTION = arrayOf("Item Input for Multiblocks", Tags.IMPACT_GREGTECH)
    }

    var mRecipeMap: GT_Recipe_Map? = null

    constructor(id: Int, localName: String) : super(id, NAME, localName, 0, 4, DESCRIPTION)
    constructor(aName: String, aDescription: Array<String>, aTextures: Array3<ITexture>) : super(aName, 0, 4, aDescription, aTextures)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): MetaTileEntity {
        return PrimitiveInputBus(mName, mDescriptionArray, mTextures)
    }

    override fun getTexturesActive(aBaseTexture: ITexture): Array<ITexture> {
        return if (GT_Mod.gregtechproxy.mRenderIndicatorsOnHatch)
            arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_IN), TextureFactory.of(BlockIcons.ITEM_IN_SIGN))
        else arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_IN))
    }

    override fun getTexturesInactive(aBaseTexture: ITexture): Array<ITexture> {
        return if (GT_Mod.gregtechproxy.mRenderIndicatorsOnHatch)
            arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_IN), TextureFactory.of(BlockIcons.ITEM_IN_SIGN))
        else arrayOf(aBaseTexture, TextureFactory.of(BlockIcons.OVERLAY_PIPE_IN))
    }

    override fun getBaseTexture(aTier: Int, colorIndex: Int): ITexture {
        return CompatTextures.CASE_PRIMITIVE_HATCH.factory()
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

    override fun onRightclick(aBaseMetaTileEntity: IGregTechTileEntity?, aPlayer: EntityPlayer?): Boolean {
        GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer)
        return true
    }

    override fun onPostTick(aBaseMetaTileEntity: IGregTechTileEntity, aTimer: Long) {
        if (aBaseMetaTileEntity.isServerSide && aBaseMetaTileEntity.hasInventoryBeenModified()) {
            updateSlots()
        }
    }

    fun updateSlots() {
        for (i in mInventory.indices)
            if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        if (mRecipeMap != null) aNBT.setString("recipeMap", mRecipeMap!!.mUniqueIdentifier)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        mRecipeMap = GT_Recipe_Map.sIndexedMappings.getOrDefault(aNBT.getString("recipeMap"), null)
    }

    override fun allowPullStack(te: IGregTechTileEntity?, aIndex: Int, side: ForgeDirection, aStack: ItemStack?): Boolean {
        return false
    }

    override fun allowPutStack(te: IGregTechTileEntity?, aIndex: Int, side: ForgeDirection, aStack: ItemStack?): Boolean {
        return false
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
