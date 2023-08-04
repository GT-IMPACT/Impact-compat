package space.impact.impact_compat.addon.gt.features.steam_age.machines.processing

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility.*
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.enums.GT_HatchElement
import gregtech.api.enums.SoundResource
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import gregtech.api.util.GT_Recipe
import gregtech.api.util.GT_StructureUtility.buildHatchAdder
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.base.multi.KineticMultiBlockBase
import space.impact.impact_compat.addon.gt.features.steam_age.recipes.STEAM_CENTRIFUGE_RECIPE_MAP
import space.impact.impact_compat.addon.gt.items.CompatBlocks
import space.impact.impact_compat.addon.gt.util.textures.CompatTextures
import space.impact.impact_compat.addon.gt.util.textures.HatchTexture
import space.impact.impact_compat.addon.gt.util.textures.factory
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addInputBusCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addOutputBusCount
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addRotorHatch
import space.impact.impact_compat.addon.gt.util.tooltip.TooltipExt.addSteamMachineStructure
import space.impact.impact_compat.addon.gt.util.world.CompatHatchElement
import space.impact.impact_compat.addon.gt.util.world.checkCountHatches
import space.impact.impact_compat.common.util.merch.Tags
import space.impact.impact_compat.common.util.world.Vec3

class MultiSteamKineticCentrifuge : KineticMultiBlockBase<MultiSteamKineticCentrifuge>, ISurvivalConstructable {

    companion object {
        private const val UN_LOCAL = "multis.steam.kinetic.centrifuge"
        private const val LOCAL = "Steam Bronze Centrifuge"
        private val STRUCTURE = StructureDefinition.builder<MultiSteamKineticCentrifuge>()
            .addShape(
                MAIN, transpose(
                    arrayOf(
                        arrayOf("     ", " AAA ", " AAA ", " AAA ", "     "),
                        arrayOf("     ", " BAB ", " A A ", " BAB ", "     "),
                        arrayOf("  A  ", " BAB ", "AA AA", " BAB ", "  A  "),
                        arrayOf(" AAA ", "AAAAA", "AA AA", "AAAAA", " ADA "),
                        arrayOf(" A~A ", "AAAAA", "AAAAA", "AAAAA", " AAA ")
                    ))
            )
            .addElement('A', lazy { _ ->
                buildHatchAdder(MultiSteamKineticCentrifuge::class.java)
                    .atLeast(GT_HatchElement.InputBus, GT_HatchElement.OutputBus)
                    .casingIndex(HatchTexture.MACHINE_CAGE_BRONZE.index)
                    .dot(1)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, CompatBlocks.BRONZE_MACHINE_CASING.meta)
            })
            .addElement('B', ofBlock(CompatBlocks.BRONZE_GLASS_CASING.block, CompatBlocks.BRONZE_GLASS_CASING.meta))
            .addElement('D', lazy { _ ->
                buildHatchAdder(MultiSteamKineticCentrifuge::class.java)
                    .atLeast(CompatHatchElement.RotorHatch)
                    .casingIndex(HatchTexture.MACHINE_CAGE_BRONZE.index)
                    .dot(2)
                    .buildAndChain(CompatBlocks.BRONZE_MACHINE_CASING.block, CompatBlocks.BRONZE_MACHINE_CASING.meta)
            })
            .build()
        private val OFFSET_STRUCTURE = Vec3(2, 4, 0)
    }

    constructor(id: Int) : super(id, LOCAL, UN_LOCAL, false)
    constructor(name: String) : super(name, false)
    override fun newMetaEntity(te: IGregTechTileEntity): IMetaTileEntity = MultiSteamKineticCentrifuge(mName)

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder()
            .addMachineType("Centrifuge")
            .beginStructureBlock(5, 5, 5, true)
            .addSteamMachineStructure(58..61, bronzeGlasses = 8)
            .addRotorHatch(dot = 1)
            .addInputBusCount(dot = 2)
            .addOutputBusCount(dot = 2)
            .apply { toolTipFinisher(Tags.IMPACT_GREGTECH) }
    }

    override fun getTexture(
        te: IGregTechTileEntity, side: ForgeDirection, facing: ForgeDirection,
        colorIndex: Int, active: Boolean, redstoneLevel: Boolean,
    ): Array<ITexture> {
        val base = CompatTextures.CASE_MACHINE_BRONZE.factory()
        if (side == facing) return arrayOf(
            base,
            if (active) CompatTextures.OVERLAY_CENTRIFUGE_ACTIVE.factory()
            else CompatTextures.OVERLAY_CENTRIFUGE.factory(),
        )
        return arrayOf(base)
    }

    override fun checkMachine(te: IGregTechTileEntity, aStack: ItemStack?): Boolean {
        return checkPiece(MAIN, OFFSET_STRUCTURE) && checkCountHatches(inBus = 1, outBus = 1, rotorHatch = 1)
    }

    override fun construct(stackSize: ItemStack, hintsOnly: Boolean) {
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_STRUCTURE)
    }

    override fun survivalConstruct(stackSize: ItemStack?, elementBudget: Int, env: ISurvivalBuildEnvironment?): Int {
        return if (mMachine) -1
        else survivalBuildPiece(MAIN, stackSize, OFFSET_STRUCTURE, elementBudget, env)
    }

    override fun getRecipeMap(): GT_Recipe.GT_Recipe_Map = STEAM_CENTRIFUGE_RECIPE_MAP
    override fun getStructureDefinition(): IStructureDefinition<MultiSteamKineticCentrifuge>? = STRUCTURE

    @SideOnly(Side.CLIENT)
    override fun activeSound(): ResourceLocation = SoundResource.IC2_MACHINES_RECYCLER_OP.resourceLocation
}
