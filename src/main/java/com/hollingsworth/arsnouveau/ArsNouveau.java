package com.hollingsworth.arsnouveau;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.DispenserRitualBehavior;
import com.hollingsworth.arsnouveau.client.events.ClientHandler;
import com.hollingsworth.arsnouveau.client.events.TextureEvent;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.ClientEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.FMLEventHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.Pathfinding;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.tomes.CasterTomeData;
import com.hollingsworth.arsnouveau.common.world.Terrablender;
import com.hollingsworth.arsnouveau.setup.*;
import com.hollingsworth.arsnouveau.setup.config.ANModConfig;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
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
    @SuppressWarnings("deprecation") // Has to be runForDist, SafeRunForDist will throw a sided crash
    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static boolean caelusLoaded = false;
    public static boolean terrablenderLoaded = false;

    public static CreativeModeTab itemGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), MODID) {
        @Override
        public ItemStack makeIcon() {
            return ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance();
        }
    };
    public static CreativeModeTab glyphGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), "ars_glyphs") {

        @Override
        public void fillItemList(NonNullList<ItemStack> pItems) {
            super.fillItemList(pItems);
            pItems.sort((ItemStack i1, ItemStack i2) -> {
                if (i1.getItem() instanceof Glyph g1 && i2.getItem() instanceof Glyph g2) {
                    return BaseBook.COMPARE_TYPE_THEN_NAME.compare(g1.spellPart, g2.spellPart);
                } else {
                    return -1;
                }
            });
        }

        @Override
        public ItemStack makeIcon() {
            return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE).getDefaultInstance();
        }
    };

    public ArsNouveau(){
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(FMLEventHandler.class);
        caelusLoaded = ModList.get().isLoaded("caelus");
        terrablenderLoaded = ModList.get().isLoaded("terrablender");
        APIRegistry.setup();
        ANModConfig serverConfig = new ANModConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG, ModLoadingContext.get().getActiveContainer(),MODID + "-server");
        ModLoadingContext.get().getActiveContainer().addConfig(serverConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class));

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ModSetup::registerEvents);
        ModSetup.registers(modEventBus);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::postModLoadEvent);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.register(this);
        ModSetup.initGeckolib();
        ANCriteriaTriggers.init();
    }

    public void setup(final FMLCommonSetupEvent event) {
        APIRegistry.postInit();
        Networking.registerMessages();
        event.enqueueWork(ModPotions::addRecipes);
        event.enqueueWork(ModEntities::registerPlacements);
        if (terrablenderLoaded && Config.ARCHWOOD_FOREST_WEIGHT.get() > 0) {
            event.enqueueWork(Terrablender::registerBiomes);
        }
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent e) -> CasterTomeData.reloadCasterTomes(e.getServer()));
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
            for (RitualTablet tablet : ArsNouveauAPI.getInstance().getRitualItemMap().values()){
                DispenserBlock.registerBehavior(tablet, new DispenserRitualBehavior());
            }
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
