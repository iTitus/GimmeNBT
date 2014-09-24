package io.github.iTitus.GimmeNBT;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod(modid = GimmeNBT.MOD_ID, version = GimmeNBT.VERSION)
public class GimmeNBT {
	public static final String MOD_ID = "GimmeNBT";
	public static final String VERSION = "${version}";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(ToolTipEventHandler.INSTANCE);
	}
}
