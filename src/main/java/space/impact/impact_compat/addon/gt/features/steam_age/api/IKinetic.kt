package space.impact.impact_compat.addon.gt.features.steam_age.api

import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.common.util.translate.Translate.translate
import space.impact.impact_compat.common.util.translate.TranslateObjects
import space.impact.impact_compat.core.NBT

interface IKinetic {

    fun hasInput(): Boolean
    fun hasOutput(): Boolean
    fun currentSpeed(): KineticSpeed
    fun changeSpeed(speed: KineticSpeed)
    fun inputSide(): ForgeDirection

    fun wailaBody(tt: MutableList<String>, accessor: IWailaDataAccessor) {
        val tag = accessor.nbtData
        when (tag.getInteger(NBT.NBT_SPEED)) {
            KineticSpeed.HIGH.speed -> tt.add(TranslateObjects.KINETIC_LANG_SPEED.translate(TranslateObjects.KINETIC_LANG_SEPARATOR, TranslateObjects.KINETIC_LANG_HIGH))
            KineticSpeed.MEDIUM.speed -> tt.add(TranslateObjects.KINETIC_LANG_SPEED.translate(TranslateObjects.KINETIC_LANG_SEPARATOR, TranslateObjects.KINETIC_LANG_MEDIUM))
            KineticSpeed.LOW.speed -> tt.add(TranslateObjects.KINETIC_LANG_SPEED.translate(TranslateObjects.KINETIC_LANG_SEPARATOR, TranslateObjects.KINETIC_LANG_LOW))
            else -> tt.add(TranslateObjects.KINETIC_LANG_STOP.translate())
        }
    }

    fun wailaNBTData(tag: NBTTagCompound) {
        tag.setInteger(NBT.NBT_SPEED, currentSpeed().speed)
    }
}

enum class KineticSpeed(val speed: Int) {
    STOP(0),
    LOW(1),
    MEDIUM(2),
    HIGH(4);

    fun saveNBT(nbt: NBTTagCompound) {
        nbt.setInteger(NBT.NBT_SPEED, this.speed)
    }

    companion object {
        fun typeOf(speed: Int) = when(speed) {
            HIGH.speed -> HIGH
            MEDIUM.speed -> MEDIUM
            LOW.speed -> LOW
            else -> STOP
        }

        fun loadNBT(nbt: NBTTagCompound): KineticSpeed {
            return KineticSpeed.typeOf(nbt.getInteger(NBT.NBT_SPEED))
        }
    }
}
