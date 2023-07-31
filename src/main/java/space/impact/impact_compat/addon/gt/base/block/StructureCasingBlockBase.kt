@file:Suppress("LeakingThis")

package space.impact.impact_compat.addon.gt.base.block

import gregtech.api.GregTech_API
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.entity.EnumCreatureType
import net.minecraft.item.ItemStack
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

abstract class StructureCasingBlockBase(
    tool: String = WRENCH,
    material: Material = Material.iron,
) : Block(material) {

    companion object {
        protected const val WRENCH = "wrench"
    }

    init {
        GregTech_API.registerMachineBlock(this, -1)
        super.setHarvestLevel(tool, 1)
    }

    override fun damageDropped(meta: Int): Int {
        return meta
    }

    override fun canBeReplacedByLeaves(world: IBlockAccess?, x: Int, y: Int, z: Int): Boolean {
        return false
    }

    override fun canEntityDestroy(world: IBlockAccess?, x: Int, y: Int, z: Int, entity: Entity?): Boolean {
        return false
    }

    override fun canCreatureSpawn(type: EnumCreatureType?, world: IBlockAccess?, x: Int, y: Int, z: Int): Boolean {
        return false
    }

    override fun onBlockAdded(aWorld: World, aX: Int, aY: Int, aZ: Int) {
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ)
        }
    }

    override fun breakBlock(aWorld: World, aX: Int, aY: Int, aZ: Int, aBlock: Block?, aMetaData: Int) {
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ)
        }
    }

    fun register(meta: Int): ItemStack {
        return ItemStack(this, 1, meta)
    }
}
