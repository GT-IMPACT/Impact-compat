package space.impact.impact_compat.common.item.tool

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.S23PacketBlockChange
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.events.RenderDestroyBlockProgressEvent
import space.impact.impact_compat.events.RenderSelectionBoxEvent

class ToolForgeHammer : ToolBaseItem("tool.forge_hammer", "Forge Hammer", "Hulk Smash") {

    companion object {
        val INSTANCE = ToolForgeHammer()
    }

    private var breakRadius = 1
    private var breakDepth = 0

    init {
        setFull3D()
        setMaxStackSize(1)
        setHarvestLevel("pickaxe", Int.MAX_VALUE)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onRenderDestroyBlockEvent(e: RenderDestroyBlockProgressEvent) {
        if (e.player.currentEquippedItem?.item is ToolForgeHammer) {
            calculateCoordinates(
                player = e.player,
                x = e.block.partialBlockX,
                y = e.block.partialBlockY,
                z = e.block.partialBlockZ,
                coords = e::addBlock,
            )
        } else e.isCanceled = true
    }

    @SubscribeEvent
    fun onRenderOutlineBlock(e: RenderSelectionBoxEvent) {
        if (e.player.currentEquippedItem?.item is ToolForgeHammer) {
            calculateCoordinates(
                player = e.player,
                x = e.block.x,
                y = e.block.y,
                z = e.block.z,
                coords = e::addBlock,
            )
        } else e.isCanceled = true
    }

    override fun getDigSpeed(itemstack: ItemStack?, block: Block?, metadata: Int): Float {
        return 3f
    }

    override fun onBlockStartBreak(itemstack: ItemStack, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
        return startBreak(x, y, z, player)
    }

    @Suppress("SameReturnValue")
    private fun calculateCoordinates(
        player: EntityPlayer, x: Int, y: Int, z: Int,
        coords: (xPos: Int, yPos: Int, zPos: Int) -> Unit
    ): Boolean {
        val mop = raytraceFromEntity(player.worldObj, player, false, 4.5) ?: return false
        val sideHit = ForgeDirection.getOrientation(mop.sideHit)

        var xRange = breakRadius
        var yRange = breakRadius
        var zRange = breakDepth
        when (sideHit) {
            ForgeDirection.DOWN, ForgeDirection.UP -> {
                yRange = breakDepth
                zRange = breakRadius
            }

            ForgeDirection.NORTH, ForgeDirection.SOUTH -> {
                xRange = breakRadius
                zRange = breakDepth
            }

            ForgeDirection.WEST, ForgeDirection.EAST -> {
                xRange = breakDepth
                zRange = breakRadius
            }

            else -> Unit
        }
        for (xPos in x - xRange..x + xRange) {
            for (yPos in y - yRange..y + yRange) {
                for (zPos in z - zRange..z + zRange) {
                    if (xPos == x && yPos == y && zPos == z && player is EntityPlayerMP) continue
                    coords(xPos, yPos, zPos)
                }
            }
        }
        return false
    }

    private fun startBreak(x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
        calculateCoordinates(player, x, y, z) { xPos, yPos, zPos ->
            breakBlock(player.worldObj, xPos, yPos, zPos, player, x, y, z)
        }
        return false
    }

    private fun breakBlock(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, refX: Int, refY: Int, refZ: Int) {
        if (world.isAirBlock(x, y, z)) return
        if (player !is EntityPlayerMP) return
        val block = world.getBlock(x, y, z)
        val meta = world.getBlockMetadata(x, y, z)
        val refBlock = world.getBlock(refX, refY, refZ)
        val refStrength = ForgeHooks.blockStrength(refBlock, player, world, refX, refY, refZ)
        val strength = ForgeHooks.blockStrength(block, player, world, x, y, z)
        if (!ForgeHooks.canHarvestBlock(block, player, meta) || refStrength / strength > 10f) return
        val event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.gameType, player, x, y, z)
        if (event.isCanceled) return
        if (player.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, x, y, z, meta, player)
            if (block.removedByPlayer(world, player, x, y, z, false)) block.onBlockDestroyedByPlayer(world, x, y, z, meta)
            if (!world.isRemote) {
                player.playerNetServerHandler.sendPacket(S23PacketBlockChange(x, y, z, world))
            }
            return
        }
        val currentItem = player.currentEquippedItem
        currentItem?.func_150999_a(world, block, x, y, z, player)
        if (!world.isRemote) {
            block.onBlockHarvested(world, x, y, z, meta, player)
            if (block.removedByPlayer(world, player, x, y, z, true)) {
                block.onBlockDestroyedByPlayer(world, x, y, z, meta)
                block.harvestBlock(world, player, x, y, z, meta)
                block.dropXpOnBlockBreak(world, x, y, z, event.expToDrop)
            }
            player.playerNetServerHandler.sendPacket(S23PacketBlockChange(x, y, z, world))
        } else {
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta shl 12))
            if (block.removedByPlayer(world, player, x, y, z, true)) {
                block.onBlockDestroyedByPlayer(world, x, y, z, meta)
            }
            val itemstack = player.currentEquippedItem
            if (itemstack != null) {
                itemstack.func_150999_a(world, block, x, y, z, player)
                if (itemstack.stackSize == 0) {
                    player.destroyCurrentEquippedItem()
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun raytraceFromEntity(world: World, player: Entity, par3: Boolean, range: Double): MovingObjectPosition? {
        val f = 1.0f
        val f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f
        val f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f
        val d0 = player.prevPosX + (player.posX - player.prevPosX) * f.toDouble()
        var d1 = player.prevPosY + (player.posY - player.prevPosY) * f.toDouble()
        if (!world.isRemote && player is EntityPlayer) d1 += 1.62
        val d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f.toDouble()
        val vec3 = Vec3.createVectorHelper(d0, d1, d2)
        val radian = (Math.PI / 180).toFloat()
        val f3 = MathHelper.cos(-f2 * radian - Math.PI.toFloat())
        val f4 = MathHelper.sin(-f2 * radian - Math.PI.toFloat())
        val f5 = -MathHelper.cos(-f1 * radian)
        val f6 = MathHelper.sin(-f1 * radian)
        val f7 = f4 * f5
        val f8 = f3 * f5
        var d3 = range
        if (player is EntityPlayerMP) {
            d3 = player.theItemInWorldManager.blockReachDistance
        }
        val vec31 = vec3.addVector(f7.toDouble() * d3, f6.toDouble() * d3, f8.toDouble() * d3)
        return world.func_147447_a(vec3, vec31, par3, !par3, par3)
    }
}
