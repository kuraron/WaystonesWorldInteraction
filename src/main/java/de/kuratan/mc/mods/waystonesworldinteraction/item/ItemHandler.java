package de.kuratan.mc.mods.waystonesworldinteraction.item;

import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystoneData;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystoneIntegration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemHandler {
    @SubscribeEvent
    public void interact(RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack.getItem().equals(WaystonesWorldInteraction.itemBoundScroll)) {
                if (itemStack.getSubCompound("boundTo") == null ||
                        WaystonesWorldInteraction.instance.getConfig().allowBoundScrollRebind && event.getEntityPlayer().isSneaking()) {
                    // Try position
                    TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
                    // Try below
                    if (tileEntity == null) {
                        tileEntity = event.getWorld().getTileEntity(event.getPos().add(0, -1, 0));
                    }
                    // Check for Waystone
                    WaystoneData waystoneData = WaystoneIntegration.getWaystoneDataFromTileEntity(tileEntity);
                    if (waystoneData != null) {
                        if (!itemStack.hasTagCompound()) {
                            itemStack.setTagCompound(new NBTTagCompound());
                        }
                        NBTTagCompound container = new NBTTagCompound();
                        container.setString("name", waystoneData.name);
                        container.setInteger("dimensionId", event.getWorld().provider.getDimension());
                        container.setLong("pos", waystoneData.pos.toLong());
                        itemStack.setTagInfo("boundTo", container);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
