package space.impact.impact_compat.common.tiles

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.Packet
import net.minecraft.tileentity.TileEntity
import space.impact.impact_compat.common.network.Network.PacketChangeActive
import space.impact.impact_compat.core.NBT
import space.impact.impact_compat.core.WorldAround
import space.impact.packet_network.network.NetworkHandler.sendToAllAround

class NonTickableTileBlock : TileEntity(), IBlockActive {

    private var isActive: Boolean = false

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setBoolean(NBT.NBT_ACTIVE, isActive)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        isActive = nbt.getBoolean(NBT.NBT_ACTIVE)
    }

    override fun setActive(isActive: Boolean) {
        this.isActive = isActive
        updateActivity()
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
    }

    override fun isActive(): Boolean {
        return isActive
    }

    override fun getDescriptionPacket(): Packet? {
        updateActivity()
        return null
    }

    override fun canUpdate(): Boolean {
        return false
    }

    private fun updateActivity() {
        if (!worldObj.isRemote)
            sendToAllAround(PacketChangeActive.transaction(isActive), WorldAround.CHUNK_8)
    }
}
