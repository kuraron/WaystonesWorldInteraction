package de.kuratan.mc.mods.waystonesworldinteraction.world;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import javax.annotation.Nullable;
import java.util.Random;

public class WorldGenerator implements IWorldGenerator {

    @Nullable
    private WorldGenMinable warpStoneShardGenerator;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        IBlockState warpStoneShardBlockState = WaystonesWorldInteraction.blockWarpStoneShardOre.getDefaultState();
        if (warpStoneShardGenerator == null) {
            warpStoneShardGenerator = new WorldGenMinable(warpStoneShardBlockState, 3);
        }

        int x = chunkX << 4;
        int z = chunkZ << 4;

        if (WaystonesWorldInteraction.instance.getConfig().enableWaystoneShardWorldGen) {
            for(int i = 0; i < 10; i++) {
                int randPosX = x + random.nextInt(16);
                int randPosY = 24 + random.nextInt(24);
                int randPosZ = z + random.nextInt(16);
                warpStoneShardGenerator.generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
            }
        }
    }
}
