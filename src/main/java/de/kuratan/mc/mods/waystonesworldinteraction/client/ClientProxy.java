package de.kuratan.mc.mods.waystonesworldinteraction.client;

import de.kuratan.mc.mods.waystonesworldinteraction.CommonProxy;
import de.kuratan.mc.mods.waystonesworldinteraction.WaystonesWorldInteraction;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemBoundScroll.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneCore, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneCore.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneShard, 0, new ModelResourceLocation(WaystonesWorldInteraction.itemWarpStoneShard.getRegistryName(), "inventory"));
    }
}