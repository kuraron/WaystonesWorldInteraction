package de.kuratan.mc.mods.waystonesworldinteraction;

import de.kuratan.mc.mods.waystonesworldinteraction.block.BlockWarpStoneShardOre;
import de.kuratan.mc.mods.waystonesworldinteraction.config.WaystonesWorldInteractionConfig;
import de.kuratan.mc.mods.waystonesworldinteraction.item.ChargeCoreRecipe;
import de.kuratan.mc.mods.waystonesworldinteraction.item.ItemBoundScroll;
import de.kuratan.mc.mods.waystonesworldinteraction.item.ItemWarpStoneCore;
import de.kuratan.mc.mods.waystonesworldinteraction.item.ItemWarpStoneShard;
import de.kuratan.mc.mods.waystonesworldinteraction.network.NetworkHandler;
import de.kuratan.mc.mods.waystonesworldinteraction.util.WaystonesIntegration;
import de.kuratan.mc.mods.waystonesworldinteraction.world.WorldGenerator;
import de.kuratan.mc.mods.waystonesworldinteraction.world.scattered.ScatteredWaystonesGen;
import de.kuratan.mc.mods.waystonesworldinteraction.world.stronghold.StrongholdGenerator;
import de.kuratan.mc.mods.waystonesworldinteraction.world.village.VillageCreationWaystone;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @SidedProxy(serverSide = "de.kuratan.mc.mods.waystonesworldinteraction.CommonProxy", clientSide = "de.kuratan.mc.mods.waystonesworldinteraction.client.ClientProxy")
    public static CommonProxy proxy;

    @GameRegistry.ObjectHolder(WaystonesIntegration.WAYSTONES_MOD_ID+":return_scroll")
    public static Item itemReturnScroll = null;

    @GameRegistry.ObjectHolder(WaystonesIntegration.WAYSTONES_MOD_ID+":warp_scroll")
    public static Item itemWarpScroll = null;

    @GameRegistry.ObjectHolder(WaystonesIntegration.WAYSTONES_MOD_ID+":warp_stone")
    public static Item itemWarpStone = null;

    public static ItemBoundScroll itemBoundScroll;
    public static ItemWarpStoneShard itemWarpStoneShard;
    public static ItemWarpStoneCore itemWarpStoneCore;

    public static BlockWarpStoneShardOre blockWarpStoneShardOre;
    public static ItemBlock itemBlockWarpStoneShardOre;

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
        blockWarpStoneShardOre = new BlockWarpStoneShardOre();
        GameRegistry.register(blockWarpStoneShardOre);
        itemBlockWarpStoneShardOre = new ItemBlock(blockWarpStoneShardOre);
        itemBlockWarpStoneShardOre.setRegistryName(blockWarpStoneShardOre.getRegistryName());
        GameRegistry.register(itemBlockWarpStoneShardOre);

        NetworkHandler.init();

        configuration = new Configuration(event.getSuggestedConfigurationFile());
        config = new WaystonesWorldInteractionConfig();
        config.reloadLocal(configuration);
        if (configuration.hasChanged()) {
            configuration.save();
        }
        WaystonesIntegration.getLocalWaystonesConfiguration(event.getModConfigurationDirectory());

        GameRegistry.registerWorldGenerator(new WorldGenerator(), 0);

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (config.deactivateOriginalWorldGen) {
            logger.info("Deactivate Waystones WorldGen");
            deactivateOriginalWorldGen();
        }

        if (config.createVillageWaystones) {
            logger.info("Registering Village Waystones");
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationWaystone());
        }
        if (config.spawnWaystonesVillagers) {
            VillagerRegistry.instance().register(villagerWaystoner);
            villagerWaystonerCareer.addTrade(1,
                    new EntityVillager.EmeraldForItems(itemWarpStoneShard, new EntityVillager.PriceInfo(2, 6)),
                    new EntityVillager.ListItemForEmeralds(itemReturnScroll, new EntityVillager.PriceInfo(2, 4)),
                    new EntityVillager.ListItemForEmeralds(new ItemStack(itemBoundScroll, 1, 1), new EntityVillager.PriceInfo(3, 5))
            );
            villagerWaystonerCareer.addTrade(2,
                    new EntityVillager.ListItemForEmeralds(new ItemStack(itemWarpStoneShard, 2), new EntityVillager.PriceInfo(4, 8)),
                    new EntityVillager.ListItemForEmeralds(itemWarpScroll, new EntityVillager.PriceInfo(6, 8)),
                    new EntityVillager.ListItemForEmeralds(itemBoundScroll, new EntityVillager.PriceInfo(2, 4)),
                    new EntityVillager.ItemAndEmeraldToItem(Items.DIAMOND, new EntityVillager.PriceInfo(6, 8), itemWarpStoneCore, new EntityVillager.PriceInfo(1, 1))
            );
        }
        if (config.createStrongholdWaystones) {
            logger.info("Registering Stronghold Waystones");
            MinecraftForge.TERRAIN_GEN_BUS.register(new StrongholdGenerator());
        }
        if (config.enableWaystoneScatteredFeatures) {
            logger.info("Registering Scattered Waystones");
            ScatteredWaystonesGen gen = new ScatteredWaystonesGen();
            MinecraftForge.EVENT_BUS.register(gen);
            MinecraftForge.TERRAIN_GEN_BUS.register(gen);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (config.removeOriginalWarpstoneRecepie) {
            CraftingManager.getInstance().getRecipeList().removeIf(stack -> stack.getRecipeOutput().getItem() == itemWarpStone);
        }

        GameRegistry.addRecipe(new ItemStack(itemBoundScroll), "E  ", " P ", "  P", 'E', Items.ENDER_PEARL, 'P', Items.PAPER);
        GameRegistry.addRecipe(new ItemStack(itemWarpStone), "SSS", "SCS", "SSS", 'S', itemWarpStoneShard, 'C', new ItemStack(itemWarpStoneCore, 1, 16));
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(Items.ENDER_PEARL));
        list.add(new ItemStack(itemWarpStoneCore));
        RecipeSorter.register("ChargeCoreRecipe", ChargeCoreRecipe.class, RecipeSorter.Category.SHAPELESS, "");
        GameRegistry.addRecipe(new ChargeCoreRecipe(new ItemStack(itemWarpStoneCore), list));

        proxy.postInit(event);
    }

    public WaystonesWorldInteractionConfig getConfig() {
        return config;
    }

    public void setConfig(WaystonesWorldInteractionConfig config) {
        this.config = config;
    }

    protected void deactivateOriginalWorldGen() {
        try {
            Field fieldWorldGenerators = GameRegistry.class.getDeclaredField("worldGenerators");
            fieldWorldGenerators.setAccessible(true);
            ((Set<IWorldGenerator>) fieldWorldGenerators.get(null)).removeIf(gen -> WaystonesIntegration.isWaystonesWorldGen(gen));
            fieldWorldGenerators.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field fieldWorldGeneratorIndex = GameRegistry.class.getDeclaredField("worldGeneratorIndex");
            fieldWorldGeneratorIndex.setAccessible(true);
            ((Map<IWorldGenerator, Integer>) fieldWorldGeneratorIndex.get(null)).entrySet().removeIf(entry -> WaystonesIntegration.isWaystonesWorldGen(entry.getKey()));
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
