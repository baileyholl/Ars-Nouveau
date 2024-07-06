package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.APIRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModDatagen {
    public static CompletableFuture<HolderLookup.Provider> registries;
    public static PackOutput output;
    @SubscribeEvent
    public static void datagen(GatherDataEvent event) {
        APIRegistry.postInit();
        output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        ModDatagen.registries = provider;
        event.getGenerator().addProvider(event.includeClient(), new ItemModelGenerator(output, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new BlockTagProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeClient(), new LangDatagen(output, ArsNouveau.MODID, "en_us"));

        event.getGenerator().addProvider(event.includeServer(), new RecipeDatagen(output, provider));
        event.getGenerator().addProvider(event.includeServer(), new BlockStatesDatagen(output, ArsNouveau.MODID, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ApparatusRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new PatchouliProvider(event.getGenerator()));
//        event.getGenerator().addProvider(event.includeServer(), new DefaultTableProvider(output, provider));
        event.getGenerator().addProvider(event.includeServer(), new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ItemTagProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new EntityTagProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new PlacedFeatureTagProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new PotionEffectTagProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new DyeRecipeDatagen(event.getGenerator()));
//        event.getGenerator().addProvider(event.includeServer(), new SimpleAdvancements(event.getGenerator()));

//        event.getGenerator().addProvider(event.includeServer(), new AdvancementProvider(output, provider, fileHelper));
//        event.getGenerator().addProvider(event.includeServer(), new CasterTomeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new SummonRitualProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new BuddingConversionProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ScryRitualProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new DispelEntityProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new StructureTagProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeClient(), new AtlasProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new DamageTypesProvider(output, provider));
        event.getGenerator().addProvider(event.includeServer(), new DamageTypesProvider.DamageTypesTagsProvider(output, provider, fileHelper));
        event.getGenerator().addProvider(event.includeServer(), new CompostablesProvider(output, provider));

        DatapackBuiltinEntriesProvider datapackProvider = new WorldgenProvider(output, provider);
        event.getGenerator().addProvider(event.includeServer(), datapackProvider);
        CompletableFuture<HolderLookup.Provider> lookupProvider = datapackProvider.getRegistryProvider();
        event.getGenerator().addProvider(event.includeServer(), new BiomeTagProvider(output, lookupProvider, fileHelper));
    }
}
