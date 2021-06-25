package com.hollingsworth.arsnouveau;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.client.ClientHandler;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.world.WorldEvent;
import com.hollingsworth.arsnouveau.setup.APIRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.ModSetup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.hollingsworth.arsnouveau.common.datagen.DungeonLootGenerator.GLM;

@Mod(ArsNouveau.MODID)
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsNouveau {
    public static final String MODID = "ars_nouveau";

    public static boolean caelusLoaded = false;

    public static ItemGroup itemGroup = new ItemGroup(MODID) {

        @Override
        public ItemStack makeIcon() {
            return ItemsRegistry.archmageSpellBook.getDefaultInstance();
        }
    };

    public ArsNouveau(){
        caelusLoaded = ModList.get().isLoaded("caelus");
        APIRegistry.registerSpells();
        MappingUtil.setup();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.register(this);
        ModSetup.initGeckolib();
        GLM.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public void setup (final FMLCommonSetupEvent event){
        ManaCapability.register();
        APIRegistry.registerApparatusRecipes();
        event.enqueueWork(WorldEvent::registerFeatures);
        Networking.registerMessages();
        event.enqueueWork(ModPotions::addRecipes);
        if(Config.ARCHWOOD_FOREST_WEIGHT.get() > 0) {
            BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(WorldEvent.archwoodKey, Config.ARCHWOOD_FOREST_WEIGHT.get()));
        }
    }

    public void clientSetup(final FMLClientSetupEvent event){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::init);
    }

    public void sendImc(InterModEnqueueEvent evt) {
        ModSetup.sendIntercoms();
    }
}
