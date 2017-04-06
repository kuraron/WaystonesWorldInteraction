package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

import static de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction.logger;
import static de.kuratan.mc.mods.waystonesworldinteraction.world.village.VillageCreationWaystone.getYLevelByTemplateName;

public class VillageWaystone extends StructureVillagePieces.Village {

    private VillageCreationWaystone.VillageTemplate template;

    public VillageWaystone() {}

    public VillageWaystone(VillageCreationWaystone.VillageTemplate template, StructureVillagePieces.Start start, int type) {
        super(start, type);
        this.template = template;
        this.setCoordBaseMode(template.getCoordBaseMode());
        this.boundingBox = template.getBoundingBox();
    }


    public static VillageWaystone buildComponent(StructureVillagePieces.Start startPiece, List pieces, int p5, VillageCreationWaystone.VillageTemplate template) {
        StructureBoundingBox bbox = template.getBoundingBox();
        if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(pieces, bbox) != null) {
            return null;
        }
        return new VillageWaystone(template, startPiece, p5);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(world, structureBoundingBox);

            if (this.averageGroundLvl < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + getYLevelByTemplateName(this.template.getName()), 0);
            logger.warn("TBB: {} CBB: {}", template.getBoundingBox(), this.boundingBox);
            this.template.setPositionAndBox(this.boundingBox);
        }
        if (world.provider.getDimension() != 0) {
            return false;
        }
        this.fillWithBlocks(world, structureBoundingBox, 0, -1, 0, structureBoundingBox.getXSize(), 1, structureBoundingBox.getZSize(), Blocks.GLOWSTONE.getDefaultState(), Blocks.GLOWSTONE.getDefaultState(), false);
        // Clear above
        /*for (int x = 0; x < this.template.getBoundingBox().getXSize(); x++) {
            for (int z = 0; z < this.template.getBoundingBox().getZSize(); z++) {
                this.clearCurrentPositionBlocksUpwards(world, x, 1, z, this.template.getBoundingBox());
            }
        }*/
        // Place template
        logger.warn("F: {} T: {} P: {} BB: {}", this.getCoordBaseMode(),template.getName(), template.getBoundingBox(), structureBoundingBox);
        logger.warn("BBX: {} BBY: {} BBZ: {}", this.getBoundingBox().getXSize(), getBoundingBox().getYSize(), getBoundingBox().getZSize());
        logger.warn("Rotation: {} Mirror: {}", this.template.getRotation(), this.template.getMirror());
        template.addComponentParts(world, random, this.template.getBoundingBox());
        // Make template biome specific
        /*for (int x = 0; x < this.template.getBoundingBox().getXSize(); x++) {
            for (int z = 0; z < this.template.getBoundingBox().getZSize(); z++) {
                this.replaceAirAndLiquidDownwards(world, this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState()), x, -1, z, this.template.getBoundingBox());
                for (int y = 0; y < this.template.getBoundingBox().getYSize(); y++) {
                    IBlockState blockState = this.getBlockStateFromPos(world, x, y, z, this.template.getBoundingBox()).withRotation(this.template.getRotation()).withMirror(this.template.getMirror());
                    IBlockState newBlockState = this.getBiomeSpecificBlockState(blockState);
                    for (IProperty property : blockState.getPropertyKeys()) {
                        newBlockState.withProperty(property, blockState.getValue(property));
                    }
                    this.setBlockState(world, blockState, x, y, z, this.template.getBoundingBox());
                }
            }
        }*/
        return true;
    }

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
