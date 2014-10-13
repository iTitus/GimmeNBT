package io.github.iTitus.GimmeNBT;

import io.github.iTitus.GimmeNBT.command.DumpCommand;
import io.github.iTitus.GimmeNBT.handler.GimmeNBTEventHandler;
import io.github.iTitus.GimmeNBT.network.MessageHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = GimmeNBT.MOD_ID, version = GimmeNBT.VERSION)
public class GimmeNBT {

	@Instance
	public static GimmeNBT INSTANCE;

	public static final String MOD_ID = "GimmeNBT";
	public static final String VERSION = "@VERSION@";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MessageHandler.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			GimmeNBTEventHandler handler = new GimmeNBTEventHandler();
			MinecraftForge.EVENT_BUS.register(handler);
			FMLLog.info("%s v%s successfully loaded on side CLIENT!", MOD_ID,
					VERSION);
		}
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new DumpCommand());
		FMLLog.info("%s v%s successfully loaded on side SERVER!", MOD_ID,
				VERSION);
	}

}
