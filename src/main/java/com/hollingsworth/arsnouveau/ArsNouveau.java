package com.hollingsworth.arsnouveau;

import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.DispenserRitualBehavior;
import com.hollingsworth.arsnouveau.client.registry.ClientHandler;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.ClientEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.FMLEventHandler;
import com.hollingsworth.arsnouveau.common.event.BreezeEvent;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.common.world.Terrablender;
import com.hollingsworth.arsnouveau.setup.ModSetup;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.config.StartupConfig;
import com.hollingsworth.arsnouveau.setup.proxy.ClientProxy;
import com.hollingsworth.arsnouveau.setup.proxy.IProxy;
import com.hollingsworth.arsnouveau.setup.proxy.ServerProxy;
import com.hollingsworth.arsnouveau.setup.registry.*;
import com.hollingsworth.arsnouveau.setup.reward.Rewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(ArsNouveau.MODID)
public class ArsNouveau {
    public static final String MODID = "ars_nouveau";
    @SuppressWarnings("deprecation") // Has to be runForDist, SafeRunForDist will throw a sided crash
    public static IProxy proxy;
    public static boolean caelusLoaded = false;
    public static boolean terrablenderLoaded = false;
    public static boolean optifineLoaded = false;
    public static boolean sodiumLoaded = false;
    public static boolean immersivePortalsLoaded = false;
    public static boolean patchouliLoaded = false;

    public static List<String> postLoadWarnings = new ArrayList<>();

    public static TicketController ticketController = new TicketController(ArsNouveau.prefix("ticket_controller"),  (level, ticketHelper) -> {
        ticketHelper.getEntityTickets().forEach(((uuid, chunk) -> {
            if (level.getEntity(uuid) == null)
                ticketHelper.removeAllTickets(uuid);
        }));
    });
    public static boolean isDebug = false && !FMLEnvironment.production;
    public ArsNouveau(IEventBus modEventBus, ModContainer modContainer){
        NeoForge.EVENT_BUS.addListener(FMLEventHandler::onServerStopped);
        caelusLoaded = ModList.get().isLoaded("caelus");
        terrablenderLoaded = ModList.get().isLoaded("terrablender");
        sodiumLoaded = ModList.get().isLoaded("rubidium");
        patchouliLoaded = ModList.get().isLoaded("patchouli");
        immersivePortalsLoaded = ModList.get().isLoaded("immersive_portals_core");
        APIRegistry.setup();
        modContainer.registerConfig(ModConfig.Type.STARTUP, StartupConfig.STARTUP_CONFIG);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.register(ClientEventHandler.class);
        }
        modEventBus.addListener(Networking::register);
        modEventBus.addListener(ModSetup::registerEvents);
        modEventBus.addListener(CapabilityRegistry::registerCapabilities);
        ModSetup.registers(modEventBus);
        modEventBus.addListener(ModEntities::registerPlacements);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::postModLoadEvent);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener((RegisterTicketControllersEvent e) ->{
            e.register(ticketController);
        });
        NeoForge.EVENT_BUS.addListener(BubbleEntity::onAttacked);
        NeoForge.EVENT_BUS.addListener(BubbleEntity::entityHurt);
        NeoForge.EVENT_BUS.addListener(BreezeEvent::onSpellResolve);
        ANCriteriaTriggers.init();
        try {
            Thread thread = new Thread(Rewards::init);
            thread.setDaemon(true);
            thread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(FMLEnvironment.dist.isClient()){
            ArsNouveau.proxy = new Supplier<IProxy>() {
                @Override
                public IProxy get() {
                    return new ClientProxy();
                }
            }.get();
        }else{
            ArsNouveau.proxy = new ServerProxy();
        }
    }

    public void setup(final FMLCommonSetupEvent event) {
        APIRegistry.postInit();
        if (terrablenderLoaded && Config.ARCHWOOD_FOREST_WEIGHT.get() > 0) {
            event.enqueueWork(Terrablender::registerBiomes);
        }

        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> {
            GenericRecipeRegistry.reloadAll(e.getServer().getRecipeManager());
            CasterTomeRegistry.reloadTomeData(e.getServer().getRecipeManager(), e.getServer().registryAccess());
            BuddingConversionRegistry.reloadBuddingConversionRecipes(e.getServer().getRecipeManager());
            ScryRitualRegistry.reloadScryRitualRecipes(e.getServer().getRecipeManager());
        });

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
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.BOMBEGRANTE_POD.asItem(),0.65f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.MENDOSTEEN_POD.asItem(),0.65f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.FROSTAYA_POD.asItem(),0.65f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.BASTION_POD.asItem(),0.65f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.FLOURISHING_LEAVES.asItem(),0.3f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.VEXING_LEAVES.asItem(),0.3f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.CASCADING_LEAVE.asItem(),0.3f);
            ComposterBlock.COMPOSTABLES.putIfAbsent(BlockRegistry.BLAZING_LEAVES.asItem(),0.3f);

            FlowerPotBlock flowerPot = (FlowerPotBlock) Blocks.FLOWER_POT;
            for (var pot : BlockRegistry.flowerPots.entrySet()){
                flowerPot.addPlant(pot.getKey().get(), pot::getValue);
            }

            for (RitualTablet tablet : RitualRegistry.getRitualItemMap().values()){
                DispenserBlock.registerBehavior(tablet, new DispenserRitualBehavior());
            }

        });
        for(String warning : postLoadWarnings){
            Log.getLogger().error(warning);
        }
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(ClientHandler::init);
        try {
            Class.forName("net.optifine.Config");
            optifineLoaded = true;
        } catch (Exception e) {
            optifineLoaded = false;
        }
    }

    public static ResourceLocation prefix(String str) {
        return ResourceLocation.fromNamespaceAndPath(MODID, str);
    }
}
