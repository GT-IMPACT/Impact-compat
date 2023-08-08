package space.impact.impact_compat.addon.gt.util.world

import com.gtnewhorizon.structurelib.StructureLibAPI
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment
import com.gtnewhorizon.structurelib.structure.IItemSource
import com.gtnewhorizon.structurelib.structure.IStructureElement
import com.gtnewhorizon.structurelib.structure.IStructureElement.BlocksToPlace
import com.gtnewhorizon.structurelib.structure.IStructureElement.PlaceResult
import gregtech.api.enums.Materials
import gregtech.api.metatileentity.BaseMetaPipeEntity
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Fluid
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.IChatComponent
import net.minecraft.world.World
import java.util.function.Consumer

object CompatStructureUtility {
    fun <T> ofFluidPipe(aFrameMaterial: Materials, dot: Int): IStructureElement<T> {
        return object : IStructureElement<T> {
            override fun check(t: T, world: World, x: Int, y: Int, z: Int): Boolean {
                val tBase = world.getTileEntity(x, y, z)
                if (tBase is BaseMetaPipeEntity) {
                    if (tBase.isInvalidTileEntity) return false
                    if (tBase.metaTileEntity is GT_MetaPipeEntity_Fluid)
                        return aFrameMaterial == (tBase.metaTileEntity as GT_MetaPipeEntity_Fluid).mMaterial
                }
                return false
            }

            override fun spawnHint(t: T, world: World, x: Int, y: Int, z: Int, trigger: ItemStack): Boolean {
                StructureLibAPI.hintParticle(world, x, y, z, StructureLibAPI.getBlockHint(), dot)
                return true
            }

            override fun placeBlock(t: T, world: World, x: Int, y: Int, z: Int, trigger: ItemStack): Boolean {
                return false
            }

            override fun getBlocksToPlace(t: T, world: World, x: Int, y: Int, z: Int, trigger: ItemStack, env: AutoPlaceEnvironment): BlocksToPlace? {
                return BlocksToPlace.createEmpty()
            }

            override fun survivalPlaceBlock(
                t: T, world: World, x: Int, y: Int, z: Int, trigger: ItemStack,
                s: IItemSource, actor: EntityPlayerMP, chatter: Consumer<IChatComponent>
            ): PlaceResult {
                return PlaceResult.SKIP
            }

            override fun survivalPlaceBlock(t: T, world: World, x: Int, y: Int, z: Int, trigger: ItemStack, env: AutoPlaceEnvironment): PlaceResult {
                return PlaceResult.SKIP
            }
        }
    }
}