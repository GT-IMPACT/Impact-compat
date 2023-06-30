@file:Suppress("unused")

package space.impact.impact_compat

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import space.impact.impact_compat.common.network.Network
import space.impact.impact_compat.core.Config
import space.impact.impact_compat.core.InitSide
import space.impact.impact_compat.proxy.InitCommon

@Mod(
    modid = MODID,
    name = MODNAME,
    version = VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    dependencies = "required-after:gregtech"
)
object ImpactCompat {

    init {
        Network.registerPackets()
    }

    @SidedProxy(
        clientSide = "$GROUPNAME.$MODID.proxy.InitClient",
        serverSide = "$GROUPNAME.$MODID.proxy.InitServer"
    )
    private lateinit var proxyInit: InitSide
    private val initCommon: InitSide = InitCommon()

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Config.createConfig(event.modConfigurationDirectory)
        proxyInit.preInit(event)
        initCommon.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxyInit.init(event)
        initCommon.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxyInit.postInit(event)
        initCommon.postInit(event)
    }
}
