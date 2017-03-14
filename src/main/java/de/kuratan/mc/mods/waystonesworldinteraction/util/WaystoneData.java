package de.kuratan.mc.mods.waystonesworldinteraction.util;

import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class WaystoneData {
    protected String name;
    protected int dimension;
    protected BlockPos pos;
    protected boolean global;
    protected NameGenerator.WaystoneLocation location;

    public WaystoneData(String name, int dimension, BlockPos pos, boolean global) {
        this.name = name;
        this.dimension = dimension;
        this.pos = pos;
        this.global = global;
        this.location = NameGenerator.WaystoneLocation.NONE;
    }

    public WaystoneData(String name, int dimension, int x, int y, int z, boolean global) {
        this(name, dimension, new BlockPos(x, y, z), global);
    }

    public WaystoneData setLocation(NameGenerator.WaystoneLocation location) {
        this.location = location;
        return this;
    }

    public static WaystoneData read(NBTTagCompound nbt) {
        return new WaystoneData(nbt.getString(WaystonesIntegration.WAYSTONE_ENTRY_NBT_NAME),
                nbt.getInteger(WaystonesIntegration.WAYSTONE_ENTRY_NBT_DIMENSION),
                BlockPos.fromLong(nbt.getLong(WaystonesIntegration.WAYSTONE_ENTRY_NBT_POSITION)),
                nbt.getBoolean(WaystonesIntegration.WAYSTONE_ENTRY_NBT_IS_GLOBAL)).setLocation(NameGenerator.WaystoneLocation.values()[nbt.getInteger(WaystonesIntegration.WAYSTONE_ENTRY_NBT_LOCATION)]);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString(WaystonesIntegration.WAYSTONE_ENTRY_NBT_NAME, name);
        tagCompound.setInteger(WaystonesIntegration.WAYSTONE_ENTRY_NBT_DIMENSION, dimension);
        tagCompound.setLong(WaystonesIntegration.WAYSTONE_ENTRY_NBT_POSITION, pos.toLong());
        tagCompound.setBoolean(WaystonesIntegration.WAYSTONE_ENTRY_NBT_IS_GLOBAL, global);
        tagCompound.setInteger(WaystonesIntegration.WAYSTONE_ENTRY_NBT_LOCATION, location.ordinal());
        return tagCompound;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isGlobal() {
        return global;
    }
}
