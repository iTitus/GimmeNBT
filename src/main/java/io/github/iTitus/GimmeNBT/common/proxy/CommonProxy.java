package io.github.iTitus.GimmeNBT.common.proxy;

import io.github.iTitus.GimmeNBT.common.lib.LibMod;
import io.github.iTitus.GimmeNBT.common.network.MessageHandler;
import io.github.iTitus.GimmeNBT.server.command.DumpCommand;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
		FMLLog.info("%s v%s successfully loaded on side %s!", LibMod.MOD_ID,
				LibMod.MOD_VERSION, event.getSide().toString());
	}

	public void preInit(FMLPreInitializationEvent event) {
		MessageHandler.init();
	}

	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new DumpCommand());
	}

}
