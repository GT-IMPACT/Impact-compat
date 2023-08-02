package space.impact.impact_compat.addon.gt.util.world

import space.impact.impact_compat.addon.gt.base.multi.CompatMultiBlockBase
import space.impact.impact_compat.addon.gt.base.multi.KineticMultiBlockBase

fun CompatMultiBlockBase<*>.checkCountHatches(
    inBus: Int = 0,
    inHatch: Int = 0,
    outBus: Int = 0,
    outHatch: Int = 0,
): Boolean {
    return mInputBusses.size <= inBus && mInputHatches.size <= inHatch
            && mOutputBusses.size <= outBus && mOutputHatches.size <= outHatch
}

fun KineticMultiBlockBase<*>.checkCountHatches(
    inBus: Int = 0,
    inHatch: Int = 0,
    outBus: Int = 0,
    outHatch: Int = 0,
    rotorHatch: Int = 0,
): Boolean {
    return mInputBusses.size <= inBus && mInputHatches.size <= inHatch
            && mOutputBusses.size <= outBus && mOutputHatches.size <= outHatch
            && rotorHatches.size <= rotorHatch
}