package de.kuratan.mc.mods.waystonesworldinteraction.world.village;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

public class VillageCreationWaystone implements VillagerRegistry.IVillageCreationHandler {
    static {
        MapGenStructureIO.registerStructureComponent(VillageWaystone.class, WaystonesWorldInteraction.MOD_ID +":VillageWaystone");
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
        return VillageWaystone.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
    }
}
