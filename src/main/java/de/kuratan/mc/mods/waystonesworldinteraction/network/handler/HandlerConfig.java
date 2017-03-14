package de.kuratan.mc.mods.waystonesworldinteraction.network.handler;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.network.NetworkHandler;
import de.kuratan.mc.mods.waystonesworldinteraction.network.message.MessageConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerConfig implements IMessageHandler<MessageConfig, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(final MessageConfig message, MessageContext ctx) {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
            @Override
            public void run() {
                WaystonesWorldInteraction.instance.setConfig(message.getConfig());
            }
        });
        return null;
    }
}
