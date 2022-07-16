package com.hollingsworth.arsnouveau;

import com.hollingsworth.arsnouveau.client.ClientHandler;
import com.hollingsworth.arsnouveau.client.TextureEvent;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.ClientEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.FMLEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.Pathfinding;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.world.Terrablender;
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



@Mod(ArsNouveau.MODID)
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsNouveau {
    public static final String MODID = "ars_nouveau";
    // Has to be runForDist, SafeRunForDist will throw a sided crash
    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static boolean caelusLoaded = false;
    public static boolean terrablenderLoaded = false;

    public static CreativeModeTab itemGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), MODID) {
        @Override
        public ItemStack makeIcon() {
            return ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance();
        }
    };

    public ArsNouveau() {
        caelusLoaded = ModList.get().isLoaded("caelus");
        terrablenderLoaded = ModList.get().isLoaded("terrablender");

        APIRegistry.setup();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class));
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(FMLEventHandler.class);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ModSetup::registerEvents);
        ModSetup.registers(modEventBus);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::postModLoadEvent);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.register(this);
        ModSetup.initGeckolib();
//        GLM.register(modEventBus);
    }

    public void setup(final FMLCommonSetupEvent event) {
        APIRegistry.postInit();
        Networking.registerMessages();
        event.enqueueWork(ModPotions::addRecipes);
        event.enqueueWork(ModEntities::registerPlacements);
        if (terrablenderLoaded && Config.ARCHWOOD_FOREST_WEIGHT.get() > 0) {
            event.enqueueWork(Terrablender::registerBiomes);
        }
    }

    public void postModLoadEvent(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.BLAZING_SAPLING.asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.CASCADING_SAPLING.asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.FLOURISHING_SAPLING.asItem(), 0.5F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.VEXING_SAPLING.asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.SOURCEBERRY_BUSH.asItem(), 0.3f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(ItemsRegistry.MAGE_BLOOM.get(), 0.65F);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.MAGE_BLOOM_CROP.asItem(), 0.65F);
        });
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TextureEvent::textEvent);
    }

    public void sendImc(InterModEnqueueEvent evt) {
        ModSetup.sendIntercoms();
    }

    @SubscribeEvent
    public static void onServerStopped(final ServerStoppingEvent event) {
        Pathfinding.shutdown();
    }
}
