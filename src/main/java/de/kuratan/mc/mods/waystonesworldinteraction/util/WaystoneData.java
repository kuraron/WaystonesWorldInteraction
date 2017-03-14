package de.kuratan.mc.mods.waystonesworldinteraction.util;

import net.minecraft.util.math.BlockPos;

public class WaystoneData {
    public final String name;
    public final BlockPos pos;
    public final boolean global;

    public WaystoneData(String name, BlockPos pos, boolean global) {
        this.name = name;
        this.pos = pos;
        this.global = global;
    }

    public WaystoneData(String name, int x, int y, int z, boolean global) {
        this(name, new BlockPos(x, y, z), global);
    }
}
