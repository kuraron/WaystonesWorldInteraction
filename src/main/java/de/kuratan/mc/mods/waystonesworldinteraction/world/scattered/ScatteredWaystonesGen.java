package de.kuratan.mc.mods.waystonesworldinteraction.world.scattered;

import com.google.common.collect.Maps;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import de.kuratan.mc.mods.waystonesworldinteraction.world.WaystoneTemplate;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class ScatteredWaystonesGen extends MapGenStructure {
    private static TemplateManager PIECES;
    private static Map<String, Integer> PIECE_WEIGHTS;
    private static Map<String, Integer> PIECE_OFFSET;

    static {
        PIECES = new TemplateManager("", DataFixesManager.createFixer());
        PIECE_WEIGHTS = Maps.newHashMap();
        PIECE_OFFSET = Maps.newHashMap();
        MapGenStructureIO.registerStructure(Start.class, WaystonesWorldInteraction.MOD_ID+":ScatteredTemplate");
        MapGenStructureIO.registerStructureComponent(Feature.class, WaystonesWorldInteraction.MOD_ID+":ScatteredTemplate");
        registerPiece(WaystonesWorldInteraction.MOD_ID + ":scattered/waystone_pillars", 3, -3);
    }

    public static boolean registerPiece(String name, int weight, int yOffset) {
        if (PIECE_WEIGHTS.containsKey(name)) {
            return false;
        }
        PIECE_WEIGHTS.put(name, weight);
        PIECE_OFFSET.put(name, yOffset);
        return true;
    }

    public static String getTemplateName(Random random) {
        int totalWeight = PIECE_WEIGHTS.values().stream().mapToInt(Number::intValue).sum();
        int value = random.nextInt(totalWeight);
        int weight = 0;
        for (Map.Entry<String, Integer> e : PIECE_WEIGHTS.entrySet()) {
            weight += e.getValue();
            if (value < weight) {
                return e.getKey();
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onChunkPopulatePre(PopulateChunkEvent.Pre event) {
        this.generateStructure(event.getWorld(), event.getRand(), new ChunkPos(event.getChunkX(), event.getChunkZ()));
    }

    @SubscribeEvent
    public void onChunkPopulatePost(PopulateChunkEvent.Post event) {
        this.generate(event.getWorld(), event.getChunkX(), event.getChunkZ(), null);
    }
    
    @Override
    public String getStructureName() {
        return "Waystone";
    }

    @Nullable
    @Override
    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos, boolean p_180706_3_) {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures, 8, 14357617, false, 100, p_180706_3_);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures;
        Random random = this.world.setRandomSeed(k, l, 14357617);
        k = k * WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures;
        l = l * WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures/2);
        l = l + random.nextInt(WaystonesWorldInteraction.instance.getConfig().maxDistanceBetweenScatteredFeatures/2);

        if (i == k && j == l)
        {
            Biome biome = this.world.getBiomeProvider().getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8));
            if (biome == null)
            {
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new ScatteredWaystonesGen.Start(this.world, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart {
        public Start()
        {
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ)
        {
            this(worldIn, random, chunkX, chunkZ, worldIn.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ, Biome biomeIn)
        {
            super(chunkX, chunkZ);
            this.components.add(new Feature(random, chunkX * 16, 64, chunkZ * 16, getTemplateName(random)));
            this.updateBoundingBox();
        }
    }

    public static class ScatteredTemplate extends WaystoneTemplate {

        private String name;

        public ScatteredTemplate(TemplateManager templateManager, String name, BlockPos position) {
            this.name = name;
            this.templatePosition = position;
            this.loadTemplate(templateManager);
        }

        protected void loadTemplate(TemplateManager templateManager) {
            Template template = templateManager.getTemplate(null, new ResourceLocation(this.name));
            PlacementSettings placementSettings = (new PlacementSettings()).setIgnoreEntities(true);
            this.setup(template, templatePosition, placementSettings);
        }

        protected void setPlacementSettings(PlacementSettings placementSettings) {
            this.setup(template, templatePosition, placementSettings);
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setString("Template", this.name);
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager templateManager) {
            super.readStructureFromNBT(tagCompound, templateManager);
            this.name = tagCompound.getString("Template");
            this.loadTemplate(templateManager);
        }

        @Override
        protected NameGenerator.WaystoneLocation getWaystoneLocation() {
            return NameGenerator.WaystoneLocation.SCATTERED;
        }

        protected BlockPos getTemplatePostion() {
            return this.templatePosition;
        }

        protected void setTemplatePostion(BlockPos pos) {
            this.templatePosition = pos;
        }
    }

    public static class Feature extends StructureComponent {
        /** The size of the bounding box for this feature in the X axis */
        protected int scatteredFeatureSizeX;
        /** The size of the bounding box for this feature in the Y axis */
        protected int scatteredFeatureSizeY;
        /** The size of the bounding box for this feature in the Z axis */
        protected int scatteredFeatureSizeZ;
        protected int horizontalPos = -1;
        protected ScatteredTemplate scatteredTemplate;

        public Feature()
        {
        }

        protected Feature(Random rand, int x, int y, int z, String templateName)
        {
            super(0);
            scatteredTemplate = new ScatteredTemplate(PIECES, templateName, new BlockPos(x, y, z));
            this.scatteredFeatureSizeX = scatteredTemplate.getBoundingBox().getXSize();
            this.scatteredFeatureSizeY = scatteredTemplate.getBoundingBox().getYSize();
            this.scatteredFeatureSizeZ = scatteredTemplate.getBoundingBox().getZSize();
            this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(rand));
            scatteredTemplate.setCoordBaseMode(this.getCoordBaseMode());

            if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z)
            {
                this.boundingBox = new StructureBoundingBox(x, y, z, x + scatteredFeatureSizeX - 1, y + scatteredFeatureSizeY - 1, z + scatteredFeatureSizeZ - 1);
            }
            else
            {
                this.boundingBox = new StructureBoundingBox(x, y, z, x + scatteredFeatureSizeZ - 1, y + scatteredFeatureSizeY - 1, z + scatteredFeatureSizeX - 1);
            }
        }

        /**
         * (abstract) Helper method to write subclass data to NBT
         */
        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setInteger("Width", this.scatteredFeatureSizeX);
            tagCompound.setInteger("Height", this.scatteredFeatureSizeY);
            tagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
            tagCompound.setInteger("HPos", this.horizontalPos);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
        {
            this.scatteredFeatureSizeX = tagCompound.getInteger("Width");
            this.scatteredFeatureSizeY = tagCompound.getInteger("Height");
            this.scatteredFeatureSizeZ = tagCompound.getInteger("Depth");
            this.horizontalPos = tagCompound.getInteger("HPos");
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (!this.offsetToAverageGroundLevel(world, structureBoundingBox, PIECE_OFFSET.get(scatteredTemplate.name)))
            {
                return false;
            }
            scatteredTemplate.setTemplatePostion(scatteredTemplate.getTemplatePostion().down(64).up(this.getBoundingBox().minY));
            PlacementSettings placementsettings = (new PlacementSettings()).setReplacedBlock(Blocks.STRUCTURE_VOID);
            scatteredTemplate.setPlacementSettings(placementsettings);
            return scatteredTemplate.addComponentParts(world, random, this.getBoundingBox());
        }

        /**
         * Calculates and offsets this structure boundingbox to average ground level
         */
        protected boolean offsetToAverageGroundLevel(World worldIn, StructureBoundingBox structurebb, int yOffset)
        {
            if (this.horizontalPos >= 0)
            {
                return true;
            }
            else
            {
                int i = 0;
                int j = 0;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k)
                {
                    for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l)
                    {
                        blockpos$mutableblockpos.setPos(l, 64, k);

                        if (structurebb.isVecInside(blockpos$mutableblockpos))
                        {
                            i += Math.max(worldIn.getTopSolidOrLiquidBlock(blockpos$mutableblockpos).getY(), worldIn.provider.getAverageGroundLevel());
                            ++j;
                        }
                    }
                }
                if (j == 0)
                {
                    return false;
                }
                else
                {
                    this.horizontalPos = i / j;
                    this.boundingBox.offset(0, this.horizontalPos - this.boundingBox.minY + yOffset, 0);
                    return true;
                }
            }
        }
    }
}
