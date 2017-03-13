package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.item.Item;

public class ItemWarpStoneShard extends Item {

    public ItemWarpStoneShard() {
        setRegistryName(WaystonesWorldInteraction.MOD_ID, "warp_stone_shard");
        setUnlocalizedName(getRegistryName().toString());
        setMaxStackSize(16);
    }
}
