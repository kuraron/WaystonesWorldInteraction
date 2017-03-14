package de.kuratan.mc.mods.waystonesworldinteraction.client;

import de.kuratan.mc.mods.waystonesworldinteraction.CommonProxy;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll, 1, new ModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneCore, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneCore.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneShard, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneShard.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(WaystonesWorldInteraction.blockWarpStoneShardOre), 0, new ModelResourceLocation(WaystonesWorldInteraction.blockWarpStoneShardOre.getRegistryName(), "inventory"));
    }

    @Override
    public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(sound, SoundCategory.AMBIENT, WaystonesIntegration.getWaystoneConfig().soundVolume, pitch, pos));
    }
}
