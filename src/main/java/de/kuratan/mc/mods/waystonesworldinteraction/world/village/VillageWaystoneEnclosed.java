package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;

public class VillageWaystoneEnclosed extends VillageWaystone {

    public VillageWaystoneEnclosed() {}

    public VillageWaystoneEnclosed(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(startPiece, componentType, random, boundingBox, facing);
    }

    public static VillageWaystone buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 9, 12, facing);
        if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(pieces, bbox) != null) {
            return null;
        }
        return new VillageWaystoneEnclosed(startPiece, p5, random, bbox, facing);
    }

    @Override
    protected int boundingBoxYTranspose() {
        return 8;
    }

    @Override
    protected void generateWaystoneStructure(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
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

        for (int l = 0; l < 9; ++l)
        {
            for (int k = 0; k < 13; ++k)
            {
                this.setBlockState(worldIn, base, l, 0, k, structureBoundingBoxIn);
                this.clearCurrentPositionBlocksUpwards(worldIn, l, 1, k, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, base, l, -1, k, structureBoundingBoxIn);
            }
        }

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 1, 11, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 6, 1, 10, planks, planks, false);

        // Walls
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 1, 2, 11, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 1, 4, 11, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 1, 5, 11, planks, planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 1, 7, 2, 11, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 1, 7, 4, 11, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 5, 1, 7, 5, 11, planks, planks, false);

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 7, 2, 1, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 7, 4, 1, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 7, 5, 1, planks, planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 11, 7, 2, 11, base, base, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 11, 7, 4, 11, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 11, 7, 5, 11, planks, planks, false);

        this.setBlockState(worldIn, planks, 2, 6, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, 2, 6, 11, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, 6, 6, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, 6, 6, 11, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 6, 1, 4, 7, 1, glass, glass, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 6, 11, 4, 7, 11, glass, glass, false);

        for (int k = 1; k < 12; k+=2) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1 ,k, 1, 5, k, log, log, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1 ,k, 7, 5, k, log, log, false);
        }
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1 ,1, 3, 7, 1, log, log, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1 ,1, 5, 7, 1, log, log, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1 ,11, 3, 7, 11, log, log, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1 ,11, 5, 7, 11, log, log, false);

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
        this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 4, 3-1, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairN, 4, 3-1, 8-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairN, 4+1, 3-1, 8-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairW, 4+1, 3-1, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairW, 4+1, 3-1, 8+1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairS, 4, 3-1, 8+1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairS, 4-1, 3-1, 8+1, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairE, 4-1, 3-1, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, podestStairE, 4-1, 3-1, 8-1, structureBoundingBoxIn);

        // Door
        this.setBlockState(worldIn, stairN, 4, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, planks, 4, 4, 1, structureBoundingBoxIn);
        this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 4, 2, 1, EnumFacing.NORTH);
        if (WaystonesWorldInteraction.instance.getConfig().spawnWaystonesVillagers) {
            this.spawnVillagers(worldIn, structureBoundingBoxIn, 3, 2, 3, 1);
        }

        // Torches
        this.placeTorch(worldIn, EnumFacing.EAST, 2, 5, 4, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.EAST, 2, 5, 8, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.NORTH, 4, 5, 2, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.SOUTH, 4, 4, 0, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.SOUTH, 4, 5, 10, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.WEST, 6, 5, 4, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.WEST, 6, 5, 8, structureBoundingBoxIn);

        BlockPos blockpos = new BlockPos(this.getXWithOffset(4, 8), this.getYWithOffset(3), this.getZWithOffset(4, 8));
        WaystonesIntegration.generateWaystoneInWorld(worldIn, randomIn, blockpos, NameGenerator.WaystoneLocation.VILLAGE);
    }
}
