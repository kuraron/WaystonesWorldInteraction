package de.kuratan.mc.mods.waystonesworldinteraction.world;

import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public abstract class WaystoneTemplate extends StructureComponentTemplate {
    protected abstract NameGenerator.WaystoneLocation getWaystoneLocation();

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        super.writeStructureToNBT(tagCompound);
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
        super.readStructureFromNBT(tagCompound, p_143011_2_);
    }

    @Override
    protected void handleDataMarker(String s, BlockPos blockPos, World world, Random random, StructureBoundingBox structureBoundingBox) {
        if (s.equals("Waystone")) {
            WaystonesIntegration.generateWaystoneInWorld(world, random, blockPos, getWaystoneLocation());
        }
    }
}
