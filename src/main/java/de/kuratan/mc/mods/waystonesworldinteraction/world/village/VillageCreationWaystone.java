package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import com.google.common.collect.Maps;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class VillageCreationWaystone implements VillagerRegistry.IVillageCreationHandler {
    private static Map<Class<? extends VillageWaystone>, Integer> PIECES;

    static {
        MapGenStructureIO.registerStructureComponent(VillageWaystoneSimple.class, WaystonesWorldInteraction.MOD_ID +":VillageWaystoneSimple");
        MapGenStructureIO.registerStructureComponent(VillageWaystoneOrnate.class, WaystonesWorldInteraction.MOD_ID +":VillageWaystoneOrnate");
        MapGenStructureIO.registerStructureComponent(VillageWaystoneEnclosed.class, WaystonesWorldInteraction.MOD_ID +":VillageWaystoneEnclosed");

        PIECES = Maps.newHashMap();
        PIECES.put(VillageWaystoneSimple.class, 10);
        PIECES.put(VillageWaystoneOrnate.class, 7);
        PIECES.put(VillageWaystoneEnclosed.class, 5);
    }

    public Class<? extends VillageWaystone> getVillageWaystoneClass(Random random) {
        int totalWeight = PIECES.values().stream().mapToInt(Number::intValue).sum();
        int value = random.nextInt(totalWeight);
        int weight = 0;
        for (Map.Entry<Class<? extends VillageWaystone>, Integer> e: PIECES.entrySet()) {
            weight += e.getValue();
            if (value < weight) {
                return e.getKey();
            }
        }
        return null;
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
        Class<? extends VillageWaystone> clazz = getVillageWaystoneClass(random);
        if (clazz.equals(VillageWaystoneOrnate.class))
            return VillageWaystoneOrnate.buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, facing, p5);
        if (clazz.equals(VillageWaystoneEnclosed.class))
            return VillageWaystoneEnclosed.buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, facing, p5);
        return VillageWaystoneSimple.buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, facing, p5);
    }
}
