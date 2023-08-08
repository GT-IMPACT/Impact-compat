package space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.enums.ParticleFX
import gregtech.api.enums.Textures
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.util.*
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.SpecialChars
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.addon.gt.base.multi.PrimitiveMultiBlockBase
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.PRIMITIVE_CASTER_RECIPE_MAP
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HatchTexture
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.textures.factoryGlow
import space.impact.impact_compat.addon.gt.util.world.GTParticles
import space.impact.impact_compat.addon.gt.util.world.PrimitiveHatchElement
import space.impact.impact_compat.addon.gt.util.world.checkCountHatches
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.sound.SoundRes
import space.impact.impact_compat.common.util.world.Vec3

class MultiMetallurgyCaster : PrimitiveMultiBlockBase<MultiMetallurgyCaster>, ISurvivalConstructable {

    companion object {
        private const val UN_LOCAL = "multis.primitive.caster"
        private const val LOCAL = "Primitive Caster"
        private val OFFSET_STRUCTURE = Vec3(1, 0, 0)
        private val STRUCTURE = StructureDefinition.builder<MultiMetallurgyCaster>()
            .addShape(
                MAIN, StructureUtility.transpose(
                    arrayOf(
                        arrayOf("A~A", "A A", "AAA"),
                        arrayOf(" A ", "AAA", " A "),
                    )
                )
            )
            .addElement('A', StructureUtility.lazy { _ ->
                GT_StructureUtility.buildHatchAdder(MultiMetallurgyCaster::class.java)
                    .atLeast(PrimitiveHatchElement.InputHatch, PrimitiveHatchElement.InputBus, PrimitiveHatchElement.OutputBus)
                    .casingIndex(HatchTexture.MACHINE_CASE_PRIMITIVE_BRICK.index)
                    .dot(1)
                    .buildAndChain(CompatBlocks.PRIMITIVE_SMELTER_CASING.block, CompatBlocks.PRIMITIVE_SMELTER_CASING.meta)
            })
            .build()
    }

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL)
    constructor(name: String) : super(name)

    override fun newMetaEntity(te: IGregTechTileEntity): IMetaTileEntity = MultiMetallurgyCaster(mName)

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder()
            .addMachineType("Caster")
            .beginStructureBlock(3, 3, 2, false)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun useModularUI(): Boolean = false

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, facing: ForgeDirection,
        colorIndex: Int, active: Boolean, redstoneLevel: Boolean,
    ): Array<ITexture> {
        val base = CompatTextures.CASE_PRIMITIVE_SMELTER.factory()
        if (side == facing) return arrayOf(
            base,
            if (active) Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE.factory()
            else Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE.factory(),
            if (active) Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE_GLOW.factoryGlow()
            else Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_GLOW.factoryGlow(),
        )
        return arrayOf(base)
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        val tRecipe: GT_Recipe?
        if (mLockedToSingleRecipe && mSingleRecipeCheck != null) {
            if (!mSingleRecipeCheck.checkRecipeInputsSingleStack(true)) return false
            tRecipe = mSingleRecipeCheck.recipe
        } else {
            val tInputList = getStoredInputs()
            val tFluidList = getStoredFluids()
            val inputs = tInputList.toTypedArray<ItemStack>()
            val fluids = tFluidList.toTypedArray<FluidStack>()
            if (inputs.isEmpty() && fluids.isEmpty()) return false
            var tSingleRecipeCheckBuilder: GT_Single_Recipe_Check.Builder? = null
            if (mLockedToSingleRecipe) {
                tSingleRecipeCheckBuilder = GT_Single_Recipe_Check.builder(this).setBefore(inputs, fluids)
            }
            tRecipe = recipeMap.findRecipe(baseMetaTileEntity, false, false, 0, fluids, *inputs)
            if (tRecipe == null || !canOutputAll(tRecipe) || !tRecipe.isRecipeInputEqual(true, fluids, *inputs)) return false
            if (mLockedToSingleRecipe) {
                mSingleRecipeCheck = tSingleRecipeCheckBuilder?.setAfter(inputs, fluids)?.setRecipe(tRecipe)?.build()
            }
        }

        mEfficiency = 10000
        mEfficiencyIncrease = 500

        mMaxProgresstime = tRecipe!!.mDuration
        mOutputItems = tRecipe.mOutputs
        mOutputFluids = tRecipe.mFluidOutputs
        updateSlots()
        return true
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        return checkPiece(MAIN, OFFSET_STRUCTURE) && checkCountHatches(inBus = 1, outBus = 1) //TODO
    }

    override fun construct(stackSize: ItemStack, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE)
    }

    override fun survivalConstruct(stackSize: ItemStack?, elementBudget: Int, env: ISurvivalBuildEnvironment?): Int {
        return if (mMachine) -1
        else survivalBuildPiece(MAIN, stackSize, OFFSET_STRUCTURE, elementBudget, env)
    }

    override fun getRecipeMap(): GT_Recipe.GT_Recipe_Map = PRIMITIVE_CASTER_RECIPE_MAP
    override fun getStructureDefinition(): IStructureDefinition<MultiMetallurgyCaster>? = STRUCTURE

    override fun getWailaBody(itemStack: ItemStack?, currentTip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler?) {
        val tag = accessor.nbtData
        if (tag.getBoolean("incompleteStructure")) {
            currentTip.add(SpecialChars.RED + "** INCOMPLETE STRUCTURE **" + SpecialChars.RESET)
        }
        val hasProblems = tag.getBoolean("hasProblems")
        val efficiency = tag.getFloat("efficiency")
        currentTip.add("${if (hasProblems) "${SpecialChars.RED}** HAS PROBLEMS **" else "${SpecialChars.GREEN}Running Fine"}${SpecialChars.RESET} Efficiency: $efficiency%")
        val progress = tag.getInteger("progress")
        currentTip.add(GT_Waila.getMachineProgressString(progress > 0, tag.getInteger("maxProgress"), progress))
    }

    override fun getWailaNBTData(player: EntityPlayerMP?, tile: TileEntity?, tag: NBTTagCompound, world: World?, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
    }

    @SideOnly(Side.CLIENT)
    override fun onRandomDisplayTick(te: IGregTechTileEntity) = GTParticles.createSparklesMainFace(te, ParticleFX.LAVA)

    @SideOnly(Side.CLIENT)
    override fun activeSound(): ResourceLocation = SoundRes.FURNACE.resourceLocation
}
