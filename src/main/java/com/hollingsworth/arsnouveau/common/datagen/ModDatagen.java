package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDatagen {

    @SubscribeEvent
    public static void datagen(GatherDataEvent event) {
        BlockTagsProvider blocktagsprovider = new BlockTagsProvider(event.getGenerator(), MODID, event.getExistingFileHelper());

        event.getGenerator().addProvider(event.includeClient(), new ItemModelGenerator(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeClient(), new LangDatagen(event.getGenerator(), ArsNouveau.MODID, "en_us"));

        event.getGenerator().addProvider(event.includeServer(), new RecipeDatagen(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new BlockTagProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BlockStatesDatagen(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ApparatusRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new PatchouliProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new LootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new DefaultTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new DungeonLootGenerator(event.getGenerator(), MODID));
        event.getGenerator().addProvider(event.includeServer(), new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ItemTagProvider(event.getGenerator(), blocktagsprovider, MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new EntityTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BiomeTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new PlacedFeatureTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new JsonDatagen(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new Advancements(event.getGenerator(), event.getExistingFileHelper()));
        BiomeModifiersProvider.datagenModifiers(event);
    }

}
