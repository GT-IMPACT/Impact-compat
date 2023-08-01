package space.impact.impact_compat.addon.gt.features.steam_age.machines

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility.lazy
import com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock
import gregtech.api.enums.GT_HatchElement
import gregtech.api.enums.GT_Values
import gregtech.api.enums.Materials
import gregtech.api.enums.OrePrefixes
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.util.*
import gregtech.api.util.GT_StructureUtility.buildHatchAdder
import gregtech.api.util.GT_Utility.formatNumbers
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.SpecialChars
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.base.multi.CompatMultiBlockBase
import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeBlocks
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HATCH_INDEX_MACHINE_CASE_BRONZE
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.world.GTWorldUtil
import space.impact.impact_compat.common.tiles.NonTickableTileBlock
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.core.Config
import space.impact.impact_compat.core.NBT
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of
import kotlin.math.max
import kotlin.math.min

class MultiSteamBoiler : CompatMultiBlockBase<MultiSteamBoiler>, ISurvivalConstructable {

    companion object {
        private const val UN_LOCAL = "multis.steam.bronze_boiler"
        private const val LOCAL = "Bronze Steam Boiler"
        private const val EU_COEFFICIENT_STEAM = 400
        private const val EFFICIENCY_INCREASE = 16
        private val STRUCTURE = StructureDefinition.builder<MultiSteamBoiler>()
            .addShape(
                MAIN, arrayOf(
                    arrayOf("AAA", "AAA", "AAA", "AAA", "B~B"),
                    arrayOf("AAA", "A A", "A A", "AAA", "BBB"),
                    arrayOf("AAA", "AAA", "AAA", "AAA", "BBB")
                )
            )
            .addElement('B', ofBlock(CompatBlocks.BRONZE_FIREBOX_CASING.block, SteamAgeBlocks.META_BRONZE_FIREBOX_CASING))
            .addElement('A', lazy { t ->
                buildHatchAdder(MultiSteamBoiler::class.java)
                    .atLeast(GT_HatchElement.InputBus, GT_HatchElement.InputHatch, GT_HatchElement.OutputHatch)
                    .casingIndex(HATCH_INDEX_MACHINE_CASE_BRONZE)
                    .dot(1)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, SteamAgeBlocks.META_BRONZE_MACHINE_CASING)
            })
            .build()
    }

    private var fireBoxes = arrayListOf<NonTickableTileBlock>()
    private var mActive: Boolean = false
    private var oActive: Boolean = false
    private var mSuperEfficiencyIncrease = 0
    private var excessWater = 0
    private var excessFuel = 0
    private var excessProjectedEU = 0

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL, false)
    constructor(name: String) : super(name, false)

    override fun newMetaEntity(aTileEntity: IGregTechTileEntity): IMetaTileEntity {
        return MultiSteamBoiler(mName)
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        aNBT.setBoolean(NBT.NBT_ACTIVE, mActive)

        aNBT.setInteger("excessFuel", excessFuel)
        aNBT.setInteger("excessWater", excessWater)
        aNBT.setInteger("excessProjectedEU", excessProjectedEU)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        mActive = aNBT.getBoolean(NBT.NBT_ACTIVE)

        excessFuel = aNBT.getInteger("excessFuel")
        excessWater = aNBT.getInteger("excessWater")
        excessProjectedEU = aNBT.getInteger("excessProjectedEU")
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, facing: ForgeDirection,
        colorIndex: Int, active: Boolean, redstoneLevel: Boolean,
    ): Array<ITexture> {
        val base = CompatTextures.CASE_MACHINE_BRONZE.factory()
        return when {
            side == facing && active -> arrayOf(base, CompatTextures.CASE_FIREBOX_BRONZE_DOOR_OVERLAY_ACTIVE.factory())
            side == facing && !active -> arrayOf(base, CompatTextures.CASE_FIREBOX_BRONZE_DOOR_OVERLAY.factory())
            side.ordinal >= 2 -> arrayOf(CompatTextures.CASE_FIREBOX_BRONZE.factory())
            else -> arrayOf(base)
        }
    }

    override fun onRemoval() {
        if (!Config.isEnabledLowPerformance && baseMetaTileEntity.isServerSide) {
            fireBoxes.forEach { it.setActive(false) }
        }
        super.onRemoval()
    }

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(te, aTick)
        if (!Config.isEnabledLowPerformance && te.isServerSide && aTick of WorldTick.SECOND) {
            if (fireBoxes.any { it.isActive() } != te.isActive) {
                fireBoxes.forEach { it.setActive(baseMetaTileEntity.isActive) }
            }
        }
    }

    private fun runtimeBoost(mTime: Int): Int {
        return mTime * 2
    }

    @Suppress("SameParameterValue")
    private fun adjustEUtForConfig(rawEUt: Int): Int {
        val adjustedSteamOutput: Int = rawEUt
        return max(adjustedSteamOutput.toDouble(), 25.0).toInt()
    }

    private fun adjustBurnTimeForConfig(rawBurnTime: Int): Int {
        if (mEfficiency < getMaxEfficiency(mInventory[1]) - (idealStatus - repairStatus) * 1000) {
            return rawBurnTime
        }
        val adjustedEUt = max(25.0, EU_COEFFICIENT_STEAM.toDouble()).toInt()
        var adjustedBurnTime: Int = rawBurnTime * EU_COEFFICIENT_STEAM / adjustedEUt
        this.excessProjectedEU += EU_COEFFICIENT_STEAM * rawBurnTime - adjustedEUt * adjustedBurnTime
        adjustedBurnTime += this.excessProjectedEU / adjustedEUt
        this.excessProjectedEU %= adjustedEUt
        return adjustedBurnTime
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        for (tRecipe in GT_Recipe.GT_Recipe_Map.sDieselFuels.mRecipeList) {
            val tFluid = GT_Utility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true)
            if (tFluid != null && tRecipe.mSpecialValue > 1) {
                tFluid.amount = 1000
                if (depleteInput(tFluid)) {
                    mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(tRecipe.mSpecialValue / 2))
                    mEUt = adjustEUtForConfig(EU_COEFFICIENT_STEAM)
                    mEfficiencyIncrease = mMaxProgresstime * EFFICIENCY_INCREASE * 4
                    return true
                }
            }
        }
        for (tRecipe in GT_Recipe.GT_Recipe_Map.sDenseLiquidFuels.mRecipeList) {
            val tFluid = GT_Utility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true)
            if (tFluid != null) {
                tFluid.amount = 1000
                if (depleteInput(tFluid)) {
                    mMaxProgresstime = adjustBurnTimeForConfig(max(1.0, runtimeBoost(tRecipe.mSpecialValue * 2).toDouble()).toInt())
                    mEUt = adjustEUtForConfig(EU_COEFFICIENT_STEAM)
                    mEfficiencyIncrease = mMaxProgresstime * EFFICIENCY_INCREASE
                    return true
                }
            }
        }

        val tInputList = getStoredInputs()
        if (tInputList.isNotEmpty()) {
            for (tInput in tInputList) {
                if (tInput != GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Lava, 1)) {
                    if (GT_Utility.getFluidForFilledItem(tInput, true) == null &&
                        (GT_ModHandler.getFuelValue(tInput) / 80).also { mMaxProgresstime = it } > 0 &&
                        GT_ModHandler.getFuelValue(tInput) * 2 / EU_COEFFICIENT_STEAM > 1 && GT_ModHandler.getFuelValue(tInput) < 100000000
                    ) {
                        excessFuel += GT_ModHandler.getFuelValue(tInput) % 80
                        mMaxProgresstime += excessFuel / 80
                        excessFuel %= 80
                        mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(mMaxProgresstime))
                        mEUt = adjustEUtForConfig(EU_COEFFICIENT_STEAM)
                        mEfficiencyIncrease = mMaxProgresstime * EFFICIENCY_INCREASE
                        mOutputItems = arrayOf(GT_Utility.getContainerItem(tInput, true))
                        tInput.stackSize -= 1
                        updateSlots()
                        if (mEfficiencyIncrease > 5000) {
                            mEfficiencyIncrease = 0
                            mSuperEfficiencyIncrease = 20
                        }
                        mActive = true
                        return true
                    }
                }
            }
        }
        mActive = false
        mMaxProgresstime = 0
        mEUt = 0
        return false
    }

    override fun onRunningTick(aStack: ItemStack?): Boolean {
        if (mEUt > 0) {
            if (this.mSuperEfficiencyIncrease > 0)
                mEfficiency = max(0.0, min((mEfficiency + mSuperEfficiencyIncrease).toDouble(), (getMaxEfficiency(mInventory[1]) - (idealStatus - repairStatus) * 1000).toDouble())).toInt()
            val tGeneratedEU = (mEUt * 2L * mEfficiency / 10000L).toInt()
            if (tGeneratedEU > 0) {
                var amount = ((tGeneratedEU + GT_Values.STEAM_PER_WATER) / GT_Values.STEAM_PER_WATER).toLong()
                excessWater += (amount * GT_Values.STEAM_PER_WATER - tGeneratedEU).toInt()
                amount -= (excessWater / GT_Values.STEAM_PER_WATER).toLong()
                excessWater %= GT_Values.STEAM_PER_WATER
                if (depleteInput(Materials.Water.getFluid(amount)) || depleteInput(GT_ModHandler.getDistilledWater(amount))) {
                    addOutput(GT_ModHandler.getSteam(tGeneratedEU.toLong()))
                } else {
                    GT_Log.exp.println("Boiler $mName had no Water!")
                    explodeMultiblock()
                }
            }
            return true
        }
        return true
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        fireBoxes.clear()
        val isBuild = checkPiece(MAIN, 1, 4, 0)
        for (x in -1..1) {
            for (z in 0 downTo -2) {
                val iBlock = GTWorldUtil.getTileOffset<NonTickableTileBlock>(te, GTWorldUtil.vectorOffset(te, x, 0, z))
                if (iBlock != null) fireBoxes.add(iBlock)
            }
        }
        val isCompleted = isBuild && fireBoxes.size == 8 && mInputBusses.size <= 1 && mInputHatches.size <= 2 && mOutputHatches.size <= 9
        if (!isCompleted) mActive = false
        return isCompleted
    }

    override fun explodesOnComponentBreak(aStack: ItemStack?): Boolean {
        return false
    }

    override fun getMaxEfficiency(aStack: ItemStack?): Int {
        return 10000
    }

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder()
            .addMachineType("Boiler")
            .addInfo("Produces ${formatNumbers(((EU_COEFFICIENT_STEAM * 40) * (runtimeBoost(20) / 20f)).toDouble())}L of Steam with 1 Coal at ${formatNumbers(EU_COEFFICIENT_STEAM * 40L)}L/s") // ?
            .addInfo("Solid Fuels with a burn value that is too high or too low will not work")
            .addInfo(String.format("Diesel fuels have 1/4 efficiency - Takes %s seconds to heat up", formatNumbers(500.0 / EFFICIENCY_INCREASE)))
            .addSeparator()
            .beginStructureBlock(3, 5, 3, false)
            .addCasingInfoRange("Bronze Machine Casing", 18, 31, false)
            .addOtherStructurePart("Bronze Fire Boxes", "Bottom layer, 3 minimum")
            .addInputBus("Solid fuel / Water, Any casing", 1)
            .addInputHatch("Liquid fuel, Any casing", 1)
            .addStructureInfo("You can use either, or both")
            .addOutputHatch("Steam, Any casing", 1)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun construct(stackSize: ItemStack?, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, 1, 4, 0)
    }

    override fun useModularUI(): Boolean {
        return false
    }

    override fun getStructureDefinition(): IStructureDefinition<MultiSteamBoiler>? = STRUCTURE

    override fun getWailaBody(itemStack: ItemStack?, currentTip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler?) {
        val tag = accessor.nbtData
        if (tag.getBoolean("incompleteStructure")) {
            currentTip.add(SpecialChars.RED + "** INCOMPLETE STRUCTURE **" + SpecialChars.RESET)
        }
        if (!tag.getBoolean("hasProblems")) {
            currentTip.add( "${SpecialChars.GREEN}Running Fine ${SpecialChars.RESET}Efficiency: ${tag.getFloat("efficiency")}%")
        }
        currentTip.add(String.format("Facing: %s", baseMetaTileEntity.frontFacing.name))
        val isActive = tag.getBoolean("isActive")
        currentTip.add(GT_Waila.getMachineProgressString(isActive, tag.getInteger("maxProgress"), tag.getInteger("progress")))
    }

    override fun getWailaNBTData(player: EntityPlayerMP?, tile: TileEntity?, tag: NBTTagCompound, world: World?, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
    }
}
