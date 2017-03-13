package de.kuratan.mc.mods.waystonesworldinteraction;

import de.kuratan.mc.mods.waystonesworldinteraction.item.*;
import de.kuratan.mc.mods.waystonesworldinteraction.network.NetworkHandler;
import de.kuratan.mc.mods.waystonesworldinteraction.world.stronghold.StrongholdGenerator;
import de.kuratan.mc.mods.waystonesworldinteraction.world.village.VillageCreationWaystone;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.worldgen.WaystoneWorldGen;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

@Mod(
        modid = WaystonesWorldInteraction.MOD_ID,
        name = "WaystonesWorldInteraction",
        acceptedMinecraftVersions = "[1.11]",
        dependencies = "required-after:waystones@[3.0.0,]")
public class WaystonesWorldInteraction {

    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "waystonesworldinteraction";

    @Mod.Instance
    public static WaystonesWorldInteraction instance;

    @Mod.Instance("waystones")
    public static Waystones waystones;

    @SidedProxy(serverSide = "de.kuratan.mc.mods.waystonesworldinteraction", clientSide = "de.kuratan.mc.mods.waystonesworldinteraction.client.ClientProxy")
    public static CommonProxy proxy;

    public static ItemBoundScroll itemBoundScroll;
    public static ItemWarpStoneShard itemWarpStoneShard;
    public static ItemWarpStoneCore itemWarpStoneCore;

    public static Configuration configuration;
    private WaystonesWorldInteractionConfig config;

    public static final VillagerRegistry.VillagerProfession villagerWaystoner = new VillagerRegistry.VillagerProfession(
            MOD_ID +":waystoner", MOD_ID +":textures/entity/villager/waystoner.png",  MOD_ID +":textures/entity/villager/zombie_waystoner.png"
    );
    public static final VillagerRegistry.VillagerCareer villagerWaystonerCareer = new VillagerRegistry.VillagerCareer(villagerWaystoner, "waystoner");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        itemBoundScroll = new ItemBoundScroll();
        GameRegistry.register(itemBoundScroll);
        itemWarpStoneCore = new ItemWarpStoneCore();
        GameRegistry.register(itemWarpStoneCore);
        itemWarpStoneShard = new ItemWarpStoneShard();
        GameRegistry.register(itemWarpStoneShard);

        NetworkHandler.init();

        configuration = new Configuration(event.getSuggestedConfigurationFile());
        config = new WaystonesWorldInteractionConfig();
        config.reloadLocal(configuration);
        if (configuration.hasChanged()) {
            configuration.save();
        }

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (config.deactivateOriginalWorldGen) {
            deactivateOriginalWorldGen();
        }

        if (config.createVillageWaystones) {
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationWaystone());
        }
        if (config.spawnWaystonesVillagers) {
            VillagerRegistry.instance().register(villagerWaystoner);
            villagerWaystonerCareer.addTrade(1,
                    new EntityVillager.EmeraldForItems(itemWarpStoneShard, new EntityVillager.PriceInfo(2, 6)),
                    new EntityVillager.ListItemForEmeralds(Waystones.itemReturnScroll, new EntityVillager.PriceInfo(2, 4)),
                    new EntityVillager.ListItemForEmeralds(itemBoundScroll, new EntityVillager.PriceInfo(2, 4))
            );
            villagerWaystonerCareer.addTrade(2,
                    new EntityVillager.ListItemForEmeralds(new ItemStack(itemWarpStoneShard, 2), new EntityVillager.PriceInfo(4, 8)),
                    new EntityVillager.ListItemForEmeralds(Waystones.itemWarpScroll, new EntityVillager.PriceInfo(6, 8)),
                    new EntityVillager.ItemAndEmeraldToItem(Items.DIAMOND, new EntityVillager.PriceInfo(6, 8), itemWarpStoneCore, new EntityVillager.PriceInfo(1, 1))
            );
        }
        if (config.createStrongholdWaystones) {
            MinecraftForge.TERRAIN_GEN_BUS.register(new StrongholdGenerator());
        }
        MinecraftForge.EVENT_BUS.register(new ItemHandler());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        itemBoundScroll.setCreativeTab(waystones.creativeTab);
        itemWarpStoneCore.setCreativeTab(waystones.creativeTab);
        itemWarpStoneShard.setCreativeTab(waystones.creativeTab);

        if (config.removeOriginalWarpstoneRecepie) {
            CraftingManager.getInstance().getRecipeList().removeIf(stack -> stack.getRecipeOutput().getItem() == waystones.itemWarpStone);
        }

        GameRegistry.addRecipe(new ItemStack(itemBoundScroll), "E  ", " P ", "  P", 'E', Items.ENDER_PEARL, 'P', Items.PAPER);
        GameRegistry.addRecipe(new ItemStack(itemWarpStoneCore, 1, 1), " E ", "ECE", " E ", 'E', Items.ENDER_PEARL, 'C', new ItemStack(itemWarpStoneCore, 1, 0));
        GameRegistry.addRecipe(new ItemStack(waystones.itemWarpStone), "SSS", "SCS", "SSS", 'S', itemWarpStoneShard, 'C', new ItemStack(itemWarpStoneCore, 1, 16));
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(Items.ENDER_PEARL));
        list.add(new ItemStack(itemWarpStoneCore));
        GameRegistry.addRecipe(new ChargeCoreRecipe(new ItemStack(itemWarpStoneCore), list));
    }

    public WaystonesWorldInteractionConfig getConfig() {
        return config;
    }

    public void setConfig(WaystonesWorldInteractionConfig config) {
        this.config = config;
    }

    protected void deactivateOriginalWorldGen() {
        logger.info("Deactivate Waystones WorldGen");
        try {
            Field fieldWorldGenerators = GameRegistry.class.getDeclaredField("worldGenerators");
            fieldWorldGenerators.setAccessible(true);
            Set<IWorldGenerator> worldGeneratorSet = (Set<IWorldGenerator>) fieldWorldGenerators.get(null);
            Set<IWorldGenerator> remove = new HashSet<IWorldGenerator>();
            for (IWorldGenerator worldGenerator : worldGeneratorSet) {
                if (worldGenerator instanceof WaystoneWorldGen) {
                    remove.add(worldGenerator);
                }
            }
            worldGeneratorSet.removeAll(remove);
            fieldWorldGenerators.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field fieldWorldGeneratorIndex = GameRegistry.class.getDeclaredField("worldGeneratorIndex");
            fieldWorldGeneratorIndex.setAccessible(true);
            Map<IWorldGenerator, Integer> worldGeneratorIndex = (Map<IWorldGenerator, Integer>) fieldWorldGeneratorIndex.get(null);
            for (Map.Entry<IWorldGenerator, Integer> entry: worldGeneratorIndex.entrySet()) {
                if (entry.getKey() instanceof WaystoneWorldGen) {
                    worldGeneratorIndex.remove(entry.getKey());
                }
            }
            fieldWorldGeneratorIndex.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field fieldSortedGeneratorList = GameRegistry.class.getDeclaredField("sortedGeneratorList");
            fieldSortedGeneratorList.setAccessible(true);
            fieldSortedGeneratorList.set(null, null);
            fieldSortedGeneratorList.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
