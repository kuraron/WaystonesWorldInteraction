package de.kuratan.mc.mods.waystonesworldinteraction.client;

import de.kuratan.mc.mods.waystonesworldinteraction.CommonProxy;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Arrays;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll, 1, new ModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll.getRegistryName()+"_old", "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneCore, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneCore.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneShard, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneShard.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(WaystonesWorldInteraction.blockWarpStoneShardOre), 0, new ModelResourceLocation(WaystonesWorldInteraction.blockWarpStoneShardOre.getRegistryName(), "inventory"));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        CreativeTabs waystonesTab = Arrays.stream(CreativeTabs.CREATIVE_TAB_ARRAY).filter(tab -> tab.getTabLabel().equals(WaystonesIntegration.WAYSTONES_MOD_ID)).findFirst().orElse(null);
        if (waystonesTab != null) {
            WaystonesWorldInteraction.itemBoundScroll.setCreativeTab(waystonesTab);
            WaystonesWorldInteraction.itemWarpStoneCore.setCreativeTab(waystonesTab);
            WaystonesWorldInteraction.itemWarpStoneShard.setCreativeTab(waystonesTab);
            WaystonesWorldInteraction.blockWarpStoneShardOre.setCreativeTab(waystonesTab);
        } else {
            WaystonesWorldInteraction.logger.warn("Could not find Waystones creative tab.");
        }
    }

    @Override
    public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(sound, SoundCategory.AMBIENT, WaystonesIntegration.getWaystoneConfig().soundVolume, pitch, pos));
    }
}
