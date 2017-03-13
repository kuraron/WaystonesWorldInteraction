package de.kuratan.mc.mods.waystonesworldinteraction.network.message;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteractionConfig;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageConfig implements IMessage {

    private WaystonesWorldInteractionConfig config;

    public MessageConfig() {

    }

    public MessageConfig(WaystonesWorldInteractionConfig config) {
        this.config = config;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        config = WaystonesWorldInteractionConfig.read(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        config.write(buf);
    }

    public WaystonesWorldInteractionConfig getConfig() {
        return config;
    }
}
