package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDatagen {

    @SubscribeEvent
    public static void datagen(GatherDataEvent event){
        System.out.println("calling datagen");
        BlockTagsProvider blocktagsprovider = new BlockTagsProvider(event.getGenerator(), MODID, event.getExistingFileHelper());
        event.getGenerator().addProvider(true, new LootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new DefaultTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ItemModelGenerator(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new LangDatagen(event.getGenerator(), ArsNouveau.MODID, "en_us"));

        event.getGenerator().addProvider(true, new Recipes(event.getGenerator()));
        event.getGenerator().addProvider(true, new BlockTagProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new BlockStatesDatagen(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ApparatusRecipeProvider(event.getGenerator()));
        //event.getGenerator().addProvider(true, new PatchouliProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new DungeonLootGenerator(event.getGenerator(), MODID));
        event.getGenerator().addProvider(true, new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ItemTagProvider(event.getGenerator(), blocktagsprovider, MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new EntityTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));

    }
}
