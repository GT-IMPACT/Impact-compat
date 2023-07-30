@file:Suppress("unused")

package space.impact.impact_compat.proxy

import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import software.bernie.geckolib3.GeckoLib
import space.impact.impact_compat.addon.gt.GTAddon
import space.impact.impact_compat.addon.waila.Waila
import space.impact.impact_compat.client.register.initModels
import space.impact.impact_compat.common.tiles.registerTEModels
import space.impact.impact_compat.core.InitSide

class InitClient : InitSide {

    @SideOnly(Side.CLIENT)
    override fun preInit(e: FMLPreInitializationEvent) {
        GeckoLib.initialize(true)
    }

    @SideOnly(Side.CLIENT)
    override fun init(e: FMLInitializationEvent) {

    }

    @SideOnly(Side.CLIENT)
    override fun postInit(e: FMLPostInitializationEvent) {
        initModels()
    }
}

class InitServer : InitSide {

    @SideOnly(Side.SERVER)
    override fun preInit(e: FMLPreInitializationEvent) {

    }

    @SideOnly(Side.SERVER)
    override fun init(e: FMLInitializationEvent) {

    }

    @SideOnly(Side.SERVER)
    override fun postInit(e: FMLPostInitializationEvent) {

    }
}

class InitCommon : InitSide {

    override fun preInit(e: FMLPreInitializationEvent) {
        GeckoLib.initialize(e.side.isClient)
    }

    override fun init(e: FMLInitializationEvent) {
        registerTEModels()
        GTAddon.init()
        Waila.init()
    }

    override fun postInit(e: FMLPostInitializationEvent) {

    }
}
