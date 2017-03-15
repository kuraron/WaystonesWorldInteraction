package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystoneData;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
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
        setHasSubtypes(true);
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
        NBTTagCompound lastEntry = getBoundToTag(itemStack);
        if (lastEntry != null) {
            return 32;
        }
        return 0;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        NBTTagCompound lastEntry = getBoundToTag(itemStack);
        if (lastEntry != null) {
            return EnumAction.BOW;
        }
        return EnumAction.NONE;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            WaystoneEntry lastEntry = tagToEntry(getBoundToTag(itemStack));
            if (lastEntry != null) {
                if (itemStack.getMetadata() > 0 && !WaystoneManager.checkAndUpdateWaystone((EntityPlayer) entity, lastEntry)) {
                    WaystoneManager.addPlayerWaystone((EntityPlayer) entity, lastEntry);
                }
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
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack itemStack = player.getHeldItem(hand);
            if (itemStack.getItem().equals(WaystonesWorldInteraction.itemBoundScroll)) {
                if (itemStack.getSubCompound("boundTo") == null ||
                        itemStack.getMetadata() == 0 && WaystonesWorldInteraction.instance.getConfig().allowBoundScrollRebind && player.isSneaking()) {
                    if (itemStack.getMetadata() > 0) {
                        bindToRandomGeneratedWaystone(player, world, itemStack);
                        return EnumActionResult.SUCCESS;
                    }
                    // Try position
                    TileEntity tileEntity = world.getTileEntity(pos);
                    // Try below
                    if (tileEntity == null) {
                        tileEntity = world.getTileEntity(pos.add(0, -1, 0));
                    }
                    // Check for Waystone
                    if (tileEntity != null) {
                        WaystoneData waystoneData = WaystonesIntegration.getWaystoneDataFromTileEntity(tileEntity);
                        if (waystoneData != null) {
                            if (!itemStack.hasTagCompound()) {
                                itemStack.setTagCompound(new NBTTagCompound());
                            }
                            NBTTagCompound container = new NBTTagCompound();
                            container.setString("name", waystoneData.getName());
                            container.setInteger("dimensionId", waystoneData.getDimension());
                            container.setLong("pos", waystoneData.getPos().toLong());
                            itemStack.setTagInfo("boundTo", container);
                            WaystonesWorldInteraction.proxy.playSound(SoundEvents.BLOCK_ANVIL_PLACE, new BlockPos(player.posX, player.posY, player.posZ), 2f);
                            return EnumActionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return EnumActionResult.PASS;
    }

    protected boolean bindToRandomGeneratedWaystone(EntityPlayer player, World world, ItemStack itemStack) {
        WaystoneData waystoneData = WaystonesIntegration.get(world).getRandomWaystone();
        if (waystoneData != null) {
            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound container = new NBTTagCompound();
            container.setString("name", waystoneData.getName());
            container.setInteger("dimensionId", waystoneData.getDimension());
            container.setLong("pos", waystoneData.getPos().toLong());
            itemStack.setTagInfo("boundTo", container);
            return true;
        }
        return false;
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
            if (itemStack.getMetadata() > 0 && bindToRandomGeneratedWaystone(player, world, itemStack)) {
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
            player.sendStatusMessage(new TextComponentTranslation("tooltip." + WaystonesWorldInteraction.MOD_ID + ":scrollNotBound"), true);
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, 0));
        subItems.add(new ItemStack(this, 1, 1));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getMetadata() > 0) {
            return super.getUnlocalizedName(stack) + "_old";
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return getBoundToTag(stack) != null || stack.getMetadata() > 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean debug) {
        NBTTagCompound lastEntry = getBoundToTag(itemStack);
        if (itemStack.getMetadata() == 0) {
            if (lastEntry != null) {
                list.add(TextFormatting.GRAY + I18n.format("tooltip." + WAYSTONES_MOD_ID + ":boundTo", TextFormatting.DARK_AQUA + lastEntry.getString("name")));
            } else {
                list.add(TextFormatting.GRAY + I18n.format("tooltip." + WAYSTONES_MOD_ID + ":boundTo", I18n.format("tooltip." + WAYSTONES_MOD_ID + ":none")));
            }
        } else {
            list.add(TextFormatting.GRAY + I18n.format("tooltip." + WAYSTONES_MOD_ID + ":boundTo", TextFormatting.OBFUSCATED + "Unknown"));
        }
    }
}
