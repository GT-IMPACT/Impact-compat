package space.impact.impact_compat.addon.gt.features.steam_age.machines

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
import gregtech.api.render.TextureFactory
import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import gregtech.api.util.GT_Recipe
import gregtech.api.util.GT_StructureUtility.buildHatchAdder
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.base.multi.KineticMultiBlockBase
import space.impact.impact_compat.addon.gt.features.steam_age.blocks.SteamAgeBlocks
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.SteamForgeHammerRecipeMap
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HATCH_INDEX_MACHINE_CASE_BRONZE
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addInputBusCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addOtherStructurePartCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addOutputBusCount
import space.impact.impact_compat.addon.gt.util.world.GTParticles
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.sound.SoundRes

class MultiSteamKineticForgeHammer : KineticMultiBlockBase<MultiSteamKineticForgeHammer>, ISurvivalConstructable {

    companion object {
        private const val UN_LOCAL = "multis.steam.kinetic.forge_hammer"
        private const val LOCAL = "Steam Kinetic Forge Hammer"
        private val STRUCTURE = StructureDefinition.builder<MultiSteamKineticForgeHammer>()
            .addShape(MAIN, arrayOf(arrayOf("ACA", " ~ ", "ADA"), arrayOf("AAA", " A ", "AAA"), arrayOf("AAA", "ABA", "AAA")))
            .addElement('A', ofBlock(CompatBlocks.BRONZE_MACHINE_CASING.block, SteamAgeBlocks.META_BRONZE_MACHINE_CASING))
            .addElement('B', lazy { _ ->
                buildHatchAdder(MultiSteamKineticForgeHammer::class.java)
                    .atLeast(RotorHatch)
                    .casingIndex(HATCH_INDEX_MACHINE_CASE_BRONZE)
                    .dot(3)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, SteamAgeBlocks.META_BRONZE_MACHINE_CASING)
            })
            .addElement('C', lazy { _ ->
                buildHatchAdder(MultiSteamKineticForgeHammer::class.java)
                    .atLeast(GT_HatchElement.InputBus)
                    .casingIndex(HATCH_INDEX_MACHINE_CASE_BRONZE)
                    .dot(2)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, SteamAgeBlocks.META_BRONZE_MACHINE_CASING)
            })
            .addElement('D', lazy { _ ->
                buildHatchAdder(MultiSteamKineticForgeHammer::class.java)
                    .atLeast(GT_HatchElement.OutputBus)
                    .casingIndex(HATCH_INDEX_MACHINE_CASE_BRONZE)
                    .dot(1)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, SteamAgeBlocks.META_BRONZE_MACHINE_CASING)
            })
            .build()
    }

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL, false)
    constructor(name: String) : super(name, false)

    override fun newMetaEntity(te: IGregTechTileEntity): IMetaTileEntity {
        return MultiSteamKineticForgeHammer(mName)
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, facing: ForgeDirection,
        colorIndex: Int, active: Boolean, redstoneLevel: Boolean,
    ): Array<ITexture> {
        val base = CompatTextures.CASE_MACHINE_BRONZE.factory()
        if (side == facing) return arrayOf(
            base,
            if (active) BlockIcons.OVERLAY_FRONT_STEAM_HAMMER_ACTIVE.factory()
            else BlockIcons.OVERLAY_FRONT_STEAM_HAMMER.factory(),
            if (active) TextureFactory.builder().addIcon(BlockIcons.OVERLAY_FRONT_STEAM_HAMMER_ACTIVE_GLOW).glow().build()
            else TextureFactory.builder().addIcon(BlockIcons.OVERLAY_FRONT_STEAM_HAMMER_GLOW).glow().build()
        )
        return arrayOf(base)
    }

    @SideOnly(Side.CLIENT)
    override fun getActivitySoundLoop(): ResourceLocation {
        return SoundRes.FORGE_HAMMER.resourceLocation
    }

    override fun getTimeBetweenProcessSounds(): Int {
        return 10
    }

    @SideOnly(Side.CLIENT)
    override fun onRandomDisplayTick(te: IGregTechTileEntity) {
        GTParticles.createSparklesMainFace(te, ParticleFX.LAVA)
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        return checkPiece(MAIN, 1, 1, 0) && rotorHatches.size == 1
    }

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder() //TODO
            .addMachineType("Forge Hammer")
            .beginStructureBlock(3, 3, 3, false)
            .addOtherStructurePartCount("Machine Rotor Hatch", dot = 1)
            .addInputBusCount(dot = 1)
            .addOutputBusCount(dot = 1)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun construct(stackSize: ItemStack, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, 1, 1, 0)
    }

    override fun survivalConstruct(stackSize: ItemStack?, elementBudget: Int, env: ISurvivalBuildEnvironment?): Int {
        return if (mMachine) -1
        else survivialBuildPiece(MAIN, stackSize, 1, 1, 0, elementBudget, env, false, true)
    }

    override fun getRecipeMap(): GT_Recipe.GT_Recipe_Map = SteamForgeHammerRecipeMap
    override fun getStructureDefinition(): IStructureDefinition<MultiSteamKineticForgeHammer>? = STRUCTURE
}
