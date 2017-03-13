package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemHandler {
    @SubscribeEvent
    public void interact(RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack.getItem().equals(WaystonesWorldInteraction.itemBoundScroll)) {
                if (itemStack.getSubCompound("boundTo") == null || event.getEntityPlayer().isSneaking()) {
                    // Try position
                    TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
                    // Try below
                    if (tileEntity == null) {
                        tileEntity = event.getWorld().getTileEntity(event.getPos().add(0, -1, 0));
                    }
                    // Check for Waystone
                    if (tileEntity instanceof TileWaystone) {
                        if (!itemStack.hasTagCompound()) {
                            itemStack.setTagCompound(new NBTTagCompound());
                        }
                        TileWaystone tileWaystone = (TileWaystone) tileEntity;
                        NBTTagCompound container = new NBTTagCompound();
                        BlockPos pos = tileWaystone.getPos();
                        container.setString("name", tileWaystone.getWaystoneName());
                        container.setInteger("dimensionId", event.getWorld().provider.getDimension());
                        container.setLong("pos", pos.toLong());
                        itemStack.setTagInfo("boundTo", container);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
