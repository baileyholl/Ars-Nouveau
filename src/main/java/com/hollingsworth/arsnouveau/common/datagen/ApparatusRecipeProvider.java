package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
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
                .withPedestalItem(4, Items.GOLD_INGOT)
                .withPedestalItem(3, Items.FEATHER)
                .withPedestalItem(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.EffectLaunchID))
                .withCategory(ArsNouveauAPI.PatchouliCategories.equipment)
                .build());
        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.jarOfLight), Ingredient.fromItems(Items.GLASS_BOTTLE), Arrays.asList(Ingredient.fromItems(Items.GLOWSTONE),
                Ingredient.fromItems(Items.GLOWSTONE),
                Ingredient.fromItems(Items.GLOWSTONE),
                Ingredient.fromItems(Items.GLOWSTONE),
                Ingredient.fromItems(Items.REDSTONE_LAMP),
                Ingredient.fromTag(Tags.Items.GLASS),
                Ingredient.fromTag(Tags.Items.GLASS),
                Ingredient.fromItems(Items.REDSTONE_LAMP)), ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(BlockRegistry.MANA_BLOOM_CROP.asItem()), Ingredient.fromTag(Tags.Items.SEEDS),
                Arrays.asList(
                Recipes.MANA_GEM,   Recipes.MANA_GEM,   Recipes.MANA_GEM,  Recipes.MANA_GEM),  ArsNouveauAPI.PatchouliCategories.resources.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.ringOfLesserDiscount), Ingredient.fromItems(ItemsRegistry.ringOfPotential), Arrays.asList(
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND),
                Ingredient.fromTag(Tags.Items.ENDER_PEARLS),
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND),
                Recipes.MANA_GEM,
                Recipes.MANA_GEM,
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND),
                Ingredient.fromTag(Tags.Items.ENDER_PEARLS),
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND)),
                ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.ringOfGreaterDiscount), Ingredient.fromItems(ItemsRegistry.ringOfLesserDiscount), Arrays.asList(
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND),
                Ingredient.fromTag(Tags.Items.RODS_BLAZE),
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND),
                Recipes.MANA_GEM, Recipes.MANA_GEM,
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND),
                Ingredient.fromTag(Tags.Items.RODS_BLAZE),
                Ingredient.fromTag(Tags.Items.GEMS_DIAMOND)
        ), ArsNouveauAPI.PatchouliCategories.equipment.name()));

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

        addRecipe(new EnchantingApparatusRecipe(BlockRegistry.SUMMONING_CRYSTAL.asItem(), BlockRegistry.MANA_GEM_BLOCK.asItem(), new Item[]{
                Items.GOLD_INGOT, Items.DIAMOND, Items.DIAMOND, Items.GOLD_INGOT, BlockRegistry.ARCANE_STONE.asItem(),BlockRegistry.ARCANE_STONE.asItem(), BlockRegistry.ARCANE_STONE.asItem(), BlockRegistry.ARCANE_STONE.asItem()
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.whelpCharm, Items.EGG, new Item[]{
                ItemsRegistry.manaGem,ItemsRegistry.manaGem,Items.DIAMOND, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, ItemsRegistry.noviceSpellBook,
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(BlockRegistry.ARCANE_RELAY.asItem(), BlockRegistry.MANA_JAR.asItem(), new Item[]{
                ItemsRegistry.manaGem,ItemsRegistry.manaGem,Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT,
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.carbuncleCharm, ItemsRegistry.carbuncleShard, new Item[]{
                ItemsRegistry.manaGem,ItemsRegistry.manaGem,ItemsRegistry.manaGem, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GOLD_NUGGET,
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.amuletOfManaBoost, ItemsRegistry.dullTrinket, new Item[]{
                Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ItemsRegistry.manaGem, ItemsRegistry.manaGem, ItemsRegistry.manaGem, ItemsRegistry.manaGem, ItemsRegistry.manaGem
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.amuletOfManaRegen, ItemsRegistry.dullTrinket, new Item[]{
                Items.DIAMOND, Items.DIAMOND, Items.GOLD_INGOT, Items.GOLD_INGOT, ItemsRegistry.manaGem, ItemsRegistry.manaGem, ItemsRegistry.manaGem, ItemsRegistry.manaGem
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(BlockRegistry.ARCANE_RELAY_SPLITTER.asItem(), BlockRegistry.ARCANE_RELAY.asItem(), new Item[]{
                Items.QUARTZ,Items.QUARTZ, Items.QUARTZ, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.QUARTZ
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.sylphCharm,ItemsRegistry.sylphShard, new Item[]{
                ItemsRegistry.manaGem,BlockRegistry.MANA_BLOOM_CROP.asItem(), ItemsRegistry.manaBloom, Items.DIAMOND, Items.OAK_SAPLING, Items.SPRUCE_SAPLING, Items.BIRCH_SAPLING, Items.WHEAT_SEEDS
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(BlockRegistry.SPELL_TURRET.asItem(),Items.DISPENSER, new Item[]{
                Items.BLAZE_ROD, Items.GOLD_INGOT, Items.GOLD_INGOT,Items.GOLD_INGOT,
               ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.EffectRedstoneID),
                Items.QUARTZ_BLOCK, Items.REDSTONE_BLOCK, ItemsRegistry.spellParchment
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.VOID_JAR, Items.GLASS_BOTTLE, new Item[]{
                Items.LAVA_BUCKET, Items.BUCKET, Items.ENDER_PEARL,ItemsRegistry.ALLOW_ITEM_SCROLL
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.DOMINION_ROD, Items.STICK, new Item[]{
                ItemsRegistry.manaGem,  ItemsRegistry.manaGem, Items.GOLD_INGOT
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(ItemsRegistry.WIXIE_CHARM, ItemsRegistry.WIXIE_SHARD, new Item[]{
                Items.DARK_OAK_SAPLING, Items.CRAFTING_TABLE, Items.EMERALD, Items.BREWING_STAND
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.WAND), Ingredient.fromItems(BlockRegistry.ARCHWOOD_PLANK.asItem()),
                listOfIngred(new Item[]{
                ItemsRegistry.manaGem, ItemsRegistry.manaGem,ItemsRegistry.manaGem, Items.GOLD_INGOT,Items.GOLD_INGOT,
                        ItemsRegistry.spellParchment, ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile()),
                        ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAccelerate())
        }), ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.SPELL_BOW), Ingredient.fromItems(Items.BOW),
                listOfIngred(new Item[]{
                        BlockRegistry.MANA_GEM_BLOCK.asItem(), Items.GOLD_BLOCK, ArsNouveauAPI.getInstance().getGlyphItem(new MethodProjectile())
                }), ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.AMPLIFY_ARROW, 32), Ingredient.fromItems(Items.ARROW),
                listOfIngred(new Item[]{
                        BlockRegistry.MANA_GEM_BLOCK.asItem(),BlockRegistry.MANA_GEM_BLOCK.asItem(), ArsNouveauAPI.getInstance().getGlyphItem(new AugmentAmplify())
                }), ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.SPLIT_ARROW, 32), Ingredient.fromItems(Items.ARROW),
                listOfIngred(new Item[]{
                        BlockRegistry.MANA_GEM_BLOCK.asItem(),BlockRegistry.MANA_GEM_BLOCK.asItem(), ArsNouveauAPI.getInstance().getGlyphItem(new AugmentSplit())
                }), ArsNouveauAPI.PatchouliCategories.equipment.name()));

        addRecipe(new EnchantingApparatusRecipe(new ItemStack(ItemsRegistry.PIERCE_ARROW, 32), Ingredient.fromItems(Items.ARROW),
                listOfIngred(new Item[]{
                        BlockRegistry.MANA_GEM_BLOCK.asItem(),BlockRegistry.MANA_GEM_BLOCK.asItem(), ArsNouveauAPI.getInstance().getGlyphItem(new AugmentPierce())
                }), ArsNouveauAPI.PatchouliCategories.equipment.name()));


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
