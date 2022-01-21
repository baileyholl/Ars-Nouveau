package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlyphRecipeProvider implements DataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public GlyphRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }
    List<GlyphRecipe> recipes = new ArrayList<>();
    @Override
    public void run(HashCache cache) throws IOException {
        List<Glyph> glyphList = ArsNouveauAPI.getInstance().getGlyphItemMap().values().stream().collect(Collectors.toList());
        Path output = this.generator.getOutputFolder();
        for(Glyph g : glyphList){
            Path path = getGlyphPath(output, g);
            DataProvider.save(GSON, cache, g.asRecipe(), path);
        }

        add(get(AugmentAccelerate.INSTANCE).withItem(Items.POWERED_RAIL).withItem(Items.SUGAR).withItem(Items.CLOCK));
        add(get(AugmentAmplify.INSTANCE).withItem(Items.DIAMOND_PICKAXE));
        add(get(AugmentAOE.INSTANCE).withItem(Items.FIREWORK_STAR));
        add(get(AugmentDampen.INSTANCE).withItem(Items.NETHER_BRICK));
        add(get(AugmentDurationDown.INSTANCE).withItem(Items.CLOCK).withItem(Items.GLOWSTONE_DUST));
        add(get(AugmentExtendTime.INSTANCE).withItem(Items.CLOCK).withIngredient(Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE)));
        add(get(AugmentExtract.INSTANCE).withItem(Items.EMERALD));
        add(get(AugmentFortune.INSTANCE).withItem(Items.RABBIT_FOOT));
        add(get(AugmentPierce.INSTANCE).withItem(Items.ARROW).withItem(ItemsRegistry.WILDEN_SPIKE));
        add(get(AugmentSensitive.INSTANCE).withItem(Items.SCAFFOLDING).withItem(Items.POPPY).withItem(Items.WATER_BUCKET));
        add(get(AugmentSplit.INSTANCE).withItem(BlockRegistry.RELAY_SPLITTER).withItem(ItemsRegistry.WILDEN_SPIKE).withItem(Items.STONECUTTER));

        add(get(MethodOrbit.INSTANCE).withItem(Items.COMPASS).withItem(Items.ENDER_EYE).withIngredient(Ingredient.of(Tags.Items.RODS_BLAZE)));
        add(get(MethodProjectile.INSTANCE).withItem(Items.FLETCHING_TABLE).withItem(Items.ARROW));
        add(get(MethodSelf.INSTANCE).withIngredient(Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES)).withItem(Items.IRON_CHESTPLATE));
        add(get(MethodTouch.INSTANCE).withIngredient(Ingredient.of(ItemTags.BUTTONS)));
        add(get(MethodUnderfoot.INSTANCE).withItem(Items.IRON_BOOTS).withIngredient(Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES)));

        add(get(EffectAquatic.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withIngredient(Ingredient.of(ItemTags.FISHES)).withIngredient(Ingredient.of(ItemTags.FISHES)));
        add(get(EffectBlink.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withIngredient(Ingredient.of(Tags.Items.ENDER_PEARLS), 4));
        add(get(EffectBounce.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withIngredient(Ingredient.of(Tags.Items.SLIMEBALLS), 3));
        add(get(EffectBreak.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.IRON_PICKAXE));
        add(get(EffectColdSnap.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withItem(Items.POWDER_SNOW_BUCKET).withItem(Items.ICE));
        add(get(EffectConjureWater.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withItem(Items.WATER_BUCKET));
        add(get(EffectCraft.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.CRAFTING_TABLE));
        add(get(EffectCrush.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.GRINDSTONE).withItem(Items.PISTON));
        add(get(EffectCut.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.SHEARS).withItem(Items.IRON_SWORD));
        add(get(EffectDelay.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.REPEATER).withItem(Items.CLOCK));
        add(get(EffectDispel.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.REPEATER).withItem(Items.CLOCK));


        for(GlyphRecipe recipe : recipes){
            Path path = getScribeGlyphPath(output,  recipe.output.getItem());
            DataProvider.save(GSON, cache, recipe.asRecipe(), path);
        }
    }

    public void add(GlyphRecipe recipe){
        recipes.add(recipe);
    }

    public GlyphRecipe get(AbstractSpellPart spellPart){
        return new GlyphRecipe(new ResourceLocation(ArsNouveau.MODID, "glyph_" + spellPart.getId()),
                ArsNouveauAPI.getInstance().getGlyphItem(spellPart).getDefaultInstance(), new ArrayList<>(), getExpFromTier(spellPart));
    }

    public int getExpFromTier(AbstractSpellPart spellPart){
        return 2;
    }

    private static Path getGlyphPath(Path pathIn, Glyph glyph) {
        return pathIn.resolve("data/ars_nouveau/recipes/glyphs/" + glyph.getRegistryName().getPath() + ".json");
    }
    private static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/ars_nouveau/recipes/" + glyph.getRegistryName().getPath() + ".json");
    }
    @Override
    public String getName() {
        return "Glyph Recipes";
    }
}
