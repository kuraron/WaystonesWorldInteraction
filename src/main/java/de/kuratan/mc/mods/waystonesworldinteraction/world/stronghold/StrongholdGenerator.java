package de.kuratan.mc.mods.waystonesworldinteraction.world.stronghold;

import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

public class StrongholdGenerator extends MapGenStronghold {

    static {
        MapGenStructureIO.registerStructure(StrongholdGenerator.Start.class, "WaystoneStronghold");
        MapGenStructureIO.registerStructureComponent(StrongholdGenerator.WaystoneCrossing.class, "SHStartNew");
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void modifyStrongholdGen(InitMapGenEvent event) {
        if (event.getType().equals(InitMapGenEvent.EventType.STRONGHOLD)) {
            event.setNewGen(new StrongholdGenerator());
        }
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        Start start;

        for (start = new Start(this.world, this.rand, chunkX, chunkZ); start.getComponents().isEmpty() || ((WaystoneCrossing) start.getComponents().get(0)).strongholdPortalRoom == null; start = new Start(this.world, this.rand, chunkX, chunkZ)) {
        }

        return start;
    }

    public static class Start extends StructureStart {

        public Start() {
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            StructureStrongholdPieces.prepareStructurePieces();
            WaystoneCrossing waystoneCrossing = new WaystoneCrossing(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.components.add(waystoneCrossing);
            waystoneCrossing.buildComponent(waystoneCrossing, this.components, random);
            List<StructureComponent> list = waystoneCrossing.pendingChildren;

            while (!list.isEmpty()) {
                int i = random.nextInt(list.size());
                StructureComponent structurecomponent = list.remove(i);
                structurecomponent.buildComponent(waystoneCrossing, this.components, random);
            }
            this.updateBoundingBox();
            this.markAvailableHeight(worldIn, random, 10);
        }
    }

    static class Stones extends StructureComponent.BlockSelector {
        private Stones() {
        }

        /**
         * picks Block Ids and Metadata (Silverfish)
         */
        public void selectBlocks(Random rand, int x, int y, int z, boolean p_75062_5_) {
            if (p_75062_5_) {
                float f = rand.nextFloat();

                if (f < 0.2F) {
                    this.blockstate = Blocks.MONSTER_EGG.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.CRACKED_STONEBRICK);
                } else if (f < 0.5F) {
                    this.blockstate = Blocks.MONSTER_EGG.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.MOSSY_STONEBRICK);
                } else if (f < 0.55F) {
                    this.blockstate = Blocks.MONSTER_EGG.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONEBRICK);
                } else {
                    this.blockstate = Blocks.STONEBRICK.getDefaultState();
                }
            } else {
                this.blockstate = Blocks.AIR.getDefaultState();
            }
        }
    }


    public static class WaystoneCrossing extends StructureStrongholdPieces.Stairs2 {
        private String waystoneName = "";
        private int x = 2;
        private int y = 2;
        private int z = 2;

        public WaystoneCrossing(int p_i2083_1_, Random p_i2083_2_, int p_i2083_3_, int p_i2083_4_) {
            super(0, p_i2083_2_, p_i2083_3_, p_i2083_4_);
            this.boundingBox = new StructureBoundingBox(p_i2083_3_, 64, p_i2083_4_, p_i2083_3_ + x * 2 + 1, 74, p_i2083_4_ + y * 2 + 1);
        }

        /**
         * (abstract) Helper method to write subclass data to NBT
         */
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setString("Waystone", this.waystoneName);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.waystoneName = tagCompound.getString("Waystone");
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }

            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, structureBoundingBoxIn.getXSize(), y + 3, structureBoundingBoxIn.getZSize(), true, randomIn, new Stones());
            this.fillWithAir(worldIn, structureBoundingBoxIn, 1, 1, 1, structureBoundingBoxIn.getXSize() - 1, y + 2, structureBoundingBoxIn.getZSize() - 1);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 0, 1, z - 1, 0, 3, z + 1);
            this.fillWithAir(worldIn, structureBoundingBoxIn, structureBoundingBoxIn.getXSize(), 1, z - 1, structureBoundingBoxIn.getXSize(), 3, z + 1);
            this.fillWithAir(worldIn, structureBoundingBoxIn, x - 1, 1, 0, x + 1, 3, 0);
            this.fillWithAir(worldIn, structureBoundingBoxIn, x - 1, 1, structureBoundingBoxIn.getZSize(), x + 1, 3, structureBoundingBoxIn.getZSize());
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, x - 1, y - 1, z - 1, x + 1, y - 1, z + 1, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);

            if (worldIn.provider.getDimension() == 0) {
                BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
                WaystonesIntegration.generateWaystoneInWorld(worldIn, randomIn, blockpos, NameGenerator.WaystoneLocation.STRONGHOLD);
            }

            return true;
        }
    }
}
