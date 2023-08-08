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
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import space.impact.impact_compat.addon.gt.base.multi.PrimitiveMultiBlockBase
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.PRIMITIVE_SMELTER_RECIPE_MAP
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HatchTexture
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.textures.factoryGlow
import space.impact.impact_compat.addon.gt.util.world.GTParticles
import space.impact.impact_compat.addon.gt.util.world.GTWorldUtil
import space.impact.impact_compat.addon.gt.util.world.PrimitiveHatchElement
import space.impact.impact_compat.addon.gt.util.world.checkCountHatches
import space.impact.impact_compat.common.blocks.SmelterContentBlock
import space.impact.impact_compat.common.tiles.models.SmelterContentModelTile
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.sound.SoundRes
import space.impact.impact_compat.common.util.world.Vec3
import space.impact.impact_compat.core.NBT
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of

class MultiMetallurgySmelter : PrimitiveMultiBlockBase<MultiMetallurgySmelter>, ISurvivalConstructable {

    companion object {
        private const val UN_LOCAL = "multis.primitive.smelter"
        private const val LOCAL = "Primitive Smelter"
        private val OFFSET_STRUCTURE = Vec3(2, 0, 0)
        private val OFFSET_CONTENT = Vec3(0, 0, -2)
        private val STRUCTURE = StructureDefinition.builder<MultiMetallurgySmelter>()
            .addShape(
                MAIN, StructureUtility.transpose(
                    arrayOf(
                        arrayOf("AA~AA", "A   A", "A   A", "A   A", "AAAAA"),
                        arrayOf(" AAA ", "AAAAA", "AAAAA", "AAAAA", " AAA "),
                    )
                )
            )
            .addElement('A', StructureUtility.lazy { _ ->
                GT_StructureUtility.buildHatchAdder(MultiMetallurgySmelter::class.java)
                    .atLeast(PrimitiveHatchElement.InputBus, PrimitiveHatchElement.OutputHatch)
                    .casingIndex(HatchTexture.MACHINE_CASE_PRIMITIVE_BRICK.index)
                    .dot(1)
                    .buildAndChain(CompatBlocks.PRIMITIVE_SMELTER_CASING.block, CompatBlocks.PRIMITIVE_SMELTER_CASING.meta)
            })
            .build()
    }

    private var burnEnergy: Int = 0

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL)
    constructor(name: String) : super(name)

    override fun newMetaEntity(te: IGregTechTileEntity): IMetaTileEntity = MultiMetallurgySmelter(mName)

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder()
            .addMachineType("Smelter")
            .beginStructureBlock(5, 5, 2, true)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        aNBT.setInteger(NBT.NBT_BURN, burnEnergy)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        burnEnergy = aNBT.getInteger(NBT.NBT_BURN)
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
            handleEnvironment(te)
        }
        if (te.isServerSide && aTick of WorldTick.SECOND && mMachine) {
            val offsetBlock = GTWorldUtil.vectorOffset(te, OFFSET_CONTENT)
            when {
                mProgresstime > 0 && burnEnergy > 0 -> setLavaContent(te, offsetBlock)
                mProgresstime <= 0 && burnEnergy > 0 -> setCoalContent(te, offsetBlock)
                else -> removeAllContent(te)
            }
        }
    }

    private fun setBlockSmelterContent(te: IGregTechTileEntity, offset: Vec3, isRemove: Boolean = false) {
        GTWorldUtil.setBlockOffset(te, offset, if (isRemove) Blocks.air else SmelterContentBlock.INSTANCE, 0)
    }

    private fun setLavaContent(te: IGregTechTileEntity, offsetBlock: Vec3) {
        setBlockSmelterContent(te, offsetBlock)
        GTWorldUtil.getTileOffset<SmelterContentModelTile>(te, offsetBlock)?.setActive(true)
    }

    private fun setCoalContent(te: IGregTechTileEntity, offsetBlock: Vec3) {
        setBlockSmelterContent(te, offsetBlock)
        GTWorldUtil.getTileOffset<SmelterContentModelTile>(te, offsetBlock)?.setActive(false)
    }

    private fun removeAllContent(te: IGregTechTileEntity) {
        val offsetBlock = GTWorldUtil.vectorOffset(te, OFFSET_CONTENT)
        setBlockSmelterContent(te, offsetBlock, true)
    }

    override fun onRemoval() {
        baseMetaTileEntity?.also(::removeAllContent)
        super.onRemoval()
    }

    private fun handleEnvironment(te: IGregTechTileEntity) {
        if (burnEnergy > 0) {
            val aabb = GTWorldUtil.createAABBOffset(te, OFFSET_CONTENT, 1, 1)
            for (tLiving in te.world.getEntitiesWithinAABB(EntityLivingBase::class.java, aabb)) {
                GT_Utility.applyHeatDamage(tLiving as EntityLivingBase, 1f)
            }
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
                if (GT_Utility.isStackValid(burnStack)) {
                    val burnTime = TileEntityFurnace.getItemBurnTime(burnStack)
                    if (burnTime > 0) {
                        burnEnergy += burnTime
                        burnStack.stackSize -= 1
                        updateSlots()
                        break
                    }
                }
            }
        }
    }

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        if (burnEnergy > 0) {
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
        return false
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        val isConfirm = checkPiece(MAIN, OFFSET_STRUCTURE) && checkCountHatches(inBus = 1, outBus = 1) //TODO
        if (!isConfirm) removeAllContent(te)
        return isConfirm
    }

    override fun construct(stackSize: ItemStack, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE)
    }

    override fun survivalConstruct(stackSize: ItemStack?, elementBudget: Int, env: ISurvivalBuildEnvironment?): Int {
        return if (mMachine) -1
        else survivalBuildPiece(MAIN, stackSize, OFFSET_STRUCTURE, elementBudget, env)
    }

    override fun getRecipeMap(): GT_Recipe.GT_Recipe_Map = PRIMITIVE_SMELTER_RECIPE_MAP
    override fun getStructureDefinition(): IStructureDefinition<MultiMetallurgySmelter>? = STRUCTURE

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

        val burnEnergy = tag.getInteger(NBT.NBT_BURN)
        currentTip.add("Burn Time: ${burnEnergy / 20}s")
    }

    override fun getWailaNBTData(player: EntityPlayerMP?, tile: TileEntity?, tag: NBTTagCompound, world: World?, x: Int, y: Int, z: Int) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z)
        tag.setInteger(NBT.NBT_BURN, burnEnergy)
    }

    @SideOnly(Side.CLIENT)
    override fun onRandomDisplayTick(te: IGregTechTileEntity) = GTParticles.createSparklesMainFace(te, ParticleFX.LAVA)

    @SideOnly(Side.CLIENT)
    override fun activeSound(): ResourceLocation = SoundRes.FURNACE.resourceLocation
}
