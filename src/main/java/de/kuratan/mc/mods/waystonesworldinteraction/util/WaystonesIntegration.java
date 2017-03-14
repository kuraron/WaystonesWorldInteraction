package de.kuratan.mc.mods.waystonesworldinteraction.util;

import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.io.File;
import java.util.Random;

import static de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction.logger;

public class WaystonesIntegration {

    public static final String NBT_WAYSTONE_NAME = "WaystoneName";
    public static final String NBT_WAYSTONE_IS_GLOBAL = "IsGlobal";
    public static final String NBT_WAYSTONE_X = "x";
    public static final String NBT_WAYSTONE_Y = "y";
    public static final String NBT_WAYSTONE_Z = "z";

    public static final String WAYSTONES_WORLDGEN_CANONICAL_NAME = "net.blay09.mods.waystones.worldgen.WaystoneWorldGen";
    public static final String WAYSTONES_MOD_ID = "waystones";

    private static WaystoneConfig config;

    public static WaystoneData getWaystoneDataFromTileEntity(TileEntity tileEntity) {
        NBTTagCompound tileNBT = tileEntity.serializeNBT();
        if (tileNBT.hasKey(NBT_WAYSTONE_NAME)) {
            return new WaystoneData(tileNBT.getString(NBT_WAYSTONE_NAME),
                    tileNBT.getInteger(NBT_WAYSTONE_X),
                    tileNBT.getInteger(NBT_WAYSTONE_Y),
                    tileNBT.getInteger(NBT_WAYSTONE_Z),
                    tileNBT.getBoolean(NBT_WAYSTONE_IS_GLOBAL));
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
            tileData.setString(NBT_WAYSTONE_NAME, waystoneName);
            tileEntity.deserializeNBT(tileData);
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
