package space.impact.impact_compat.common.network

import space.impact.impact_compat.common.tiles.WaterWhealTE
import space.impact.impact_compat.common.tiles.WindWhealTE
import space.impact.packet_network.network.packets.createPacketStream
import space.impact.packet_network.network.registerPacket

object Network {

    val PacketUpdateWhealMill = createPacketStream(3000) {isServer, data ->
        if (!isServer) {
            val isActive = data.readBoolean()
            when(val te = tileEntity) {
                is WaterWhealTE -> te.isAnimated = isActive
                is WindWhealTE -> te.isAnimated = isActive
            }
        }
    }

    fun registerPackets() {
        registerPacket(PacketUpdateWhealMill)
    }
}
