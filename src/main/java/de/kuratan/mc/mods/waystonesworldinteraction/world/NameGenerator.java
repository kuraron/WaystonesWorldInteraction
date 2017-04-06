package de.kuratan.mc.mods.waystonesworldinteraction.world;

import net.minecraft.world.biome.Biome;

import java.util.Random;

public class NameGenerator {
    public enum WaystoneLocation {
        NONE(null),
        VILLAGE("Village"),
        STRONGHOLD("Stronghold"),
        SCATTERED("");

        public final String suffix;

        WaystoneLocation(String suffix) {
            this.suffix = suffix;
        }
    }

    public static String getName(Biome biome, Random random) {
        return getName(biome, random, WaystoneLocation.NONE);
    }

    public static String getName(Biome biome, Random random, WaystoneLocation waystoneLocation) {
        String name = net.blay09.mods.waystones.worldgen.NameGenerator.getName(biome, random);
        if (waystoneLocation.suffix != null && waystoneLocation.suffix.length() > 0) {
            return name + " " + waystoneLocation.suffix;
        }
        return name;
    }
}
