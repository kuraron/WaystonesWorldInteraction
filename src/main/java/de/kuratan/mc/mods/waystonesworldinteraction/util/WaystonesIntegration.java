package de.kuratan.mc.mods.waystonesworldinteraction.util;

import com.google.common.collect.Maps;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.io.File;
import java.util.Map;
import java.util.Random;

public class WaystonesIntegration extends WorldSavedData {

    private static final String DATA_NAME = WaystonesWorldInteraction.MOD_ID + "_GeneratedWaystones";
    private static final String TAG_LIST_NAME = "GeneratedWaystones";

    protected final Map<String, WaystoneData> generatedWaystones = Maps.newHashMap();

    public static final String WAYSTONE_TILE_NBT_NAME = "WaystoneName";
    public static final String WAYSTONE_TILE_NBT_X = "x";
    public static final String WAYSTONE_TILE_NBT_Y = "y";
    public static final String WAYSTONE_TILE_NBT_Z = "z";
    public static final String WAYSTONE_TILE_NBT_IS_GLOBAL = "IsGlobal";

    public static final String WAYSTONE_ENTRY_NBT_NAME = "Name";
    public static final String WAYSTONE_ENTRY_NBT_DIMENSION = "Dimension";
    public static final String WAYSTONE_ENTRY_NBT_POSITION = "Position";
    public static final String WAYSTONE_ENTRY_NBT_IS_GLOBAL = "IsGlobal";
    public static final String WAYSTONE_ENTRY_NBT_LOCATION = "Location";

    public static final String WAYSTONES_WORLDGEN_CANONICAL_NAME = "net.blay09.mods.waystones.worldgen.WaystoneWorldGen";
    public static final String WAYSTONES_MOD_ID = "waystones";

    private static WaystoneConfig config;

    WaystonesIntegration() {
        super(DATA_NAME);
    }

    public WaystonesIntegration(String name) {
        super(name);
    }

    public static WaystonesIntegration get(World world) {
        MapStorage storage = world.getMapStorage();
        if(storage != null) {
            WaystonesIntegration instance = (WaystonesIntegration) storage.getOrLoadData(WaystonesIntegration.class, DATA_NAME);
            if (instance == null) {
                instance = new WaystonesIntegration();
                storage.setData(DATA_NAME, instance);
            }
            return instance;
        }
        return new WaystonesIntegration();
    }

    public void addWaystone(WaystoneData data) {
        generatedWaystones.entrySet().removeIf(entry -> entry.getValue().pos.equals(data.pos));
        generatedWaystones.put(data.getName(), data);
        markDirty();
    }

    public WaystoneData getRandomWaystone() {
        return generatedWaystones.get(generatedWaystones.keySet().toArray()[0]);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList tagList = nbt.getTagList(TAG_LIST_NAME, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < tagList.tagCount(); i++) {
            WaystoneData entry = WaystoneData.read((NBTTagCompound) tagList.get(i));
            generatedWaystones.put(entry.name, entry);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        for (WaystoneData entry : generatedWaystones.values()) {
            tagList.appendTag(entry.writeToNBT());
        }
        compound.setTag(TAG_LIST_NAME, tagList);
        return compound;
    }

    public static WaystoneData getWaystoneDataFromTileEntity(TileEntity tileEntity) {
        NBTTagCompound tileNBT = tileEntity.serializeNBT();
        if (tileNBT.hasKey(WAYSTONE_TILE_NBT_NAME)) {
            return new WaystoneData(tileNBT.getString(WAYSTONE_TILE_NBT_NAME),
                    tileEntity.getWorld().provider.getDimension(),
                    tileNBT.getInteger(WAYSTONE_TILE_NBT_X),
                    tileNBT.getInteger(WAYSTONE_TILE_NBT_Y),
                    tileNBT.getInteger(WAYSTONE_TILE_NBT_Z),
                    tileNBT.getBoolean(WAYSTONE_TILE_NBT_IS_GLOBAL));
        }
        return null;
    }

    public static boolean isWaystonesWorldGen(IWorldGenerator worldGenerator) {
        return worldGenerator.getClass().getCanonicalName().equals(WAYSTONES_WORLDGEN_CANONICAL_NAME);
    }

    public static void generateWaystoneInWorld(World world, Random random, BlockPos blockPos) {
        generateWaystoneInWorld(world, random, blockPos, NameGenerator.WaystoneLocation.NONE);
    }

    public static void generateWaystoneInWorld(World world, Random random, BlockPos blockPos, NameGenerator.WaystoneLocation location) {
        EnumFacing facing = EnumFacing.Plane.HORIZONTAL.random(random);
        Block blockWaystone = Block.getBlockFromName("waystones:waystone");

        world.setBlockState(blockPos, blockWaystone.getStateFromMeta(facing.getIndex()|8));
        world.setBlockState(blockPos.add(0, 1, 0), blockWaystone.getStateFromMeta(facing.getIndex()));

        TileEntity tileEntity = world.getTileEntity(blockPos);
        WaystoneData waystoneData = getWaystoneDataFromTileEntity(tileEntity);
        if (waystoneData != null) {
            String waystoneName = NameGenerator.getName(world.getBiome(blockPos), random, location);
            NBTTagCompound tileData = tileEntity.serializeNBT();
            tileData.setString(WAYSTONE_TILE_NBT_NAME, waystoneName);
            tileEntity.deserializeNBT(tileData);
            WaystonesIntegration.get(world).addWaystone(getWaystoneDataFromTileEntity(tileEntity).setLocation(location));
        }
    }

    public static void getLocalWaystonesConfiguration(File configurationDir) {
        config = new WaystoneConfig();
        config.reloadLocal(new Configuration(new File(configurationDir, WAYSTONES_MOD_ID+".cfg")));
    }

    public static WaystoneConfig getWaystoneConfig() {
        return config;
    }
}
