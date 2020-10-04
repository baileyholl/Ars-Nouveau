package com.hollingsworth.arsnouveau;

import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.client.ClientHandler;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.world.FeatureGen;
import com.hollingsworth.arsnouveau.setup.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

@Mod(ArsNouveau.MODID)
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsNouveau {
    public static final String MODID = "ars_nouveau";
    public static final String MODNAME = "Ars Nouveau";
    public static final String MODVERSION = "0.0.1";

    public static IProxy proxy = DistExecutor.runForDist(()-> () -> new ClientProxy(), () -> ()-> new ServerProxy());
    public static Logger logger;


    public static ItemGroup itemGroup = new ItemGroup("ars_nouveau") {
        @Override
        public ItemStack createIcon() {
            return ItemsRegistry.archmageSpellBook.getDefaultInstance();
        }
    };

    public ArsNouveau(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_CONFIG);

        APIRegistry.registerSpells();
        MappingUtil.setup();
        // modLoading setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.register(this);

    }

    public void setup (final FMLCommonSetupEvent event){
        APIRegistry.registerApparatusRecipes();
        FeatureGen.setupOreGen();
        //Pre-init code
        proxy.init();
        Networking.registerMessages();
    }

    public void clientSetup(final FMLClientSetupEvent event){
        proxy.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::init);
    }


    public void sendImc(InterModEnqueueEvent evt) {
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("charm"));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("ring").setSize(2));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("belt"));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("body"));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("head"));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("necklace"));
    }
    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)

}
