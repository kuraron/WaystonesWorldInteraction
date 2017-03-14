package de.kuratan.mc.mods.waystonesworldinteraction;

import de.kuratan.mc.mods.waystonesworldinteraction.item.*;
import de.kuratan.mc.mods.waystonesworldinteraction.network.NetworkHandler;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystoneIntegration;
import de.kuratan.mc.mods.waystonesworldinteraction.world.stronghold.StrongholdGenerator;
import de.kuratan.mc.mods.waystonesworldinteraction.world.village.VillageCreationWaystone;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
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
import net.minecraftforge.oredict.RecipeSorter;
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
    public static final String WAYSTONES_MOD_ID = "waystones";

    @Mod.Instance
    public static WaystonesWorldInteraction instance;

    @SidedProxy(serverSide = "de.kuratan.mc.mods.waystonesworldinteraction", clientSide = "de.kuratan.mc.mods.waystonesworldinteraction.client.ClientProxy")
    public static CommonProxy proxy;

    @GameRegistry.ObjectHolder(WAYSTONES_MOD_ID+":return_scroll")
    public static Item itemReturnScroll = null;

    @GameRegistry.ObjectHolder(WAYSTONES_MOD_ID+":warp_scroll")
    public static Item itemWarpScroll = null;

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
                    new EntityVillager.ListItemForEmeralds(itemReturnScroll, new EntityVillager.PriceInfo(2, 4)),
                    new EntityVillager.ListItemForEmeralds(itemBoundScroll, new EntityVillager.PriceInfo(2, 4))
            );
            villagerWaystonerCareer.addTrade(2,
                    new EntityVillager.ListItemForEmeralds(new ItemStack(itemWarpStoneShard, 2), new EntityVillager.PriceInfo(4, 8)),
                    new EntityVillager.ListItemForEmeralds(itemWarpScroll, new EntityVillager.PriceInfo(6, 8)),
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
        CreativeTabs waystonesTab = Arrays.stream(CreativeTabs.CREATIVE_TAB_ARRAY).filter(tab -> tab.getTabLabel().equals(WAYSTONES_MOD_ID)).findFirst().orElse(null);
        if (waystonesTab != null) {
            itemBoundScroll.setCreativeTab(waystonesTab);
            itemWarpStoneCore.setCreativeTab(waystonesTab);
            itemWarpStoneShard.setCreativeTab(waystonesTab);
        } else {
            logger.warn("Could not find Waystones creative tab.");
        }

        Item itemWarpStone = Item.getByNameOrId(WAYSTONES_MOD_ID+":warp_stone");

        if (config.removeOriginalWarpstoneRecepie) {
            CraftingManager.getInstance().getRecipeList().removeIf(stack -> stack.getRecipeOutput().getItem() == itemWarpStone);
        }

        GameRegistry.addRecipe(new ItemStack(itemBoundScroll), "E  ", " P ", "  P", 'E', Items.ENDER_PEARL, 'P', Items.PAPER);
        GameRegistry.addRecipe(new ItemStack(itemWarpStoneCore, 1, 1), " E ", "ECE", " E ", 'E', Items.ENDER_PEARL, 'C', new ItemStack(itemWarpStoneCore, 1, 0));
        GameRegistry.addRecipe(new ItemStack(itemWarpStone), "SSS", "SCS", "SSS", 'S', itemWarpStoneShard, 'C', new ItemStack(itemWarpStoneCore, 1, 16));
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(Items.ENDER_PEARL));
        list.add(new ItemStack(itemWarpStoneCore));
        RecipeSorter.register("ChargeCoreRecipe", ChargeCoreRecipe.class, RecipeSorter.Category.SHAPELESS, "");
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
            ((Set<IWorldGenerator>) fieldWorldGenerators.get(null)).removeIf(gen -> WaystoneIntegration.isWaystonesWorldGen(gen));
            fieldWorldGenerators.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field fieldWorldGeneratorIndex = GameRegistry.class.getDeclaredField("worldGeneratorIndex");
            fieldWorldGeneratorIndex.setAccessible(true);
            Map<IWorldGenerator, Integer> worldGeneratorIndex = ((Map<IWorldGenerator, Integer>) fieldWorldGeneratorIndex.get(null));
            for (Map.Entry<IWorldGenerator, Integer> entry: worldGeneratorIndex.entrySet()) {
                if (WaystoneIntegration.isWaystonesWorldGen(entry.getKey())) {
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
