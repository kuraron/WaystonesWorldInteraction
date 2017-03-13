package de.kuratan.mc.mods.waystonesworldinteraction.network;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.network.handler.HandlerConfig;
import de.kuratan.mc.mods.waystonesworldinteraction.network.message.MessageConfig;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(WaystonesWorldInteraction.MOD_ID);

    public static void init() {
        channel.registerMessage(HandlerConfig.class, MessageConfig.class, 0 , Side.CLIENT);
    }
}
