package de.kuratan.mc.mods.waystonesworldinteraction.block;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockWarpStoneShardOre extends Block {

    public BlockWarpStoneShardOre()
    {
        super(Material.ROCK);
        setRegistryName(WaystonesWorldInteraction.MOD_ID, "warp_stone_shard_ore");
        setUnlocalizedName(getRegistryName().toString());
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return WaystonesWorldInteraction.itemWarpStoneShard;
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return this.quantityDropped(random) + random.nextInt(fortune + 1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 4 + random.nextInt(2);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        if (this.getItemDropped(state, RANDOM, fortune) != Item.getItemFromBlock(this))
        {
            return 1 + RANDOM.nextInt(5);
        }
        return 0;
    }

    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return new ItemStack(WaystonesWorldInteraction.blockWarpStoneShardOre);
    }
}