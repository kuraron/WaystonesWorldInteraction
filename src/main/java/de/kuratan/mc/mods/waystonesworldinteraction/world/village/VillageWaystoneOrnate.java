package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
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

public class VillageWaystoneOrnate extends VillageWaystone {

    public VillageWaystoneOrnate() {}

    public VillageWaystoneOrnate(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(startPiece, componentType, random, boundingBox, facing);
    }

    public static VillageWaystone buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 7, 4, 7, facing);
        if (!canVillageGoDeeper(bbox) || StructureComponent.findIntersecting(pieces, bbox) != null) {
            return null;
        }
        return new VillageWaystoneOrnate(startPiece, p5, random, bbox, facing);
    }


    @Override
    protected int boundingBoxYTranspose() {
        return 3;
    }

    @Override
    protected void generateWaystoneStructure(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        IBlockState base = Blocks.STONEBRICK.getDefaultState();
        IBlockState ornate = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
        IBlockState slab = Blocks.STONE_SLAB.getDefaultState();
        IBlockState stairN = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
        IBlockState stairS = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        IBlockState stairE = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        IBlockState stairW = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);

        for (int l = 0; l < 7; ++l)
        {
            for (int k = 0; k < 7; ++k)
            {
                this.setBlockState(worldIn, base, l, 0, k, structureBoundingBoxIn);
                this.clearCurrentPositionBlocksUpwards(worldIn, k, 1, l, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, base, k, -1, l, structureBoundingBoxIn);
            }
        }

        // Edges
        this.setBlockState(worldIn, base, 0, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, 0, 2, 0, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 0, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairW, 0+1, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairS, 0, 1, 0+1, structureBoundingBoxIn);

        this.setBlockState(worldIn, base, 0, 1, 7-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, 0, 2, 7-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 0, 3, 7-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairW, 0+1, 1, 7-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairN, 0, 1, 7-2, structureBoundingBoxIn);

        this.setBlockState(worldIn, base, 7-1, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, 7-1, 2, 0, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 7-1, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairE, 7-2, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairS, 7-1, 1, 0+1, structureBoundingBoxIn);

        this.setBlockState(worldIn, base, 7-1, 1, 7-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, ornate, 7-1, 2, 7-1, structureBoundingBoxIn);
        this.placeTorch(worldIn, EnumFacing.UP, 7-1, 3, 7-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairE, 7-2, 1, 7-1, structureBoundingBoxIn);
        this.setBlockState(worldIn, stairN, 7-1, 1, 7-2, structureBoundingBoxIn);

        // Center
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3-1, 2-1, 3-1, 3+1, 2-1, 3+1, slab, slab, false);
        this.setBlockState(worldIn, ornate, 3, 2-1, 3, structureBoundingBoxIn);

        BlockPos blockpos = new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3));
        WaystonesIntegration.generateWaystoneInWorld(worldIn, randomIn, blockpos, NameGenerator.WaystoneLocation.VILLAGE);
    }
}
