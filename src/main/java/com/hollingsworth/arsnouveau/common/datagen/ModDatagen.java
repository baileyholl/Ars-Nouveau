package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDatagen {

    @SubscribeEvent
    public static void datagen(GatherDataEvent event){
        System.out.println("calling datagen");
        event.getGenerator().addProvider(new LootTables(event.getGenerator()));
        event.getGenerator().addProvider(new DefaultTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(new ItemModelGenerator(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new LangDatagen(event.getGenerator(), ArsNouveau.MODID, "en_us"));
        event.getGenerator().addProvider(new SpellDocProvider(event.getGenerator()));
        event.getGenerator().addProvider(new Recipes(event.getGenerator()));
        event.getGenerator().addProvider(new BlockTagProvider(event.getGenerator()));
        event.getGenerator().addProvider(new BlockStatesDatagen(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new ApparatusRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new PatchouliProvider(event.getGenerator()));
    }
}
