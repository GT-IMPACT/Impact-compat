package space.impact.impact_compat.addon.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import gregtech.api.enums.Mods;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import space.impact.impact_compat.common.tiles.base.BaseCompatTileEntity;

public class Waila {
	
	@SuppressWarnings("unused")
	public static void callbackRegister(IWailaRegistrar register) {
		final IWailaDataProvider multiBlockProvider = new CompactWailaDataProvider();
		register.registerBodyProvider(multiBlockProvider, BaseCompatTileEntity.class);
		register.registerNBTProvider(multiBlockProvider, BaseCompatTileEntity.class);
		register.registerTailProvider(multiBlockProvider, BaseCompatTileEntity.class);
	}
	
	public static void init() {
		FMLInterModComms.sendMessage(Mods.Waila.ID, "register", Waila.class.getName() + ".callbackRegister");
	}
}
