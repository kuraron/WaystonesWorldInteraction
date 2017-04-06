package de.kuratan.mc.mods.waystonesworldinteraction.world;

import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;

import java.util.Random;

public abstract class WaystoneTemplate extends StructureComponentTemplate {
    protected abstract NameGenerator.WaystoneLocation getWaystoneLocation();

    @Override
    protected void handleDataMarker(String s, BlockPos blockPos, World world, Random random, StructureBoundingBox structureBoundingBox) {
        if (s.equals("Waystone")) {
            WaystonesIntegration.generateWaystoneInWorld(world, random, blockPos, getWaystoneLocation());
        }
    }
}
