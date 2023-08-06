package space.impact.impact_compat.addon.gt.features.steam_age.machines.pump

import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility.lazy
import com.gtnewhorizon.structurelib.structure.StructureUtility.transpose
import gregtech.api.enums.GT_HatchElement
import gregtech.api.enums.Materials
import gregtech.api.enums.Textures
import gregtech.api.interfaces.IHatchElement
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Fluid
import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import gregtech.api.util.GT_StructureUtility.buildHatchAdder
import gregtech.api.util.IGT_HatchAdder
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.IFluidHandler
import space.impact.impact_compat.addon.gt.base.multi.KineticMultiBlockBase
import space.impact.impact_compat.addon.gt.features.steam_age.machines.hatch.PumpUnderwaterHatch
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HatchTexture
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addOutputHatchCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addRotorHatch
import space.impact.impact_compat.addon.gt.util.world.CompatHatchElement
import space.impact.impact_compat.addon.gt.util.world.CompatStructureUtility.ofFluidPipe
import space.impact.impact_compat.addon.gt.util.world.checkCountHatches
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.translate.Translate.translate
import space.impact.impact_compat.common.util.world.Vec3
import space.impact.impact_compat.core.NBT
import kotlin.math.min

class MultiSteamKineticWaterPump : KineticMultiBlockBase<MultiSteamKineticWaterPump>, IFluidHandler {

    companion object {
        private const val UN_LOCAL = "multis.steam.kinetic.water_pump"
        private const val LOCAL = "Steam Bronze Water Pump"
        private const val PIPE_MAIN = "PIPE_MAIN"
        private const val UNDERWATER_MAIN = "UNDERWATER_MAIN"
        private const val CAPACITY = 1000
        private const val MAX_PIPE_LENGTH = 16
        private val WAILA_TT = "waila.underwater_hatch_problem".translate()
        private val OFFSET_STRUCTURE = Vec3(0, 1, 0)
        private val UnderwaterHatch = object : IHatchElement<MultiSteamKineticWaterPump> {
            override fun mteClasses(): MutableList<out Class<out IMetaTileEntity>> = mutableListOf(PumpUnderwaterHatch::class.java)
            override fun name(): String = "UnderwaterHatch"
            override fun count(t: MultiSteamKineticWaterPump): Long = if (t.underwater != null) 1 else 0
            override fun adder(): IGT_HatchAdder<in MultiSteamKineticWaterPump> {
                return IGT_HatchAdder { tile, te, index -> tile.addUnderwaterHatch(te, index) }
            }
        }
        private val STRUCTURE = StructureDefinition.builder<MultiSteamKineticWaterPump>()
            .addShape(MAIN, transpose(arrayOf(arrayOf("B", "A"), arrayOf("~", "C"))))
            .addShape(PIPE_MAIN, transpose(arrayOf(arrayOf("B"))))
            .addShape(UNDERWATER_MAIN, transpose(arrayOf(arrayOf("D"))))
            .addElement('A', lazy { _ ->
                buildHatchAdder(MultiSteamKineticWaterPump::class.java)
                    .atLeast(GT_HatchElement.OutputHatch)
                    .casingIndex(HatchTexture.MACHINE_CAGE_BRONZE.index)
                    .dot(1)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, CompatBlocks.BRONZE_MACHINE_CASING.meta)
            })
            .addElement('B', lazy { _ -> ofFluidPipe(Materials.Bronze, 4) })
            .addElement('C', lazy { _ ->
                buildHatchAdder(MultiSteamKineticWaterPump::class.java)
                    .atLeast(CompatHatchElement.RotorHatch)
                    .casingIndex(HatchTexture.MACHINE_CAGE_BRONZE.index)
                    .dot(2)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, CompatBlocks.BRONZE_MACHINE_CASING.meta)
            })
            .addElement('D', lazy { _ ->
                buildHatchAdder(MultiSteamKineticWaterPump::class.java)
                    .atLeast(UnderwaterHatch)
                    .casingIndex(HatchTexture.MACHINE_CAGE_BRONZE.index)
                    .dot(3)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, CompatBlocks.BRONZE_MACHINE_CASING.meta)
            })
            .build()
    }

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL, false)
    constructor(name: String) : super(name, false)

    private val water: FluidStack = FluidStack(FluidRegistry.WATER, CAPACITY)
    private val tankInfo: FluidTankInfo = FluidTankInfo(water, CAPACITY)
    internal var underwater: PumpUnderwaterHatch? = null

    override fun newMetaEntity(te: IGregTechTileEntity): IMetaTileEntity = MultiSteamKineticWaterPump(mName)

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder()
            .addMachineType("Pump")
            .addRotorHatch(dot = 2)
            .addOtherStructurePart("Any Bronze Fluid Pipe", "Max Length 16 blocks", 4)
            .addOtherStructurePart("Underwater Pump Part", "Under the last bronze pipe, in the water", 3)
            .addOutputHatchCount(dot = 1)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, facing: ForgeDirection,
        colorIndex: Int, active: Boolean, redstoneLevel: Boolean,
    ): Array<ITexture> {
        val base = CompatTextures.CASE_MACHINE_BRONZE.factory()
        if (side == facing || side == ForgeDirection.UP) return arrayOf(
            base,
            Textures.BlockIcons.OVERLAY_PUMP.factory(),
        )
        return if (side.ordinal >= 2 && side != facing.opposite) arrayOf(
            base,
            if (active) Textures.BlockIcons.STEAM_TURBINE_SIDE_ACTIVE.factory()
            else Textures.BlockIcons.STEAM_TURBINE_SIDE.factory()
        )
        else arrayOf(base)
    }

    fun addUnderwaterHatch(te: IGregTechTileEntity?, index: Short): Boolean {
        if (te == null) return false
        val mte = te.metaTileEntity ?: return false
        if (mte is PumpUnderwaterHatch) {
            mte.updateTexture(index.toInt())
            underwater = mte
            return true
        }
        return false
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        underwater = null
        val isMain = checkPiece(MAIN, OFFSET_STRUCTURE)
        if (!isMain) return false
        var last = 0
        for (i in 0..MAX_PIPE_LENGTH) {
            val isPipe = checkPiece(PIPE_MAIN, OFFSET_STRUCTURE.copy(z = OFFSET_STRUCTURE.z + i + 1, y = 0))
            if (!isPipe) break
            last++
        }
        val isWater = checkPiece(UNDERWATER_MAIN, OFFSET_STRUCTURE.copy(z = OFFSET_STRUCTURE.z + last, y = -1))
        if (!isWater) return false

        underwater?.also {
            connectToPipe(it.baseMetaTileEntity.getIGregTechTileEntityAtSide(ForgeDirection.UP), ForgeDirection.DOWN)
        }
        val pipeTop = te.getIGregTechTileEntityAtSide(ForgeDirection.UP)
        connectToPipe(pipeTop, ForgeDirection.DOWN)
        connectToPipe(pipeTop, te.backFacing)
        connectToPipe(te.getIGregTechTileEntityAtSide(te.frontFacing), te.backFacing)

        return checkCountHatches(outHatch = 1, rotorHatch = 1) && underwater != null
    }

    override fun getTankInfo(side: ForgeDirection): Array<FluidTankInfo> {
        return if (side == baseMetaTileEntity.frontFacing || side == ForgeDirection.UP) arrayOf(tankInfo) else super.getTankInfo(side)
    }

    override fun construct(stackSize: ItemStack, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE)
        var last = 0
        for (i in 0..min(16, stackSize.stackSize)) {
            buildPiece(PIPE_MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE.copy(z = OFFSET_STRUCTURE.z + i + 1, y = 0))
            last++
        }
        buildPiece(UNDERWATER_MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE.copy(z = OFFSET_STRUCTURE.z + last, y = -1))
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        if (underwater?.isDoneToWork == true) {
            mMaxProgresstime = 200 / speedRotor.speed
            mEfficiencyIncrease = 10000
            mEfficiency = 10000
            mOutputFluids = arrayOf(water)
            updateSlots()
            return true
        }
        return false
    }

    private fun connectToPipe(te: IGregTechTileEntity, side: ForgeDirection) {
        (te.metaTileEntity as? GT_MetaPipeEntity_Fluid)?.connect(side)
    }

    override fun getStructureDefinition(): IStructureDefinition<MultiSteamKineticWaterPump>? = STRUCTURE

    override fun getWailaBody(itemStack: ItemStack, currentTip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler) {
        super.getWailaBody(itemStack, currentTip, accessor, config)
        val tag = accessor.nbtData
        if (tag.hasKey(NBT.NBT_DONE_TO_WORK)) {
            val isDoneToWork = tag.getBoolean(NBT.NBT_DONE_TO_WORK)
            if (!isDoneToWork) currentTip.add(EnumChatFormatting.RED.toString() + WAILA_TT)
        }
    }

    override fun getWailaNBTData(player: EntityPlayerMP, tile: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
        if (underwater != null) {
            tag.setBoolean(NBT.NBT_DONE_TO_WORK, underwater!!.isDoneToWork)
        }
    }
}
