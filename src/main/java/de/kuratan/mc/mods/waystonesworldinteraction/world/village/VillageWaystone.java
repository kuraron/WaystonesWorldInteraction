package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.worldgen.NameGenerator;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

import static de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction.logger;

enum VillageWaystoneTypes {
    SIMPLE(0, 0, 0, 5, 4, 5, 2, 1, 2),
    ORNATE(0, 0, 0, 7, 4, 7, 3, 2, 3),
    ENCLOSED(0, 0, 0, 9, 9, 13, 4, 3, 9);

    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;
    public final int waystoneX;
    public final int waystoneY;
    public final int waystoneZ;
    VillageWaystoneTypes(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int waystoneX, int waystoneY, int waystoneZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.waystoneX = waystoneX;
        this.waystoneY = waystoneY;
        this.waystoneZ = waystoneZ;
    }
}

public class VillageWaystone extends StructureVillagePieces.House1 {

    private VillageWaystoneTypes type;

    public VillageWaystone() {}

    public VillageWaystone(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing, VillageWaystoneTypes type) {
        super(startPiece, componentType, random, boundingBox, facing);
        this.type = type;
    }

    public static VillageWaystone buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) {
        VillageWaystoneTypes type = VillageWaystoneTypes.values()[random.nextInt(VillageWaystoneTypes.values().length)];
        StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX, structureMinY, structureMinZ, type.minX, type.minY, type.minZ, type.maxX, type.maxY, type.maxZ, facing);
        if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(pieces, bbox) != null) {
            return null;
        }
        return new VillageWaystone(startPiece, componentType, random, bbox, facing, type);
    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + type.maxY - 1, 0);
        }
        if (worldIn.provider.getDimension() != 0) {
            return false;
        }

        switch (type) {
            case SIMPLE:
                generate_SIMPLE(worldIn, randomIn, structureBoundingBoxIn);
                break;
            case ORNATE:
                generate_ORNATE(worldIn, randomIn, structureBoundingBoxIn);
                break;
            case ENCLOSED:
                generate_ENCLOSED(worldIn, randomIn, structureBoundingBoxIn);
        }

        BlockPos blockpos = new BlockPos(this.getXWithOffset(type.waystoneX, type.waystoneZ), this.getYWithOffset(type.waystoneY), this.getZWithOffset(type.waystoneX, type.waystoneZ));
        EnumFacing facing = EnumFacing.values()[2 + randomIn.nextInt(4)];
        this.setBlockState(worldIn, Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, true).withProperty(BlockWaystone.FACING, facing), type.waystoneX, type.waystoneY, type.waystoneZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, false).withProperty(BlockWaystone.FACING, facing), type.waystoneX, type.waystoneY+1, type.waystoneZ, structureBoundingBoxIn);
        TileEntity tileEntity = worldIn.getTileEntity(blockpos);
        if (tileEntity instanceof TileWaystone) {
            String waystoneName = NameGenerator.getName(worldIn.getBiome(blockpos), randomIn);
            ((TileWaystone) tileEntity).setWaystoneName(waystoneName);
            logger.warn("Created Waystone '{}' @{}", waystoneName, blockpos);
        }

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

    protected void generate_SIMPLE(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        IBlockState base = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
        IBlockState stairN = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
        IBlockState stairS = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
        IBlockState stairE = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
        IBlockState stairW = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
        IBlockState fence = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());

        for (int l = type.minX; l < type.maxX; ++l)
        {
            for (int k = type.minZ; k < type.maxZ; ++k)
            {
                this.setBlockState(worldIn, base, l, 0, k, structureBoundingBoxIn);
                this.clearCurrentPositionBlocksUpwards(worldIn, k, 1, l, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, base, k, -1, l, structureBoundingBoxIn);
            }
        }

        // Torches
        this.setBlockState(worldIn, fence, type.minX, 1, type.minZ, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.minX, 2, type.minZ, structureBoundingBoxIn);

        this.setBlockState(worldIn, fence, type.minX, 1, type.maxZ-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.minX, 2, type.maxZ-1, structureBoundingBoxIn);

        this.setBlockState(worldIn, fence, type.maxX-1, 1, type.minZ, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.maxX-1, 2, type.minZ, structureBoundingBoxIn);

        this.setBlockState(worldIn, fence, type.maxX-1, 1, type.maxZ-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.maxX-1, 2, type.maxZ-1, structureBoundingBoxIn);

        // Stairs
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 3, 0, 0, stairN, stairN, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 4, 3, 0, 4, stairS, stairS, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 1, 0, 0, 3, stairE, stairE, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 4, 0, 3, stairW, stairW, false);
    }

    protected void generate_ORNATE(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        IBlockState base = Blocks.STONEBRICK.getDefaultState();
        IBlockState ornate = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
        IBlockState slab = Blocks.STONE_SLAB.getDefaultState();
        IBlockState stairN = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
        IBlockState stairS = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        IBlockState stairE = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        IBlockState stairW = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);

        for (int l = type.minX; l < type.maxX; ++l)
        {
            for (int k = type.minZ; k < type.maxZ; ++k)
            {
                this.setBlockState(worldIn, base, l, 0, k, structureBoundingBoxIn);
                this.clearCurrentPositionBlocksUpwards(worldIn, k, 1, l, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, base, k, -1, l, structureBoundingBoxIn);
            }
        }

        // Edges
        this.setBlockState(worldIn, base, type.minX, 1, type.minZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, type.minX, 2, type.minZ, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.minX, 3, type.minZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairW, type.minX+1, 1, type.minZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairS, type.minX, 1, type.minZ+1, structureBoundingBoxIn);

        this.setBlockState(worldIn, base, type.minX, 1, type.maxZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, type.minX, 2, type.maxZ-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.minX, 3, type.maxZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairW, type.minX+1, 1, type.maxZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairN, type.minX, 1, type.maxZ-2, structureBoundingBoxIn);

        this.setBlockState(worldIn, base, type.maxX-1, 1, type.minZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, type.maxX-1, 2, type.minZ, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.maxX-1, 3, type.minZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairE, type.maxX-2, 1, type.minZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairS, type.maxX-1, 1, type.minZ+1, structureBoundingBoxIn);

        this.setBlockState(worldIn, base, type.maxX-1, 1, type.maxZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, type.maxX-1, 2, type.maxZ-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, type.maxX-1, 3, type.maxZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairE, type.maxX-2, 1, type.maxZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairN, type.maxX-1, 1, type.maxZ-2, structureBoundingBoxIn);

        // Center
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.waystoneX-1, type.waystoneY-1, type.waystoneZ-1, type.waystoneX+1, type.waystoneY-1, type.waystoneZ+1, slab, slab, false);
        this.setBlockState(worldIn, ornate, type.waystoneX, type.waystoneY-1, type.waystoneZ, structureBoundingBoxIn);
    }

    protected void generate_ENCLOSED(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        IBlockState base = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
        IBlockState log = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
        IBlockState stairN = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
        IBlockState stairE = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
        IBlockState stairW = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
        IBlockState podestStairN = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
        IBlockState podestStairS = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        IBlockState podestStairE = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        IBlockState podestStairW = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        IBlockState planks = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
        IBlockState glass = this.getBiomeSpecificBlockState(Blocks.GLASS_PANE.getDefaultState());

        for (int l = type.minX; l < type.maxX; ++l)
        {
            for (int k = type.minZ; k < type.maxZ; ++k)
            {
                this.setBlockState(worldIn, base, l, 0, k, structureBoundingBoxIn);
                this.clearCurrentPositionBlocksUpwards(worldIn, k, 1, l, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, base, k, -1, l, structureBoundingBoxIn);
            }
        }

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, type.maxX-2, 1, type.maxZ-2, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, type.maxX-3, 1, type.maxZ-3, planks, planks, false);

        // Walls
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 1, 2, type.maxZ-2, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 1, 4, type.maxZ-2, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 1, 5, type.maxZ-2, planks, planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.maxX-2, 2, 1, type.maxX-2, 2, type.maxZ-2, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.maxX-2, 3, 1, type.maxX-2, 4, type.maxZ-2, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.maxX-2, 5, 1, type.maxX-2, 5, type.maxZ-2, planks, planks, false);

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, type.maxX-2, 2, 1, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, type.maxX-2, 4, 1, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, type.maxX-2, 5, 1, planks, planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, type.maxZ-2, type.maxX-2, 2, type.maxZ-2, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, type.maxZ-2, type.maxX-2, 4, type.maxZ-2, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, type.maxZ-2, type.maxX-2, 5, type.maxZ-2, planks, planks, false);

        this.setBlockState(worldIn, planks, 2, 6, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, 2, 6, type.maxZ-2, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, type.maxX-3, 6, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, type.maxX-3, 6, type.maxZ-2, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 6, 1, 4, 7, 1, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 6, 11, 4, 7, 11, glass, glass, false);

        for (int k = 1; k < type.maxZ; k+=2) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1 ,k, 1, 5, k, log, log, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.maxX-2, 1 ,k, type.maxX-2, 5, k, log, log, false);
        }
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1 ,1, 3, 7, 1, log, log, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.maxX-4, 1 ,1, type.maxX-4, 7, 1, log, log, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1 ,type.maxZ-2, 3, 7, type.maxZ-2, log, log, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, type.maxX-4, 1 ,type.maxZ-2, type.maxX-4, 7, type.maxZ-2, log, log, false);

        // Roof
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 0, 5, 12, stairE, stairE, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 6, 0, 1, 6, 12, stairE, stairE, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 7, 0, 2, 7, 12, stairE, stairE, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 8, 0, 3, 8, 12, stairE, stairE, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 8, 0, 4, 8, 12, planks, planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 8, 0, 5, 8, 12, stairW, stairW, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 7, 0, 6, 7, 12, stairW, stairW, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 6, 0, 7, 6, 12, stairW, stairW, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 0, 8, 5, 12, stairW, stairW, false);

        // Podest
        this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), type.waystoneX, type.waystoneY-1, type.waystoneZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairN, type.waystoneX, type.waystoneY-1, type.waystoneZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairN, type.waystoneX+1, type.waystoneY-1, type.waystoneZ-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairW, type.waystoneX+1, type.waystoneY-1, type.waystoneZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairW, type.waystoneX+1, type.waystoneY-1, type.waystoneZ+1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairS, type.waystoneX, type.waystoneY-1, type.waystoneZ+1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairS, type.waystoneX-1, type.waystoneY-1, type.waystoneZ+1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairE, type.waystoneX-1, type.waystoneY-1, type.waystoneZ, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairE, type.waystoneX-1, type.waystoneY-1, type.waystoneZ-1, structureBoundingBoxIn);

        // Torches
        this.placeTorch(worldIn, EnumFacing.EAST, 2, 5, 4, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.EAST, 2, 5, 8, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.NORTH, 4, 5, 2, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.SOUTH, 4, 5, 10, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.WEST, 6, 5, 4, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.WEST, 6, 5, 8, structureBoundingBoxIn);

        // Door
        this.setBlockState(worldIn, stairN, 4, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, 4, 4, 1, structureBoundingBoxIn);
        this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 4, 2, 1, EnumFacing.NORTH);
        if (WaystonesWorldInteraction.instance.getConfig().spawnWaystonesVillagers) {
            this.spawnVillagers(worldIn, structureBoundingBoxIn, 3, 2, 3, 1);
        }
    }
}
