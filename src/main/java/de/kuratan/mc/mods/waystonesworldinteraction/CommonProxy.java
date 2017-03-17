package de.kuratan.mc.mods.waystonesworldinteraction;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    public void postInit(FMLPostInitializationEvent event) {}

    public void playSound(SoundEvent soundEvent, BlockPos pos, float pitch) {}
}
