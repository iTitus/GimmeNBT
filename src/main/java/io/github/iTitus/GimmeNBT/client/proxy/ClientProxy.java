package io.github.iTitus.GimmeNBT.client.proxy;

import io.github.iTitus.GimmeNBT.client.handler.GimmeNBTEventHandler;
import io.github.iTitus.GimmeNBT.common.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new GimmeNBTEventHandler());
    }

}
