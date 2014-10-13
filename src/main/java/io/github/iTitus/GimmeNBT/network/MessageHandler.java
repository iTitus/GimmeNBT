package io.github.iTitus.GimmeNBT.network;

import io.github.iTitus.GimmeNBT.GimmeNBT;
import io.github.iTitus.GimmeNBT.network.message.MessageDumpInv;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class MessageHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
			.newSimpleChannel(GimmeNBT.MOD_ID);

	public static void init() {
		INSTANCE.registerMessage(MessageDumpInv.class, MessageDumpInv.class, 0,
				Side.CLIENT);
	}

}
