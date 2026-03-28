package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.APIRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber()
public class ModDatagen {
    public static CompletableFuture<HolderLookup.Provider> registries;
    public static PackOutput output;

    // 1.21.11 NeoForge: GatherDataEvent is abstract — must use a concrete subclass
    @SubscribeEvent
    public static void datagen(GatherDataEvent.Server event) {
        APIRegistry.postInit();
        output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ModDatagen.registries = provider;
        // 1.21.11: GatherDataEvent.includeServer()/includeClient() removed; always pass true
        event.getGenerator().addProvider(true, new ItemModelGenerator(output));
        event.getGenerator().addProvider(true, new BlockTagProvider(output, provider));
        event.getGenerator().addProvider(true, new LangDatagen(output, ArsNouveau.MODID, "en_us"));

        event.getGenerator().addProvider(true, new RecipeDatagen.Runner(output, provider));
        event.getGenerator().addProvider(true, new BlockStatesDatagen(output, ArsNouveau.MODID));
        event.getGenerator().addProvider(true, new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ApparatusRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new DefaultTableProvider(output, provider));
        event.getGenerator().addProvider(true, new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ItemTagProvider(output, provider));
        event.getGenerator().addProvider(true, new EntityTagProvider(output, provider));
        event.getGenerator().addProvider(true, new BannerTagsProvider(output, provider));
        event.getGenerator().addProvider(true, new PlacedFeatureTagProvider(output, provider));
        event.getGenerator().addProvider(true, new PotionEffectTagProvider(output, provider));
        event.getGenerator().addProvider(true, new DyeRecipeDatagen(event.getGenerator()));

        event.getGenerator().addProvider(true, new AdvancementProvider(output, provider));
        event.getGenerator().addProvider(true, new CasterTomeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new SummonRitualProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new BuddingConversionProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new AlakarkinosRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ScryRitualProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new OneOffRecipesProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new DispelEntityProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new StructureTagProvider(output, provider));
        event.getGenerator().addProvider(true, new AtlasProvider(output, provider));
        event.getGenerator().addProvider(true, new EnchantmentProvider(output, provider));
        event.getGenerator().addProvider(true, new EnchantmentProvider.EnchantmentTagsProvider(output, provider));
        event.getGenerator().addProvider(true, new MusicProvider(output, provider));
        event.getGenerator().addProvider(true, new DamageTypesProvider(output, provider));
        event.getGenerator().addProvider(true, new DamageTypesProvider.DamageTypesTagsProvider(output, provider));
        event.getGenerator().addProvider(true, new CompostablesProvider(output, provider));

        event.getGenerator().addProvider(true, new ANCurioProvider(output, null, provider));

        event.getGenerator().addProvider(true, new PatchouliProvider(event.getGenerator(), provider));

        DatapackBuiltinEntriesProvider datapackProvider = new WorldgenProvider(output, provider);
        event.getGenerator().addProvider(true, datapackProvider);
        CompletableFuture<HolderLookup.Provider> lookupProvider = datapackProvider.getRegistryProvider();
        event.getGenerator().addProvider(true, new BiomeTagProvider(output, lookupProvider));
    }
}
