package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApparatusRecipeProvider implements IDataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public ApparatusRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    List<EnchantingApparatusRecipe> recipes = new ArrayList<>();
    @Override
    public void act(DirectoryCache cache) throws IOException {
        addEntries();
        Path output = this.generator.getOutputFolder();
        for(IEnchantingRecipe g : recipes){
            if(g instanceof EnchantingApparatusRecipe){
                System.out.println(g);
                Path path = getRecipePath(output, ((EnchantingApparatusRecipe) g).result.getItem());
                IDataProvider.save(GSON, cache, ((EnchantingApparatusRecipe) g).asRecipe(), path);
            }
        }
    }

    public ApparatusRecipeBuilder builder(){
        return ApparatusRecipeBuilder.builder();
    }

    public void addEntries(){
//        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.beltOfLevitation, ItemsRegistry.mundaneBelt, new Item[]
//                {Items.GOLD_INGOT,Items.GOLD_INGOT,Items.GOLD_INGOT, Items.GOLD_INGOT,
//                Items.FEATHER,Items.FEATHER,Items.FEATHER, ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.EffectLaunchID)},  ArsNouveauAPI.PatchouliCategories.equipment.name()));
        addRecipe(builder().withResult(ItemsRegistry.beltOfLevitation)
                .withReagent(ItemsRegistry.mundaneBelt)
                .withPedestalItem(4, Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(3, Items.FEATHER)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.EffectLaunchID))
                .withCategory(ArsNouveauAPI.PatchouliCategories.equipment)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.jarOfLight)
                .withReagent(Items.GLASS_BOTTLE)
                .withPedestalItem(4, Items.GLOWSTONE)
                .withPedestalItem(2, Items.REDSTONE_LAMP)
                .withPedestalItem(2,  Ingredient.fromTag(Tags.Items.GLASS))
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.MANA_BLOOM_CROP)
                .withReagent(Ingredient.fromTag(Tags.Items.SEEDS))
                .withPedestalItem(4, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ringOfLesserDiscount)
                .withReagent(ItemsRegistry.ringOfPotential)
                .withPedestalItem(4,  Ingredient.fromTag(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.fromTag(Tags.Items.ENDER_PEARLS))
                .withPedestalItem(2,   Recipes.MANA_GEM)
                .withPedestalItem(2,   Ingredient.fromTag(Tags.Items.ENDER_PEARLS))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.ringOfGreaterDiscount)
                .withReagent(ItemsRegistry.ringOfLesserDiscount)
                .withPedestalItem(4,  Ingredient.fromTag(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.fromTag(Tags.Items.RODS_BLAZE))
                .withPedestalItem(2, Recipes.MANA_GEM)
                .build());


        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.beltOfUnstableGifts), Ingredient.fromItems(ItemsRegistry.mundaneBelt), Arrays.asList(
                Ingredient.fromItems(Items.SUGAR),
                Ingredient.fromTag(Tags.Items.CROPS_NETHER_WART),
                Ingredient.fromTag(Tags.Items.RODS_BLAZE),
                Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE),
                Ingredient.fromItems(Items.FERMENTED_SPIDER_EYE),
                Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE),
                Ingredient.fromItems(Items.BREWING_STAND),
                Ingredient.fromTag(Tags.Items.FEATHERS)
        ), ArsNouveauAPI.PatchouliCategories.equipment.name()));


        addRecipe(builder()
                .withResult(BlockRegistry.SUMMONING_CRYSTAL)
                .withReagent(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(2,  Ingredient.fromTag(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(4, BlockRegistry.ARCANE_STONE)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.whelpCharm)
                .withReagent( Items.EGG)
                .withPedestalItem(4,Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(3, Recipes.MANA_GEM)
                .withPedestalItem( ItemsRegistry.noviceSpellBook)
                .build());


        addRecipe(builder()
                .withResult(BlockRegistry.ARCANE_RELAY)
                .withReagent(  BlockRegistry.MANA_JAR)
                .withPedestalItem(6,Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(2, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.carbuncleCharm)
                .withReagent(ItemsRegistry.carbuncleShard)
                .withPedestalItem(5,Ingredient.fromTag(Tags.Items.NUGGETS_GOLD))
                .withPedestalItem(3, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.amuletOfManaBoost)
                .withReagent(ItemsRegistry.dullTrinket)
                .withPedestalItem(3,Ingredient.fromTag(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(5, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.amuletOfManaRegen)
                .withReagent(ItemsRegistry.dullTrinket)
                .withPedestalItem(2,Ingredient.fromTag(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem(2,Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(4, Recipes.MANA_GEM)
                .build());

        addRecipe(builder()
                .withResult(BlockRegistry.ARCANE_RELAY_SPLITTER)
                .withReagent( BlockRegistry.ARCANE_RELAY)
                .withPedestalItem(4,Ingredient.fromTag(Tags.Items.GEMS_QUARTZ))
                .withPedestalItem(4,Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.sylphCharm)
                .withReagent(ItemsRegistry.sylphShard)
                .withPedestalItem(Recipes.MANA_GEM)
                .withPedestalItem(BlockRegistry.MANA_BLOOM_CROP)
                .withPedestalItem(ItemsRegistry.manaBloom)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.GEMS_DIAMOND))
                .withPedestalItem( Items.OAK_SAPLING)
                .withPedestalItem( Items.SPRUCE_SAPLING)
                .withPedestalItem( Items.BIRCH_SAPLING)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.SEEDS_WHEAT))
                .build());


        addRecipe(builder()
                .withResult(BlockRegistry.SPELL_TURRET)
                .withReagent(Items.DISPENSER)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.RODS_BLAZE))
                .withPedestalItem( ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.EffectRedstoneID))
                .withPedestalItem(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                .withPedestalItem(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .withPedestalItem(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(3,Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.VOID_JAR)
                .withReagent(Items.GLASS_BOTTLE)
                .withPedestalItem(Items.LAVA_BUCKET)
                .withPedestalItem(Items.BUCKET)
                .withPedestalItem(ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.ENDER_PEARLS))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.DOMINION_ROD)
                .withReagent(Items.STICK)
                .withPedestalItem(2, Recipes.MANA_GEM)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WIXIE_CHARM)
                .withReagent( ItemsRegistry.WIXIE_SHARD)
                .withPedestalItem(Ingredient.fromTag(ItemTags.SAPLINGS))
                .withPedestalItem(Ingredient.fromTag(Tags.Items.GEMS_EMERALD))
                .withPedestalItem(Items.CRAFTING_TABLE)
                .withPedestalItem(Items.BREWING_STAND)
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.WAND)
                .withReagent(Recipes.ARCHWOOD_LOG)
                .withPedestalItem(4, Recipes.MANA_GEM)
                .withPedestalItem(2, Ingredient.fromTag(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile()))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAccelerate()))
                .build());

        addRecipe(builder()
                .withResult(ItemsRegistry.SPELL_BOW)
                .withReagent(Items.BOW)
                .withPedestalItem(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile()))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.AMPLIFY_ARROW, 32))
                .withReagent( Ingredient.fromTag(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify()))
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.SPLIT_ARROW, 32))
                .withReagent( Ingredient.fromTag(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentSplit()))
                .build());
        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.PIERCE_ARROW, 32))
                .withReagent( Ingredient.fromTag(ItemTags.ARROWS))
                .withPedestalItem(2, Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce()))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK))
                .withReagent(Ingredient.fromItems(Items.GLASS_BOTTLE))
                .withPedestalItem(2, ItemsRegistry.blazeFiber)
                .withPedestalItem(Recipes.MANA_GEM_BLOCK)
                .withPedestalItem(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_GOLD))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_EXTEND_TIME))
                .withReagent(Ingredient.fromItems(ItemsRegistry.POTION_FLASK))
                .withPedestalItem(8, ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtendTime()))
                .build());


        addRecipe(builder()
                .withResult(new ItemStack(ItemsRegistry.POTION_FLASK_AMPLIFY))
                .withReagent(Ingredient.fromItems(ItemsRegistry.POTION_FLASK))
                .withPedestalItem(8, ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify()))
                .build());

        addRecipe(builder()
                .withResult(new ItemStack(BlockRegistry.POTION_MELDER))
                .withReagent(Ingredient.fromItems(BlockRegistry.POTION_JAR))
                .withPedestalItem(2, ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtract()))
                .withPedestalItem(2,Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_GOLD))
                .withPedestalItem(4,Ingredient.fromTag(Tags.Items.RODS_BLAZE))
                .build());
    }

    public static List<Ingredient> listOfIngred(Item[] items) {
        return Arrays.stream(items).map(Ingredient::fromItems).collect(Collectors.toList());
    }

    public void addRecipe(EnchantingApparatusRecipe recipe){
        recipes.add(recipe);
    }

    private static Path getRecipePath(Path pathIn, Item item) {
        return pathIn.resolve("data/ars_nouveau/recipes/" + item.getRegistryName().getPath() + ".json");
    }
    @Override
    public String getName() {
        return "Apparatus";
    }
}
