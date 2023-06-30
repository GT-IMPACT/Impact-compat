package space.impact.impact_compat.core

import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent

interface InitSide {
    fun preInit(e: FMLPreInitializationEvent)
    fun init(e: FMLInitializationEvent)
    fun postInit(e: FMLPostInitializationEvent)
}
