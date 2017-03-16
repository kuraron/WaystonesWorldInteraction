package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Random;

public abstract class VillageWaystone extends StructureVillagePieces.House1 {

    public VillageWaystone() {}

    public VillageWaystone(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(startPiece, componentType, random, boundingBox, facing);
    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + this.boundingBoxYTranspose() - 1, 0);
        }
        if (worldIn.provider.getDimension() != 0) {
            return false;
        }

        generateWaystoneStructure(worldIn, randomIn, structureBoundingBoxIn);
        return true;
    }

    protected abstract int boundingBoxYTranspose();

    protected abstract void generateWaystoneStructure(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn);

    @Override
    protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
        FMLControlledNamespacedRegistry<VillagerRegistry.VillagerProfession> registry = (FMLControlledNamespacedRegistry<VillagerRegistry.VillagerProfession>) VillagerRegistry.instance().getRegistry();
        if (villagersSpawnedIn <= 0) {
            return registry.getId(WaystonesWorldInteraction.villagerWaystoner);
        } else {
            return currentVillagerProfession;
        }
    }
}
