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
        event.getGenerator().addProvider(new LootTables(event.getGenerator()));
        event.getGenerator().addProvider(new DefaultTableProvider(event.getGenerator()));
//        event.getGenerator().addProvider(new ItemModelGenerator(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new LangDatagen(event.getGenerator(), ArsNouveau.MODID, "en_us"));
        event.getGenerator().addProvider(new SpellDocProvider(event.getGenerator()));
        event.getGenerator().addProvider(new Recipes(event.getGenerator()));
        event.getGenerator().addProvider(new BlockTagProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(new BlockStatesDatagen(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new ApparatusRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new PatchouliProvider(event.getGenerator()));
        event.getGenerator().addProvider(new DungeonLootGenerator(event.getGenerator(), MODID));
        event.getGenerator().addProvider(new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new ItemTagProvider(event.getGenerator(), blocktagsprovider, MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new EntityTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));

    }
}
