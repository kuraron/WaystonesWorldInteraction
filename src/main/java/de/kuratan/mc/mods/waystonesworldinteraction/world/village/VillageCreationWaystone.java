package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import com.google.common.collect.Maps;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.world.NameGenerator;
import de.kuratan.mc.mods.waystonesworldinteraction.world.WaystoneTemplate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction.logger;

public class VillageCreationWaystone implements VillagerRegistry.IVillageCreationHandler {
    private static TemplateManager PIECES;
    private static Map<String, Integer> PIECE_WEIGHTS;
    private static Map<String, Integer> PIECE_Y_LEVEL;

    static {
        MapGenStructureIO.registerStructureComponent(VillageWaystone.class, WaystonesWorldInteraction.MOD_ID + ":VillageWaystone");
        PIECES = new TemplateManager("", DataFixesManager.createFixer());
        PIECE_WEIGHTS = Maps.newHashMap();
        PIECE_Y_LEVEL = Maps.newHashMap();
        //registerPiece(WaystonesWorldInteraction.MOD_ID+":village/waystone_stone_only", 10, 0);
        //registerPiece(WaystonesWorldInteraction.MOD_ID+":village/waystone_simple", 10, 2);
        //registerPiece(WaystonesWorldInteraction.MOD_ID+":village/waystone_ornate", 7, 2);
        registerPiece(WaystonesWorldInteraction.MOD_ID + ":village/waystone_building", 3, 7);
    }

    public static boolean registerPiece(String name, int weight, int y) {
        if (PIECE_WEIGHTS.containsKey(name)) {
            return false;
        }
        PIECE_WEIGHTS.put(name, weight);
        PIECE_Y_LEVEL.put(name, y);
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

    public static int getYLevelByTemplateName(String name) {
        return PIECE_Y_LEVEL.get(name);
    }

    @Override
    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
        return new StructureVillagePieces.PieceWeight(VillageWaystone.class, 100, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return VillageWaystone.class;
    }

    @Override
    public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        String templateName = getTemplateName(random);
        VillageTemplate template = new VillageTemplate(PIECES, templateName, new BlockPos(p1, p2, p3), Rotation.values()[facing.getHorizontalIndex()]);
        logger.warn("BP: {} Fac: {} {} {}", new BlockPos(p1, p2, p3), facing.getName(), facing.getName2(), Rotation.values()[facing.getHorizontalIndex()].name());
        return VillageWaystone.buildComponent(startPiece, pieces, p5, template);
    }

    public static class VillageTemplate extends WaystoneTemplate {

        private String name;
        private Rotation rotation;
        private Mirror mirror;
        private TemplateManager templateManager;

        public VillageTemplate(TemplateManager templateManager, String name, BlockPos position, Rotation rotation) {
            this.name = name;
            this.templatePosition = position;
            this.rotation = rotation;
            this.mirror = Mirror.NONE;
            this.templateManager = templateManager;
            this.loadTemplate(templateManager);
        }

        public String getName() {
            return name;
        }

        public Mirror getMirror() {
            return mirror;
        }

        public Rotation getRotation() {
            return rotation;
        }

        @Override
        protected NameGenerator.WaystoneLocation getWaystoneLocation() {
            return NameGenerator.WaystoneLocation.VILLAGE;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            return super.addComponentParts(world, random, structureBoundingBox);
        }

        protected void setPositionAndBox(StructureBoundingBox structureBoundingBox) {
            this.templatePosition = new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ);
            this.loadTemplate(templateManager, structureBoundingBox);
        }



        protected void loadTemplate(TemplateManager templateManager, StructureBoundingBox structureBoundingBox) {
            Template template = templateManager.getTemplate(null, new ResourceLocation(this.name));
            PlacementSettings placementSettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).setBoundingBox(structureBoundingBox);
            this.setup(template, templatePosition, placementSettings);
        }

        protected void loadTemplate(TemplateManager templateManager) {
            Template template = templateManager.getTemplate(null, new ResourceLocation(this.name));
            PlacementSettings placementSettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror);
            this.setup(template, templatePosition, placementSettings);
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setString("Template", this.name);
            tagCompound.setString("Rot", this.placeSettings.getRotation().name());
            tagCompound.setString("Mi", this.placeSettings.getMirror().name());
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager templateManager) {
            super.readStructureFromNBT(tagCompound, templateManager);
            this.name = tagCompound.getString("Template");
            this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
            this.mirror = Mirror.valueOf(tagCompound.getString("Mi"));
            this.loadTemplate(templateManager);
        }
    }
}
