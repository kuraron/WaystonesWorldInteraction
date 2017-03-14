package de.kuratan.mc.mods.waystonesworldinteraction.util;

import net.minecraftforge.common.config.Configuration;

public class WaystoneConfig {
    public static float soundVolume = 0.5f;

    public void reloadLocal(Configuration config) {
        soundVolume = config.getFloat("Sound Volume", "client", 0.5f, 0f, 1f, "The volume of the sound played when teleporting.");
    }
}
