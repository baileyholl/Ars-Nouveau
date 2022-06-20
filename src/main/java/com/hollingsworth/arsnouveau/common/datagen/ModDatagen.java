package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.Config;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDatagen {

    @SubscribeEvent
    public static void datagen(GatherDataEvent event) {
        System.out.println("calling datagen");
        BlockTagsProvider blocktagsprovider = new BlockTagsProvider(event.getGenerator(), MODID, event.getExistingFileHelper());

        event.getGenerator().addProvider(event.includeClient(), new ItemModelGenerator(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeClient(), new LangDatagen(event.getGenerator(), ArsNouveau.MODID, "en_us"));

        event.getGenerator().addProvider(event.includeServer(), new Recipes(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new BlockTagProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BlockStatesDatagen(event.getGenerator(), ArsNouveau.MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new GlyphRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ApparatusRecipeProvider(event.getGenerator()));
        //event.getGenerator().addProvider(event.includeServer(), new PatchouliProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new LootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new DefaultTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new DungeonLootGenerator(event.getGenerator(), MODID));
        event.getGenerator().addProvider(event.includeServer(), new ImbuementRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new CrushRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new ItemTagProvider(event.getGenerator(), blocktagsprovider, MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new EntityTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BiomeTagProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
        datagenModifiers(event);
    }

    static void datagenModifiers(GatherDataEvent event) {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());

        Map<ResourceLocation, BiomeModifier> modifierMap = new HashMap<>();
        HolderSet.Named<Biome> SUMMON_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTagProvider.SUMMON_SPAWN_TAG);

        modifierMap.put(STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(SUMMON_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(),
                        Config.DEFAULT_STARBUNCLE_WEIGHT, 1, 1)
        ));


        event.getGenerator().addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(event.getGenerator(), event.getExistingFileHelper(), MODID,
                ops, ForgeRegistries.Keys.BIOME_MODIFIERS, modifierMap));

    }

    static ResourceLocation STARBUNCLE_SPAWN = new ResourceLocation(ArsNouveau.MODID, "starbuncle_spawn");

}
