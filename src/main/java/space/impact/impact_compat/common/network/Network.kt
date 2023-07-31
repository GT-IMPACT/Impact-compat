package space.impact.impact_compat.common.network

import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.addon.gt.features.steam_age.api.IKinetic
import space.impact.impact_compat.addon.gt.features.steam_age.api.KineticSpeed
import space.impact.impact_compat.common.tiles.BaseCompatTileEntity
import space.impact.impact_compat.common.tiles.BaseTileEntityModel
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

    val PacketStartBaseTile = createPacketStream(3001) { _, data ->
        val isActive = data.readBoolean()
        (tileEntity as? BaseCompatTileEntity)?.setActive(isActive)
    }

    val PacketChangeSide = createPacketStream(3002) { _, data ->
        val side = ForgeDirection.getOrientation(data.readInt())
        (tileEntity as? BaseCompatTileEntity)?.setFrontFacing(side)
    }

    val PacketSteamRotor = createPacketStream(3003) {isServer, data ->
        val speed = KineticSpeed.typeOf(data.readInt())
        if (!isServer) (tileEntity as? IKinetic)?.changeSpeed(speed)
    }

    fun registerPackets() {
        registerPacket(PacketUpdateModelAnimate)
        registerPacket(PacketStartBaseTile)
        registerPacket(PacketChangeSide)
        registerPacket(PacketSteamRotor)
    }
}
