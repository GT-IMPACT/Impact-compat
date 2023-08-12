package space.impact.impact_compat.events

import cpw.mods.fml.common.eventhandler.Cancelable
import cpw.mods.fml.common.eventhandler.Event
import net.minecraft.client.renderer.DestroyBlockProgress
import net.minecraft.entity.player.EntityPlayer

@Cancelable
class RenderDestroyBlockProgressEvent(
    val player: EntityPlayer,
    val block: DestroyBlockProgress,
) : Event() {

    private val destroyCandidates: ArrayList<DestroyBlockProgress> = arrayListOf(block)

    fun addBlock(x: Int, y: Int, z: Int) {
        destroyCandidates += DestroyBlockProgress(player.entityId, x, y, z)
    }

    fun getBlocks(): Array<DestroyBlockProgress> {
        return destroyCandidates.toTypedArray()
    }
}

@Cancelable
class RenderSelectionBoxEvent(
    val player: EntityPlayer,
    val block: BlockCoordinate,
) : Event() {

    private val destroyCandidates: ArrayList<BlockCoordinate> = arrayListOf(block)

    fun addBlock(x: Int, y: Int, z: Int) {
        destroyCandidates += BlockCoordinate(x, y, z)
    }

    fun getBlocks(): Array<BlockCoordinate> {
        return destroyCandidates.toTypedArray()
    }

    data class BlockCoordinate(val x: Int, val y: Int, val z: Int)
}
