package space.impact.impact_compat.core

import cpw.mods.fml.common.FMLLog
import org.apache.logging.log4j.Level

object CLog {

    var isDebug: Boolean = false

    fun i(text: String) {
        FMLLog.log(Level.INFO, text)
    }

    fun e(text: String) {
        if (isDebug) FMLLog.log(Level.ERROR, text)
    }

    fun d(text: String) {
        if (isDebug) FMLLog.log(Level.DEBUG, text)
    }
}
