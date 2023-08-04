package space.impact.impact_compat.addon.gt.features.steam_age.machines.processing

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility.lazy
import com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.enums.GT_HatchElement
import gregtech.api.enums.ParticleFX
import gregtech.api.enums.Textures.BlockIcons
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.util.GT_ModHandler
import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import gregtech.api.util.GT_Recipe
import gregtech.api.util.GT_StructureUtility.buildHatchAdder
import gregtech.api.util.GT_Waila
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.SpecialChars
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.base.multi.CompatMultiBlockBase
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HatchTexture
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.textures.factoryGlow
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addInputBusCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addOutputBusCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addSteamMachineStructure
import space.impact.impact_compat.addon.gt.util.world.GTParticles
import space.impact.impact_compat.addon.gt.util.world.checkCountHatches
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.sound.SoundRes
import space.impact.impact_compat.common.util.world.Vec3
import space.impact.impact_compat.core.NBT
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of

class MultiSteamFurnace : CompatMultiBlockBase<MultiSteamFurnace>, ISurvivalConstructable {

    companion object {
        private const val UN_LOCAL = "multis.steam.furnace"
        private const val LOCAL = "Bronze Furnace"
        private val STRUCTURE = StructureDefinition.builder<MultiSteamFurnace>()
            .addShape(
                MAIN, arrayOf(
                    arrayOf("BBB", "BBB", "A~A"),
                    arrayOf("BBB", "B B", "AAA"),
                    arrayOf("BBB", "BBB", "AAA")
                )
            )
            .addElement('A', ofBlock(CompatBlocks.BRONZE_BRICK_CASING.block, CompatBlocks.BRONZE_BRICK_CASING.meta))
            .addElement('B', lazy { _ ->
                buildHatchAdder(MultiSteamFurnace::class.java)
                    .atLeast(GT_HatchElement.InputBus, GT_HatchElement.OutputBus)
                    .casingIndex(HatchTexture.MACHINE_CAGE_BRONZE.index)
                    .dot(1)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, CompatBlocks.BRONZE_MACHINE_CASING.meta)
            })
            .build()
        private val OFFSET_STRUCTURE = Vec3(1, 2, 0)
        private const val MAX_PARALLEL = 8
    }

    private var burnEnergy: Int = 0

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL, false)
    constructor(name: String) : super(name, false)
    override fun newMetaEntity(te: IGregTechTileEntity): IMetaTileEntity = MultiSteamFurnace(mName)

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder()
            .addMachineType("Furnace")
            .addInfo("Smelts up to 8 items at once")
            .addInfo("Burn time logic: when the fuel remains at 5s, will try to burn items in the hatches")
            .beginStructureBlock(3, 3, 3, true)
            .addSteamMachineStructure(13..16, bronzeBricks = 8)
            .addInputBusCount(2, dot = 1)
            .addOutputBusCount(dot = 1)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, facing: ForgeDirection,
        colorIndex: Int, active: Boolean, redstoneLevel: Boolean,
    ): Array<ITexture> {
        val base = CompatTextures.CASE_BRONZE_BRICK.factory()
        if (side == facing) return arrayOf(
            base,
            if (active) BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE.factory()
            else BlockIcons.OVERLAY_FRONT_STEAM_FURNACE.factory(),
            if (active) BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE_GLOW.factoryGlow()
            else BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_GLOW.factoryGlow(),
        )
        return arrayOf(
            when(side) {
                ForgeDirection.UP -> CompatTextures.CASE_MACHINE_BRONZE.factory()
                ForgeDirection.DOWN -> CompatTextures.CASE_VANILA_BRICK.factory()
                else -> base
            }
        )
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        aNBT.setInteger(NBT.NBT_BURN, burnEnergy)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        burnEnergy = aNBT.getInteger(NBT.NBT_BURN)
    }

    override fun onRunningTick(aStack: ItemStack?): Boolean {
        if (baseMetaTileEntity.timer of WorldTick.SECOND) {
            burnEnergy -= 20
            if (burnEnergy <= 0) {
                burnEnergy = 0
                stopMachine()
            }
        }
        return super.onRunningTick(aStack)
    }

    override fun onPostTick(te: IGregTechTileEntity, aTick: Long) {
        running(te, aTick)
        if (te.isServerSide && aTick of WorldTick.SECOND) {
            burning()
            checkFuel()
        }
    }

    private fun running(te: IGregTechTileEntity, aTick: Long) {
        if (te.isServerSide) {
            if (mEfficiency < 0) mEfficiency = 0
            if (mUpdated) {
                if (mUpdate <= 0) mUpdate = 50
                mUpdated = false
            }
            if (--mUpdate == 0 || --mStartUpCheck == 0) {
                checkStructure(true, te)
            }
            if (mStartUpCheck < 0) {
                if (mMachine && burnEnergy > 0) runMachine(te, aTick)
                else stopMachine()
            }
            te.isActive = burnEnergy > 0
        } else {
            if (!te.hasMufflerUpgrade()) {
                doActivitySound(getActivitySoundLoop())
            }
        }
    }

    private fun burning() {
        if (burnEnergy > 0) {
            burnEnergy -= 20
            if (burnEnergy <= 0) {
                burnEnergy = 0
                stopMachine()
            }
        }
    }

    override fun stopMachine() {
        mEUt = 0
        mEfficiency = 0
        mEfficiencyIncrease = 0
    }

    private fun checkFuel() {
        if (burnEnergy <= 100) {
            val tInputList = getStoredInputs()
            for (burnStack in tInputList) {
                val burnTime = TileEntityFurnace.getItemBurnTime(burnStack)
                if (burnTime > 0) {
                    burnEnergy += burnTime
                    burnStack.stackSize -= 1
                    break
                }
            }
        }
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        val tInputList = getStoredInputs()
        if (tInputList.isEmpty()) return false
        var tCurrentParallel = 0

        val smeltedOutputs = ArrayList<ItemStack>()
        val outputStackSizes = ArrayList<Int>()

        for (item in tInputList) {
            val smeltedOutput = GT_ModHandler.getSmeltingOutput(item, false, null)
            if (smeltedOutput != null) {
                smeltedOutputs.add(smeltedOutput)
                if (item.stackSize <= MAX_PARALLEL - tCurrentParallel) {
                    tCurrentParallel += item.stackSize
                    outputStackSizes.add(smeltedOutput.stackSize * item.stackSize)
                    item.stackSize = 0
                } else {
                    val remainingStackSize = tCurrentParallel + item.stackSize - MAX_PARALLEL
                    outputStackSizes.add(smeltedOutput.stackSize * (item.stackSize - remainingStackSize))
                    item.stackSize = remainingStackSize
                    break
                }
            }
            if (tCurrentParallel == MAX_PARALLEL) break
        }
        mOutputItems = arrayOfNulls(smeltedOutputs.size)
        for (i in mOutputItems.indices) {
            val tNewStack = smeltedOutputs[i]
            tNewStack.stackSize = outputStackSizes[i]
            mOutputItems[i] = tNewStack
        }
        if (mOutputItems.isNotEmpty()) {
            mEfficiency = 10000
            mEfficiencyIncrease = 500
            mMaxProgresstime = 200
        }
        updateSlots()
        return true
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        return checkPiece(MAIN, OFFSET_STRUCTURE) && checkCountHatches(inBus = 2, outBus = 1)
    }

    override fun construct(stackSize: ItemStack, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE)
    }

    override fun survivalConstruct(stackSize: ItemStack?, elementBudget: Int, env: ISurvivalBuildEnvironment?): Int {
        return if (mMachine) -1
        else survivalBuildPiece(MAIN, stackSize, OFFSET_STRUCTURE, elementBudget, env)
    }

    override fun getRecipeMap(): GT_Recipe.GT_Recipe_Map = GT_Recipe.GT_Recipe_Map.sFurnaceRecipes //TODO
    override fun getStructureDefinition(): IStructureDefinition<MultiSteamFurnace>? = STRUCTURE

    override fun getWailaBody(itemStack: ItemStack?, currentTip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler?) {
        val tag = accessor.nbtData
        if (tag.getBoolean("incompleteStructure")) {
            currentTip.add(SpecialChars.RED + "** INCOMPLETE STRUCTURE **" + SpecialChars.RESET)
        }

        val hasProblems = tag.getBoolean("hasProblems")
        val efficiency = tag.getFloat("efficiency")
        currentTip.add("${ if (hasProblems) "${SpecialChars.RED}** HAS PROBLEMS **" else "${SpecialChars.GREEN}Running Fine"}${SpecialChars.RESET} Efficiency: $efficiency%")
        val progress = tag.getInteger("progress")
        currentTip.add(GT_Waila.getMachineProgressString(progress > 0, tag.getInteger("maxProgress"), progress))

        val burnEnergy = tag.getInteger(NBT.NBT_BURN)
        currentTip.add("Burn Time: ${burnEnergy / 20}s")
    }

    override fun getWailaNBTData(player: EntityPlayerMP?, tile: TileEntity?, tag: NBTTagCompound, world: World?, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
        tag.setInteger(NBT.NBT_BURN, burnEnergy)
    }

    @SideOnly(Side.CLIENT)
    override fun onRandomDisplayTick(te: IGregTechTileEntity) = GTParticles.createSparklesMainFace(te, ParticleFX.SMOKE)

    @SideOnly(Side.CLIENT)
    override fun activeSound(): ResourceLocation = SoundRes.FURNACE.resourceLocation
}
