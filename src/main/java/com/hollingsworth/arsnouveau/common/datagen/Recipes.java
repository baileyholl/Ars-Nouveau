package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    public static Tag.Named<Item> SOURCE_GEM_TAG = ItemTags.bind("forge:gems/source");
    public static Tag.Named<Item> SOURCE_GEM_BLOCK_TAG = ItemTags.bind("forge:storage_blocks/source");
    public static Tag.Named<Item> ARCHWOOD_LOG_TAG = ItemTags.bind("forge:logs/archwood");
    public static Tag.Named<Block> DECORATIVE_AN = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "an_decorative"));
    public static Tag.Named<Block> MAGIC_SAPLINGS = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_saplings"));
    public static Tag.Named<Block> MAGIC_PLANTS = BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_plants"));
    public static Tag.Named<Item> MAGIC_FOOD = ItemTags.bind("ars_nouveau:magic_food");

    public static Ingredient SOURCE_GEM = Ingredient.of(SOURCE_GEM_TAG);
    public static Ingredient SOURCE_GEM_BLOCK = Ingredient.of(SOURCE_GEM_BLOCK_TAG);
    public static Ingredient ARCHWOOD_LOG = Ingredient.of(ARCHWOOD_LOG_TAG);


    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        {
            makeArmor("novice", consumer, ItemsRegistry.MAGE_FIBER);
            makeArmor("apprentice", consumer, ItemsRegistry.BLAZE_FIBER);
            makeArmor("archmage", consumer, ItemsRegistry.END_FIBER);


            ShapelessRecipeBuilder.shapeless(ItemsRegistry.WORN_NOTEBOOK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Ingredient.of(Tags.Items.GEMS_LAPIS), 1)
                    .requires(Items.BOOK).save(consumer);


            ShapelessRecipeBuilder.shapeless(ItemsRegistry.MAGE_FIBER, 4).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MAGE_BLOOM)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.RUNIC_CHALK, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE).requires(Items.BONE_MEAL).requires(ItemsRegistry.MAGE_FIBER)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.BLAZE_FIBER, 2).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MAGE_FIBER, 2)
                    .requires(Items.BLAZE_POWDER)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.END_FIBER, 2).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLAZE_FIBER, 2)
                    .requires(Items.POPPED_CHORUS_FRUIT)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.SOURCE_JAR).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("yyy")
                    .pattern("x x")
                    .pattern("yyy").define('x', Tags.Items.GLASS).define('y', BlockRegistry.ARCHWOOD_SLABS).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_PEDESTAL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern(" x ")
                    .pattern("xxx").define('x', BlockRegistry.ARCANE_STONE).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ENCHANTING_APP_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xyx")
                    .pattern("x x")
                    .pattern("zzz").define('x', Tags.Items.INGOTS_IRON)
                    .define('y', Tags.Items.GEMS_DIAMOND)
                    .define('z', BlockRegistry.ARCANE_STONE).save(consumer);

            ShapedRecipeBuilder.shaped(ItemsRegistry.MUNDANE_BELT).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("   ")
                    .pattern("xyx")
                    .pattern(" x ")
                    .define('x', Tags.Items.LEATHER)
                    .define('y', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(ItemsRegistry.RING_OF_POTENTIAL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', Tags.Items.NUGGETS_IRON).define('y', SOURCE_GEM).save(consumer);


            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_BRICKS, 4).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xx ")
                    .pattern("xx ")
                    .pattern("   ").define('x', BlockRegistry.ARCANE_STONE).save(consumer);


            ShapedRecipeBuilder.shaped(BlockRegistry.SCRIBES_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("yzy")
                    .pattern("y y").define('x', Ingredient.of(BlockRegistry.ARCHWOOD_SLABS))
                    .define('y', Items.STICK)
                    .define('z', Ingredient.of(ARCHWOOD_LOG_TAG)).save(consumer);

            ShapedRecipeBuilder.shaped(ItemsRegistry.DULL_TRINKET).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern("xyx")
                    .pattern(" x ").define('x', Tags.Items.NUGGETS_IRON).define('y', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_CORE_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("y y")
                    .pattern("xxx").define('y', Tags.Items.INGOTS_GOLD).define('x', BlockRegistry.ARCANE_STONE).save(consumer);


            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_STONE, 8).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y', SOURCE_GEM).define('x', Tags.Items.STONE).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.IMBUEMENT_BLOCK.asItem(), 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xyx")
                    .pattern("x x")
                    .pattern("xyx").define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('y', Tags.Items.INGOTS_GOLD).save(consumer);

            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_ALTERNATE, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.ARCANE_BRICKS, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_HERRING, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_BASKET, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_MOSAIC, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_CLOVER, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_SLAB, LibBlockNames.ARCANE_STONE);


            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_BASKET, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_CLOVER, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_HERRING, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_MOSAIC, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_ALTERNATING, LibBlockNames.ARCANE_STONE);
            makeStonecutter(consumer, BlockRegistry.ARCANE_STONE, BlockRegistry.AB_SMOOTH_ASHLAR, LibBlockNames.ARCANE_STONE);

            ShapedRecipeBuilder.shaped(ItemsRegistry.BLANK_PARCHMENT, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("yyy")
                    .pattern("yxy")
                    .pattern("yyy").define('x', Items.PAPER).define('y', ItemsRegistry.MAGE_FIBER).save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.SPELL_PARCHMENT, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(SOURCE_GEM_TAG), 4)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.ALLOW_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(Tags.Items.CHESTS), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.DENY_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(Tags.Items.COBBLESTONE), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.WARP_SCROLL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Ingredient.of(Tags.Items.GEMS_LAPIS), 4).requires(ItemsRegistry.BLANK_PARCHMENT).requires(SOURCE_GEM, 4)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.VOLCANIC_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.LAVA_BUCKET).save(consumer);

            ShapelessRecipeBuilder.shapeless(BlockRegistry.LAVA_LILY, 8).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Items.LILY_PAD, 1).requires(SOURCE_GEM, 8)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.ARCHWOOD_PLANK, 4).requires(ARCHWOOD_LOG).save(consumer);
            makeWood(BlockRegistry.VEXING_LOG, BlockRegistry.VEXING_WOOD, 3).save(consumer);
            makeWood(BlockRegistry.CASCADING_LOG, BlockRegistry.CASCADING_WOOD, 3).save(consumer);
            makeWood(BlockRegistry.BLAZING_LOG, BlockRegistry.BLAZING_WOOD, 3).save(consumer);
            makeWood(BlockRegistry.FLOURISHING_LOG, BlockRegistry.FLOURISHING_WOOD, 3).save(consumer);
            shapedWoodenStairs(consumer, BlockRegistry.ARCHWOOD_STAIRS, BlockRegistry.ARCHWOOD_PLANK);
            shapelessWoodenButton(consumer, BlockRegistry.ARCHWOOD_BUTTON, BlockRegistry.ARCHWOOD_PLANK);
            shapedWoodenDoor(consumer, BlockRegistry.ARCHWOOD_DOOR, BlockRegistry.ARCHWOOD_PLANK);
            shapedWoodenFence(consumer, BlockRegistry.ARCHWOOD_FENCE, BlockRegistry.ARCHWOOD_PLANK);
            shapedWoodenFenceGate(consumer, BlockRegistry.ARCHWOOD_FENCE_GATE, BlockRegistry.ARCHWOOD_PLANK);
            shapedWoodenPressurePlate(consumer, BlockRegistry.ARCHWOOD_PPlate, BlockRegistry.ARCHWOOD_PLANK);
            shapedWoodenSlab(consumer, BlockRegistry.ARCHWOOD_SLABS, BlockRegistry.ARCHWOOD_PLANK);

            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_BLUE, BlockRegistry.STRIPPED_AWWOOD_BLUE);
            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_GREEN, BlockRegistry.STRIPPED_AWWOOD_GREEN);
            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_RED, BlockRegistry.STRIPPED_AWWOOD_RED);
            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_PURPLE, BlockRegistry.STRIPPED_AWWOOD_PURPLE);
            shapedWoodenTrapdoor(consumer, BlockRegistry.ARCHWOOD_TRAPDOOR, BlockRegistry.ARCHWOOD_PLANK);

            ShapedRecipeBuilder.shaped(BlockRegistry.SOURCE_GEM_BLOCK, 1)
                    .pattern("xx")
                    .pattern("xx").define('x', SOURCE_GEM).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer);
            shapelessBuilder(ItemsRegistry.SOURCE_GEM, 4).requires(BlockRegistry.SOURCE_GEM_BLOCK, 1).save(consumer, new ResourceLocation(ArsNouveau.MODID, "source_gem_block_2"));
            shapelessBuilder(Items.LEATHER, 1).requires(ItemsRegistry.WILDEN_WING).save(consumer, new ResourceLocation(ArsNouveau.MODID, "wing_to_leather"));
            shapelessBuilder(Items.BONE_MEAL, 3).requires(ItemsRegistry.WILDEN_HORN).save(consumer, new ResourceLocation(ArsNouveau.MODID, "horn_to_bonemeal"));
            shapelessBuilder(Items.ORANGE_DYE, 5).requires(ItemsRegistry.WILDEN_SPIKE).save(consumer, new ResourceLocation(ArsNouveau.MODID, "spike_to_dye"));
            ShapedRecipeBuilder.shaped(Items.ARROW, 32)
                    .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern(" y ")
                    .pattern(" z ")
                    .define('x', ItemsRegistry.WILDEN_SPIKE)
                    .define('y', Items.STICK)
                    .define('z', Items.FEATHER)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "spike_to_arrow"));

            shapelessBuilder(BlockRegistry.POTION_JAR)
                    .requires(BlockRegistry.SOURCE_JAR)
                    .requires(ItemsRegistry.ABJURATION_ESSENCE)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.RITUAL_BLOCK)
                    .requires(BlockRegistry.ARCANE_PEDESTAL)
                    .requires(Recipes.SOURCE_GEM_BLOCK_TAG)
                    .requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 3)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.SCONCE_BLOCK)
                    .requires(Recipes.SOURCE_GEM)
                    .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 2)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.MOONFALL))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(Items.INK_SAC)
                    .requires(Tags.Items.STORAGE_BLOCKS_COAL)
                    .requires(Items.CLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.MOONFALL))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(ItemsRegistry.WILDEN_WING)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "moonfall_2"));

            shapelessBuilder(getRitualItem(RitualLib.SUNRISE))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.DANDELION, 3)
                    .requires(Items.CLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.SUNRISE))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.SUNFLOWER)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "sunrise_2"));

            shapelessBuilder(getRitualItem(RitualLib.DIG))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(Items.IRON_PICKAXE)
                    .requires(Tags.Items.STORAGE_BLOCKS_COAL)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.CLOUDSHAPER))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(Items.FEATHER)
                    .requires(SOURCE_GEM_BLOCK_TAG)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.CHALLENGE))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.EMERALD_BLOCK)
                    .requires(Items.INK_SAC)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.CHALLENGE))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_HORN)
                    .requires(Items.EMERALD)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "challenge_2"));

            shapelessBuilder(getRitualItem(RitualLib.OVERGROWTH))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(ItemsRegistry.MAGE_BLOOM, 3)
                    .requires(ItemsRegistry.EARTH_ESSENCE, 2)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.FERTILITY))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(Items.WHEAT, 3)
                    .requires(Items.GOLDEN_APPLE)
                    .requires(Items.BLAZE_POWDER, 2)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.RESTORATION))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(Items.GOLDEN_APPLE)
                    .requires(ItemsRegistry.ABJURATION_ESSENCE, 1)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.DISINTEGRATION))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.GOLDEN_SWORD, 3)
                    .requires(Items.BOOK, 3)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.WARP))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WARP_SCROLL)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.GREATER_EXPERIENCE_GEM)
                    .requires(ItemsRegistry.EXPERIENCE_GEM, 4)
                    .save(consumer);
            shapelessBuilder(ItemsRegistry.EXPERIENCE_GEM, 4)
                    .requires(ItemsRegistry.GREATER_EXPERIENCE_GEM)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.ALLOW_ITEM_SCROLL)
                    .requires(ItemsRegistry.ALLOW_ITEM_SCROLL)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "clear_allow"));

            shapelessBuilder(ItemsRegistry.DENY_ITEM_SCROLL)
                    .requires(ItemsRegistry.DENY_ITEM_SCROLL)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "clear_deny"));

            shapelessBuilder(getRitualItem(RitualLib.SCRYING))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.SPIDER_EYE, 3)
                    .requires(Items.GLOWSTONE)
                    .requires(Recipes.SOURCE_GEM_BLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.FLIGHT))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_WING, 3)
                    .requires(Ingredient.of(Tags.Items.GEMS_DIAMOND), 2)
                    .requires(Items.ENDER_PEARL)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.MIMIC_ITEM_SCROLL)
                    .requires(ItemsRegistry.ALLOW_ITEM_SCROLL)
                    .requires(Items.CHEST)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.WILDEN_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_HORN, 1)
                    .requires(ItemsRegistry.WILDEN_SPIKE, 1)
                    .requires(ItemsRegistry.WILDEN_WING, 1)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer);
            shapelessBuilder(getRitualItem(RitualLib.WILDEN_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_HORN, 3)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "wilden_summon_2"));
            shapelessBuilder(getRitualItem(RitualLib.WILDEN_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_SPIKE, 3)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "wilden_summon_3"));
            shapelessBuilder(getRitualItem(RitualLib.WILDEN_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_WING, 3)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "wilden_summon_4"));

            shapelessBuilder(getRitualItem(RitualLib.WILDEN_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.EMERALD_BLOCK, 1)
                    .requires(Items.IRON_SWORD, 1)
                    .requires(Items.BOW, 1)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer, new ResourceLocation(ArsNouveau.MODID, "wilden_summon_5"));

            ShapedRecipeBuilder.shaped(BlockRegistry.AS_GOLD_STONE, 8).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y', Tags.Items.NUGGETS_GOLD).define('x', BlockRegistry.ARCANE_STONE).save(consumer);

            STONECUTTER_COUNTER = 1;
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_ALT, LibBlockNames.AS_GOLD_STONE);
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_ASHLAR, LibBlockNames.AS_GOLD_STONE);
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_BASKET, LibBlockNames.AS_GOLD_STONE);
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_CLOVER, LibBlockNames.AS_GOLD_STONE);
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_HERRING, LibBlockNames.AS_GOLD_STONE);
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_MOSAIC, LibBlockNames.AS_GOLD_STONE);
            makeStonecutter(consumer, BlockRegistry.AS_GOLD_STONE, BlockRegistry.AS_GOLD_SLAB, LibBlockNames.AS_GOLD_STONE);
            ShapedRecipeBuilder.shaped(BlockRegistry.ALCHEMICAL_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.BREWING_STAND).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.VITALIC_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.GLISTERING_MELON_SLICE).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.MYCELIAL_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.MUSHROOM_STEW).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.AGRONOMIC_SOURCELINK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.WHEAT).save(consumer);

            shapelessBuilder(ItemsRegistry.SOURCE_BERRY_PIE)
                    .requires(Items.EGG)
                    .requires(Items.SUGAR)
                    .requires(ItemsRegistry.MAGE_BLOOM)
                    .requires(BlockRegistry.SOURCEBERRY_BUSH, 3)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.SOURCE_BERRY_ROLL)
                    .requires(Items.WHEAT, 3)
                    .requires(BlockRegistry.SOURCEBERRY_BUSH)
                    .save(consumer);
            ShapedRecipeBuilder.shaped(BlockRegistry.RELAY).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("g g")
                    .pattern("gMg")
                    .pattern("g g")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('M', SOURCE_GEM_BLOCK)
                    .save(consumer);
            shapelessBuilder(getRitualItem(RitualLib.BINDING))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.BLANK_PARCHMENT)
                    .requires(Items.ENDER_PEARL, 1)
                    .requires(SOURCE_GEM, 3)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.BASIC_SPELL_TURRET).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xzy")
                    .pattern("yyy")
                    .define('z', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                    .define('x', SOURCE_GEM)
                    .define('y', Ingredient.of(Tags.Items.INGOTS_GOLD))
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ARCHWOOD_CHEST).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('y', Items.GOLD_NUGGET)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.SPELL_PRISM).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("gxg")
                    .pattern("xnx")
                    .pattern("gxg")
                    .define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('g', Ingredient.of(Tags.Items.INGOTS_GOLD))
                    .define('n', Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ))
                    .save(consumer);

            shapelessBuilder(Items.CHEST).requires(BlockRegistry.ARCHWOOD_CHEST).save(consumer, new ResourceLocation(ArsNouveau.MODID, "archwood_to_chest"));

            shapelessBuilder(getRitualItem(RitualLib.AWAKENING))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(BlockRegistry.BLAZING_SAPLING)
                    .requires(BlockRegistry.CASCADING_SAPLING)
                    .requires(BlockRegistry.FLOURISHING_SAPLING)
                    .requires(BlockRegistry.VEXING_SAPLING)
                    .requires(SOURCE_GEM, 4)
                    .save(consumer);

            shapelessBuilder(Items.PINK_DYE, 2).requires(ItemsRegistry.MAGE_BLOOM, 2).save(consumer, new ResourceLocation(ArsNouveau.MODID, "magebloom_to_pink"));
            shapelessBuilder(Items.PURPLE_DYE).requires(BlockRegistry.SOURCEBERRY_BUSH).save(consumer, new ResourceLocation(ArsNouveau.MODID, "sourceberry_to_purple"));

            shapelessBuilder(Items.WATER_BUCKET).requires(ItemsRegistry.WATER_ESSENCE).requires(Items.BUCKET).save(consumer, new ResourceLocation(ArsNouveau.MODID, "water_essence_to_bucket"));
            shapelessBuilder(Items.FIRE_CHARGE, 3).requires(ItemsRegistry.FIRE_ESSENCE).requires(Items.GUNPOWDER).requires(Items.COAL).save(consumer, new ResourceLocation(ArsNouveau.MODID, "fire_essence_to_charge"));


            ShapedRecipeBuilder.shaped(ItemsRegistry.DOWSING_ROD).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern("a a")
                    .define('x', Tags.Items.INGOTS_GOLD)
                    .define('a', BlockRegistry.ARCHWOOD_PLANK)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.ANNOTATED_CODEX).requires(ItemsRegistry.SPELL_PARCHMENT).requires(Items.LEATHER).save(consumer);

        }
    }

    public Item getRitualItem(String id) {
        return ArsNouveauAPI.getInstance().getRitualItemMap().get(id);
    }

    public static ShapedRecipeBuilder makeWood(ItemLike logs, ItemLike wood, int count) {
        return ShapedRecipeBuilder.shaped(wood, count).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .pattern("xx ")
                .pattern("xx ").define('x', logs);
    }

    private static void shapedWoodenTrapdoor(Consumer<FinishedRecipe> recipeConsumer, ItemLike trapdoor, ItemLike input) {
        ShapedRecipeBuilder.shaped(trapdoor, 2).define('#', input).pattern("###").pattern("###").group("wooden_trapdoor")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(recipeConsumer);
    }

    public static void shapedWoodenStairs(Consumer<FinishedRecipe> recipeConsumer, ItemLike stairs, ItemLike input) {
        ShapedRecipeBuilder.shaped(stairs, 4)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###").unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);

    }

    private static void shapelessWoodenButton(Consumer<FinishedRecipe> recipeConsumer, ItemLike button, ItemLike input) {
        ShapelessRecipeBuilder.shapeless(button).requires(input)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void strippedLogToWood(Consumer<FinishedRecipe> recipeConsumer, ItemLike stripped, ItemLike output) {
        ShapedRecipeBuilder.shaped(output, 3).define('#', stripped).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenDoor(Consumer<FinishedRecipe> recipeConsumer, ItemLike door, ItemLike input) {
        ShapedRecipeBuilder.shaped(door, 3).define('#', input).pattern("##").pattern("##").pattern("##").group("wooden_door")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenFence(Consumer<FinishedRecipe> recipeConsumer, ItemLike fence, ItemLike input) {
        ShapedRecipeBuilder.shaped(fence, 3).define('#', Items.STICK).define('W', input).pattern("W#W").pattern("W#W").group("wooden_fence")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenFenceGate(Consumer<FinishedRecipe> recipeConsumer, ItemLike fenceGate, ItemLike input) {
        ShapedRecipeBuilder.shaped(fenceGate).define('#', Items.STICK).define('W', input).pattern("#W#").pattern("#W#").group("wooden_fence_gate")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenPressurePlate(Consumer<FinishedRecipe> recipeConsumer, ItemLike pressurePlate, ItemLike input) {
        ShapedRecipeBuilder.shaped(pressurePlate).define('#', input).pattern("##").group("wooden_pressure_plate")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenSlab(Consumer<FinishedRecipe> recipeConsumer, ItemLike slab, ItemLike input) {
        ShapedRecipeBuilder.shaped(slab, 6).define('#', input).pattern("###").group("wooden_slab")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result) {
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result, int resultCount) {
        return ShapelessRecipeBuilder.shapeless(result, resultCount).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }

    private static int STONECUTTER_COUNTER = 0;

    public static void makeStonecutter(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output, String reg) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), output).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer, new ResourceLocation(ArsNouveau.MODID, reg + "_" + STONECUTTER_COUNTER));
        STONECUTTER_COUNTER++;
    }

    public static void makeArmor(String prefix, Consumer<FinishedRecipe> consumer, Item material) {
        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_boots")))
                .pattern("   ")
                .pattern("x x")
                .pattern("x x").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_leggings")))
                .pattern("xxx")
                .pattern("x x")
                .pattern("x x").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_hood")))
                .pattern("xxx")
                .pattern("x x")
                .pattern("   ").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_robes")))
                .pattern("x x")
                .pattern("xxx")
                .pattern("xxx").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(consumer);
    }
}
