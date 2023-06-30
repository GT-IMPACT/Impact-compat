package space.impact.impact_compat.core

import net.minecraftforge.common.config.Configuration
import java.io.File

object Config {

    private const val GENERAL = "GENERAL"

    private inline fun onPostCreate(configFile: File?, crossinline action: (Configuration) -> Unit) {
        Configuration(configFile).also { config ->
            config.load()
            action(config)
            if (config.hasChanged()) {
                config.save()
            }
        }
    }

    fun createConfig(configFile: File?) {
        val config = File(File(configFile, "IMPACT"), "COMPAT.cfg")
        onPostCreate(config) { cfg ->
            CLog.isDebug = cfg[GENERAL, "isEnableDebugLog", false].boolean

        }
    }
}
