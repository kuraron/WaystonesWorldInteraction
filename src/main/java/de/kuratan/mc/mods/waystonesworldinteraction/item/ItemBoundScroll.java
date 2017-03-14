package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration.WAYSTONES_MOD_ID;

public class ItemBoundScroll extends Item {
    public ItemBoundScroll() {
        setRegistryName(WaystonesWorldInteraction.MOD_ID, "bound_scroll");
        setUnlocalizedName(getRegistryName().toString());
        setMaxStackSize(4);
    }

    protected NBTTagCompound getBoundToTag(ItemStack itemStack) {
        return itemStack.getSubCompound("boundTo");
    }

    protected WaystoneEntry tagToEntry(NBTTagCompound tag) {
        if (tag != null) {
            return new WaystoneEntry(tag.getString("name"), tag.getInteger("dimensionId"), BlockPos.fromLong(tag.getLong("pos")), false);
        }
        return null;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.BOW;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            WaystoneEntry lastEntry = tagToEntry(getBoundToTag(itemStack));
            if (lastEntry != null) {
                if (WaystoneManager.teleportToWaystone((EntityPlayer) entity, lastEntry)) {
                    if (!((EntityPlayer) entity).capabilities.isCreativeMode) {
                        itemStack.shrink(1);
                    }
                }
            }
        }
        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        NBTTagCompound boundToTag = getBoundToTag(itemStack);
        if (boundToTag != null) {
            if (!player.isHandActive() && world.isRemote) {
                WaystonesWorldInteraction.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
            }
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        } else {
            player.sendStatusMessage(new TextComponentTranslation(WAYSTONES_MOD_ID+":scrollNotBound"), true);
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return getBoundToTag(stack) != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean debug) {
        NBTTagCompound lastEntry = getBoundToTag(itemStack);
        if (lastEntry != null) {
            list.add(TextFormatting.GRAY + I18n.format("tooltip."+WAYSTONES_MOD_ID+":boundTo", TextFormatting.DARK_AQUA + lastEntry.getString("name")));
        } else {
            list.add(TextFormatting.GRAY + I18n.format("tooltip."+WAYSTONES_MOD_ID+":boundTo", I18n.format("tooltip."+WAYSTONES_MOD_ID+":none")));
        }
    }
}
