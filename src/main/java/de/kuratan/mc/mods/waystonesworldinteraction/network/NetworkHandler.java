package de.kuratan.mc.mods.waystonesworldinteraction.network;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.network.handler.HandlerConfig;
import de.kuratan.mc.mods.waystonesworldinteraction.network.message.MessageConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkHandler {
    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(WaystonesWorldInteraction.MOD_ID);

    public static void init() {
        channel.registerMessage(HandlerConfig.class, MessageConfig.class, 0 , Side.CLIENT);
    }

    public static IThreadListener getThreadListener(MessageContext ctx) {
        return ctx.side == Side.SERVER ? (WorldServer) ctx.getServerHandler().playerEntity.world : getClientThreadListener();
    }

    @SideOnly(Side.CLIENT)
    public static IThreadListener getClientThreadListener() {
        return Minecraft.getMinecraft();
    }
}
