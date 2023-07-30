package space.impact.impact_compat.addon.gt.features.steam_age.api

import gregtech.api.interfaces.tileentity.IGregTechTileEntity

class KineticLogicStore(private val machine: IKinetic) {

    private var lastSpeed: Int = -1

    fun injectSpeedFromRotor(te: IGregTechTileEntity, tick: Long) {
        if (te.isServerSide /*&& te.isActive */ && tick % 60 == 0L) {
            val rotor = te.getTileEntityAtSideAndDistance(machine.inputSide(), 1)
            if (rotor is IKinetic) {
                if (machine.currentSpeed() != rotor.currentSpeed()) {
                    machine.changeSpeed(rotor.currentSpeed())
                    lastSpeed = rotor.currentSpeed().speed
                }
            }
        }
    }
}
