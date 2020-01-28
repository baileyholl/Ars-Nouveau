package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.items.SpellBook;
import com.hollingsworth.craftedmagic.network.Networking;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;

import static net.minecraft.world.biome.Biome.LOGGER;

@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "modtut";
    public static final String MODNAME = "Mod tutorials";
    public static final String MODVERSION = "0.0.1";

//    @SidedProxy(clientSide = "com.hollingsworth.craftedmagic.ClientProxy", serverSide = "com.hollingsworth.craftedmagic.ServerProxy")
//    public static CommonProxy proxy;

//    @Mod.Instance
//    public static ExampleMod instance;

    public static IProxy proxy = DistExecutor.runForDist(()-> () -> new ClientProxy(), () -> ()-> new ServerProxy());
    public static Logger logger;


    public static ItemGroup itemGroup = new ItemGroup("crafted_magic") {
        @Override
        public ItemStack createIcon() {
            return Items.COBBLESTONE.getDefaultInstance();
        }
    };

//    @Mod.EventHandler
//    public void preInit(FMLPreInitializationEvent event) {
//        logger = event.getModLog();
//        proxy.preInit(event);
//
//    }

    public ExampleMod(){
        // modLoading setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup (final FMLCommonSetupEvent event){
        //Pre-init code
        proxy.init();
        Networking.registerMessages();
    }

    public void clientSetup(final FMLClientSetupEvent event){
        System.out.println("Hello from setup");
        System.out.println("Debugger hello");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            itemRegistryEvent.getRegistry().register(new SpellBook());
        }
    }
//    @Mod.EventHandler
//    public void init(FMLInitializationEvent e) {
//        proxy.init(e);
//    }
//
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent e) {
//        proxy.postInit(e);
//    }
}
