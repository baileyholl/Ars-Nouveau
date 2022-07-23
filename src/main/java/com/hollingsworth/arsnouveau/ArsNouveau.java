package com.hollingsworth.arsnouveau;

import com.hollingsworth.arsnouveau.api.mana.ManaAttributes;
import com.hollingsworth.arsnouveau.client.ClientHandler;
import com.hollingsworth.arsnouveau.client.TextureEvent;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.ClientEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.FMLEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.Pathfinding;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.hollingsworth.arsnouveau.common.datagen.DungeonLootGenerator.GLM;

@Mod(ArsNouveau.MODID)
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsNouveau {
    public static final String MODID = "ars_nouveau";
    public static IProxy proxy = DistExecutor.runForDist(()-> ClientProxy::new, () -> ServerProxy::new);
    public static boolean caelusLoaded = false;

    public static CreativeModeTab itemGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), MODID) {
        @Override
        public ItemStack makeIcon() {
            return ItemsRegistry.CREATIVE_SPELLBOOK.getDefaultInstance();
        }
    };

    public ArsNouveau(){
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(FMLEventHandler.class);
        caelusLoaded = ModList.get().isLoaded("caelus");
        APIRegistry.setup();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class));

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ManaAttributes.ATTRIBUTES.register(modEventBus);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postModLoadEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.register(this);
        ModSetup.initGeckolib();
        GLM.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public void setup (final FMLCommonSetupEvent event){
//        event.enqueueWork(WorldEvent::registerFeatures);
        Networking.registerMessages();
        event.enqueueWork(ModPotions::addRecipes);
        //TODO: Restore archwood forest
        if(false && Config.ARCHWOOD_FOREST_WEIGHT.get() > 0) {
           // BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(WorldEvent.archwoodKey, Config.ARCHWOOD_FOREST_WEIGHT.get()));
        }
    }

    public void postModLoadEvent(final FMLLoadCompleteEvent event){
        event.enqueueWork(()->{
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.BLAZING_SAPLING.asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.CASCADING_SAPLING.asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.FLOURISHING_SAPLING.asItem(), 0.5F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.VEXING_SAPLING.asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.SOURCEBERRY_BUSH.asItem(), 0.3f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(ItemsRegistry.MAGE_BLOOM, 0.65F);
        });
    }

    public void clientSetup(final FMLClientSetupEvent event){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TextureEvent::textEvent);
    }

    public void sendImc(InterModEnqueueEvent evt) {
        ModSetup.sendIntercoms();
    }

    @SubscribeEvent
    public static void onServerStopped(final ServerStoppingEvent event)
    {
        Pathfinding.shutdown();
    }
}
