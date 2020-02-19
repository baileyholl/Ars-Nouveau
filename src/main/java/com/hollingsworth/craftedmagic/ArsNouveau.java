package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.block.ModBlocks;
import com.hollingsworth.craftedmagic.block.PhantomBlock;
import com.hollingsworth.craftedmagic.block.PhantomBlockTile;
import com.hollingsworth.craftedmagic.items.SpellBook;
import com.hollingsworth.craftedmagic.network.Networking;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
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

@Mod(ArsNouveau.MODID)
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsNouveau {
    public static final String MODID = "ars_nouveau";
    public static final String MODNAME = "Ars Nouveau";
    public static final String MODVERSION = "0.0.1";

    public static IProxy proxy = DistExecutor.runForDist(()-> () -> new ClientProxy(), () -> ()-> new ServerProxy());
    public static Logger logger;


    public static ItemGroup itemGroup = new ItemGroup("crafted_magic") {
        @Override
        public ItemStack createIcon() {
            return Items.COBBLESTONE.getDefaultInstance();
        }
    };

    public ArsNouveau(){
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
            blockRegistryEvent.getRegistry().register(new PhantomBlock());

        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
            event.getRegistry().register(TileEntityType.Builder.create(PhantomBlockTile::new, ModBlocks.PHANTOM_BLOCK).build(null).setRegistryName("phantom_block"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
          //  itemRegistryEvent.getRegistry().register(new SpellBook());
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.PHANTOM_BLOCK, new Item.Properties()).setRegistryName("phantom_block"));
        }
    }
}
