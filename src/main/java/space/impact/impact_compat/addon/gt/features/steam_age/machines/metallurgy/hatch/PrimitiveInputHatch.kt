package space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch

import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.fluid.IFluidStore
import gregtech.api.interfaces.metatileentity.IFluidLockable
import gregtech.api.interfaces.modularui.IAddUIWidgets
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Input
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.pipe.IPrimitiveConnectPipe
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.common.util.kotlin.Array3
import space.impact.impact_compat.common.util.merch.Tags

class PrimitiveInputHatch : GT_MetaTileEntity_Hatch_Input, IPrimitiveConnectPipe {
    companion object {
        private const val NAME = "compat.hatch.primitive.input_hatch"
        private val DESCRIPTION = arrayOf("Fluid Input for Multiblocks", "Capacity: 4,000L", Tags.IMPACT_GREGTECH)
    }
    constructor(id: Int, localName: String) : super(id, NAME, localName, 0, DESCRIPTION, 3)
    constructor(aName: String, aDescription: Array<String>, aTextures: Array3<ITexture>) : super(aName, 0, aDescription, aTextures)
    override fun newMetaEntity(aTileEntity: IGregTechTileEntity?): MetaTileEntity = PrimitiveInputHatch(mName, mDescriptionArray, mTextures)
    override fun getBaseTexture(aTier: Int, colorIndex: Int): ITexture = CompatTextures.CASE_PRIMITIVE_HATCH.factory()
    override fun getCapacity(): Int = 4000
    override fun useModularUI(): Boolean = false

    override fun getWailaBody(
        itemStack: ItemStack?, currenttip: MutableList<String>, accessor: IWailaDataAccessor,
        config: IWailaConfigHandler?
    ) {
        super.getWailaBody(itemStack, currenttip, accessor, config)
        val tag = accessor.nbtData
        val fluid = if (tag.hasKey("mFluid")) FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("mFluid")) else null
        if (fluid != null && fluid.amount > 0) {
            currenttip.removeAt(0)
            currenttip
                .add(0, String.format("%d / %d mB %s", fluid.amount, realCapacity, fluid.localizedName))
        } else {
            currenttip.add(0, "Tank Empty")
        }
    }

    override fun getWailaNBTData(
        player: EntityPlayerMP?, tile: TileEntity?, tag: NBTTagCompound, world: World?, x: Int, y: Int,
        z: Int
    ) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
        val fluid = fluid
        if (fluid != null) tag.setTag("mFluid", fluid.writeToNBT(NBTTagCompound()))
        else if (tag.hasKey("mFluid")) tag.removeTag("mFluid")
    }
}
