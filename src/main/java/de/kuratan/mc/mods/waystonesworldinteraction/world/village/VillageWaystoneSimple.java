package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

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

public class VillageWaystoneSimple extends VillageWaystone {

    public VillageWaystoneSimple() {}

    public VillageWaystoneSimple(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(startPiece, componentType, random, boundingBox, facing);
    }

    public static VillageWaystone buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 3, 5, facing);
        if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(pieces, bbox) != null) {
            return null;
        }
        return new VillageWaystoneSimple(startPiece, p5, random, bbox, facing);
    }

    @Override
    protected int boundingBoxYTranspose() {
        return 3;
    }

    @Override
    protected void generateWaystoneStructure(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        IBlockState base = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
        IBlockState stairN = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
        IBlockState stairS = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
        IBlockState stairE = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
        IBlockState stairW = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
        IBlockState fence = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());

        for (int l = 0; l < 5; ++l)
        {
            for (int k = 0; k < 5; ++k)
            {
                this.setBlockState(worldIn, base, l, 0, k, structureBoundingBoxIn);
                this.clearCurrentPositionBlocksUpwards(worldIn, k, 1, l, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, base, k, -1, l, structureBoundingBoxIn);
            }
        }

        // Torches
        this.setBlockState(worldIn, fence, 0, 1, 0, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 0, 2, 0, structureBoundingBoxIn);

        this.setBlockState(worldIn, fence, 0, 1, 5-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 0, 2, 5-1, structureBoundingBoxIn);

        this.setBlockState(worldIn, fence, 5-1, 1, 0, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 5-1, 2, 0, structureBoundingBoxIn);

        this.setBlockState(worldIn, fence, 5-1, 1, 5-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 5-1, 2, 5-1, structureBoundingBoxIn);

        // Stairs
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 3, 0, 0, stairN, stairN, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 4, 3, 0, 4, stairS, stairS, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 1, 0, 0, 3, stairE, stairE, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 4, 0, 3, stairW, stairW, false);

        BlockPos blockpos = new BlockPos(this.getXWithOffset(2, 2), this.getYWithOffset(1), this.getZWithOffset(2, 2));
        WaystonesIntegration.generateWaystoneInWorld(worldIn, randomIn, blockpos, NameGenerator.WaystoneLocation.VILLAGE);
    }
}
