package space.impact.impact_compat.common.network

import space.impact.impact_compat.common.tiles.BaseTileEntityModel
import space.impact.impact_compat.common.tiles.BaseTileRotationEntityModel
import space.impact.packet_network.network.packets.createPacketStream
import space.impact.packet_network.network.registerPacket

object Network {

    val PacketUpdateModelAnimate = createPacketStream(3000) { isServer, data ->
        if (!isServer) {
            val isActive = data.readBoolean()
            (tileEntity as? BaseTileEntityModel)?.apply {
                isAnimated = isActive
                isFirst = false
                if (isActive) isWork = true
            }
        }
    }

    fun registerPackets() {
        registerPacket(PacketUpdateModelAnimate)
    }
}
