package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemWarpStoneCore extends Item {

    public ItemWarpStoneCore() {
        setRegistryName(WaystonesWorldInteraction.MOD_ID, "warp_stone_core");
        setUnlocalizedName(getRegistryName().toString());
        setMaxStackSize(1);
        setMaxDamage(16);
        setHasSubtypes(true);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (itemstack.getItemDamage() == 0) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
        }

        if (!playerIn.capabilities.isCreativeMode)
        {
            itemstack.damageItem(1, playerIn);
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        playerIn.getCooldownTracker().setCooldown(this, 20);

        if (!worldIn.isRemote)
        {
            EntityEnderPearl entityenderpearl = new EntityEnderPearl(worldIn, playerIn);
            entityenderpearl.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(entityenderpearl);
        }

        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, 0));
        subItems.add(new ItemStack(this, 1, 16));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return stack.getItemDamage() == stack.getMaxDamage();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        switch (stack.getItemDamage()) {
            case 0:
                tooltip.add(TextFormatting.GRAY + I18n.format("tooltip."+WaystonesWorldInteraction.MOD_ID+":inert"));
                break;
            case 16:
                tooltip.add(TextFormatting.GOLD + I18n.format("tooltip."+WaystonesWorldInteraction.MOD_ID+":charged"));
                break;
            default:
                tooltip.add(TextFormatting.AQUA + I18n.format("tooltip."+WaystonesWorldInteraction.MOD_ID+":charge") + ": " + stack.getMetadata());

        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.getItemDamage() > 0 && stack.getItemDamage() < stack.getMaxDamage();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - super.getDurabilityForDisplay(stack);
    }
}
