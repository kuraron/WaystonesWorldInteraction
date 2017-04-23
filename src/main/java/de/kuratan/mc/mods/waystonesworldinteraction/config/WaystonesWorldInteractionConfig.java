package de.kuratan.mc.mods.waystonesworldinteraction.config;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;

public class WaystonesWorldInteractionConfig {
    public boolean deactivateOriginalWorldGen;
    public boolean createVillageWaystones;
    public boolean createStrongholdWaystones;
    public boolean spawnWaystonesVillagers;
    public boolean enableWaystoneShardWorldGen;
    public boolean enableWaystoneScatteredFeatures;
    public int maxDistanceBetweenScatteredFeatures;

    public boolean removeOriginalWarpstoneRecepie;
    public boolean allowBoundScrollRebind;
    public int chargesPerEnderPearl;

    public void reloadLocal(Configuration config) {
        deactivateOriginalWorldGen = config.getBoolean("Deactivate Original WorldGen", "worldgen", true, "Shall the original WorldGen be deactivated.");
        createVillageWaystones = config.getBoolean("Spawn Waystones in Villages", "worldgen", false, "Shall Waystones appear in villages");
        createStrongholdWaystones = config.getBoolean("Spawn Waystones in Strongholds", "worldgen", false, "Shall Waystones appear in strongholds");
        spawnWaystonesVillagers = config.getBoolean("Spawn Waystones Villager", "worldgen", true, "Shall specialized Villagers spawn");

        enableWaystoneShardWorldGen = config.getBoolean("Generate Waystone Shard Ore", "worldgen", false, "Spawn Waystone Shards like Emerald ore");
        enableWaystoneScatteredFeatures = config.getBoolean("Generate Scattered Waystones", "worldgen", true, "Spawn scattered Waystones in the World");
        maxDistanceBetweenScatteredFeatures = config.getInt("Max Dist Scattered", "worldgen", 32, 8, 128, "Maximal Distance between scattered Features");

        removeOriginalWarpstoneRecepie = config.getBoolean("Deactivate original warp stone recepie", "general", true, "Removes the original recepie for the warp stone");
        allowBoundScrollRebind = config.getBoolean("Rebind Bound Scroll", "general", false, "Allow players to rebind bound scrolls by sneak-rightclick");
        chargesPerEnderPearl = config.getInt("Charges per Enderpearl", "general", 2, 1, 4, "Charges gained per enderparl");
    }

    public static WaystonesWorldInteractionConfig read(ByteBuf buf) {
        WaystonesWorldInteractionConfig config = new WaystonesWorldInteractionConfig();
        config.deactivateOriginalWorldGen = buf.readBoolean();
        config.createVillageWaystones = buf.readBoolean();
        config.createStrongholdWaystones = buf.readBoolean();
        config.spawnWaystonesVillagers = buf.readBoolean();
        config.enableWaystoneShardWorldGen = buf.readBoolean();
        config.removeOriginalWarpstoneRecepie = buf.readBoolean();
        config.allowBoundScrollRebind = buf.readBoolean();
        config.chargesPerEnderPearl = buf.readInt();
        config.enableWaystoneScatteredFeatures = buf.readBoolean();
        config.maxDistanceBetweenScatteredFeatures = buf.readInt();
        return config;
    }

    public void write(ByteBuf buf) {
        buf.writeBoolean(deactivateOriginalWorldGen);
        buf.writeBoolean(createVillageWaystones);
        buf.writeBoolean(createStrongholdWaystones);
        buf.writeBoolean(spawnWaystonesVillagers);
        buf.writeBoolean(enableWaystoneShardWorldGen);
        buf.writeBoolean(removeOriginalWarpstoneRecepie);
        buf.writeBoolean(allowBoundScrollRebind);
        buf.writeInt(chargesPerEnderPearl);
        buf.writeBoolean(enableWaystoneScatteredFeatures);
        buf.writeInt(maxDistanceBetweenScatteredFeatures);
    }
}
