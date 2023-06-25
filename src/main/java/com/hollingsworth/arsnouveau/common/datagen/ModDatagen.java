package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.APIRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDatagen {

    @SubscribeEvent
    public static void datagen(GatherDataEvent event) {
        //TODO: restore datagen
        APIRegistry.postInit();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        event.getGenerator().addProvider(event.includeClient(), new ItemModelGenerator(output,  event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BlockTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeClient(), new LangDatagen(output, ArsNouveau.MODID, "en_us"));

        event.getGenerator().addProvider(event.includeServer(), new RecipeDatagen(output));
        event.getGenerator().addProvider(event.includeServer(), new BlockStatesDatagen(output, ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ApparatusRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new PatchouliProvider(event.getGenerator()));
//        event.getGenerator().addProvider(event.includeServer(), new LootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new DefaultTableProvider(output));
//        event.getGenerator().addProvider(event.includeServer(), new DungeonLootGenerator(event.getGenerator(), MODID));
        event.getGenerator().addProvider(event.includeServer(), new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ItemTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new EntityTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BiomeTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new PlacedFeatureTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new PotionEffectTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new DyeRecipeDatagen(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ANAdvancements(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new CasterTomeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new SummonRitualProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new StructureTagProvider(output, provider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new WorldgenProvider(output, provider));
    }

}
