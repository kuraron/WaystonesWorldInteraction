package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

import java.util.List;

public class ChargeCoreRecipe extends ShapelessRecipes {
    public ChargeCoreRecipe(ItemStack output, List<ItemStack> inputList) {
        super(output, inputList);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack core = null;
        boolean pearl = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) == ItemStack.EMPTY || inv.getStackInSlot(i).getItem() == Items.AIR) {
                continue;
            }
            if (inv.getStackInSlot(i).getItem() == WaystonesWorldInteraction.itemWarpStoneCore) {
                if (core != null) {
                    return false;
                }
                core = inv.getStackInSlot(i);
            }
            else if (inv.getStackInSlot(i).getItem() == Items.ENDER_PEARL && !pearl) {
                pearl = true;
            }
            else {
                return false;
            }
        }
        if (core != null && pearl && core.getItemDamage() < core.getMaxDamage()) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack core = null;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).getItem() == WaystonesWorldInteraction.itemWarpStoneCore) {
                if (core != null) {
                }
                core = inv.getStackInSlot(i);
            }
        }

        return new ItemStack(WaystonesWorldInteraction.itemWarpStoneCore, 1, core.getItemDamage()+1);
    }
}
