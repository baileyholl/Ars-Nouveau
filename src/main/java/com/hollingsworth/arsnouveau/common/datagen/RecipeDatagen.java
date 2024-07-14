package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeDatagen extends RecipeProvider {

    public static Ingredient SOURCE_GEM = Ingredient.of(ItemTagProvider.SOURCE_GEM_TAG);
    public static Ingredient SOURCE_GEM_BLOCK = Ingredient.of(ItemTagProvider.SOURCE_GEM_BLOCK_TAG);
    public static Ingredient ARCHWOOD_LOG = Ingredient.of(ItemTagProvider.ARCHWOOD_LOG_TAG);
    public static Ingredient WILDEN_DROP = Ingredient.of(ItemTagProvider.WILDEN_DROP_TAG);
    public static Ingredient SUMMON_SHARDS = Ingredient.of(ItemTagProvider.SUMMON_SHARDS_TAG);


    public RecipeOutput consumer;

    public RecipeDatagen(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        {
            this.consumer = pRecipeOutput;
            Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.NOVICE_SPELLBOOK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Items.BOOK).requires(Items.IRON_SHOVEL).requires(Items.IRON_PICKAXE).requires(Items.IRON_AXE).requires(Items.IRON_SWORD).save(consumer);

            shapelessBuilder(ItemsRegistry.STABLE_WARP_SCROLL).requires(ItemsRegistry.STABLE_WARP_SCROLL).save(consumer, ArsNouveau.prefix("reset_stable_warp_scroll"));

            shapelessBuilder(ItemsRegistry.WARP_SCROLL).requires(ItemsRegistry.WARP_SCROLL).save(consumer, ArsNouveau.prefix("reset_warp_scroll"));


            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.WORN_NOTEBOOK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Ingredient.of(Tags.Items.GEMS_LAPIS), 1)
                    .requires(Items.BOOK).save(consumer);


            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.MAGE_FIBER, 4).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MAGE_BLOOM)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.RUNIC_CHALK, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE).requires(Items.BONE_MEAL).requires(ItemsRegistry.MAGE_FIBER)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.SOURCE_JAR).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("yyy")
                    .pattern("x x")
                    .pattern("yyy").define('x', Tags.Items.GLASS_BLOCKS).define('y', BlockRegistry.ARCHWOOD_SLABS).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ARCANE_PEDESTAL.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xzx")
                    .pattern("yxy")
                    .pattern("yxy").define('x', SOURCESTONE).define('y', Tags.Items.NUGGETS_GOLD).define('z', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ENCHANTING_APP_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("nsn")
                    .pattern("gdg")
                    .pattern("nsn").define('n', Tags.Items.NUGGETS_GOLD)
                    .define('s', SOURCESTONE)
                    .define('d', Tags.Items.GEMS_DIAMOND)
                    .define('g', Tags.Items.INGOTS_GOLD).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsRegistry.MUNDANE_BELT).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("   ")
                    .pattern("xyx")
                    .pattern(" x ")
                    .define('x', Tags.Items.LEATHERS)
                    .define('y', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsRegistry.RING_OF_POTENTIAL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', Tags.Items.NUGGETS_IRON).define('y', SOURCE_GEM).save(consumer);


            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.SCRIBES_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("y y")
                    .pattern("z z").define('x', Ingredient.of(BlockRegistry.ARCHWOOD_SLABS))
                    .define('y', Tags.Items.NUGGETS_GOLD)
                    .define('z', ARCHWOOD_LOG).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsRegistry.DULL_TRINKET).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern("xyx")
                    .pattern(" x ").define('x', Tags.Items.NUGGETS_IRON).define('y', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ARCANE_CORE_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("yzy")
                    .pattern("xxx").define('y', Tags.Items.INGOTS_GOLD).define('x', SOURCESTONE).define('z', SOURCE_GEM).save(consumer);


            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SOURCESTONE, 8).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y', SOURCE_GEM).define('x', Tags.Items.STONES).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.IMBUEMENT_BLOCK.asItem(), 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xyx")
                    .pattern("x x")
                    .pattern("xyx").define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('y', Tags.Items.INGOTS_GOLD).save(consumer);


            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsRegistry.BLANK_PARCHMENT, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("yyy")
                    .pattern("yxy")
                    .pattern("yyy").define('x', Items.PAPER).define('y', ItemsRegistry.MAGE_FIBER).save(consumer);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.ALLOW_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(Tags.Items.CHESTS), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.DENY_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(Tags.Items.COBBLESTONES), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemsRegistry.WARP_SCROLL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Ingredient.of(Tags.Items.GEMS_LAPIS), 4).requires(ItemsRegistry.BLANK_PARCHMENT).requires(SOURCE_GEM, 4)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.VOLCANIC_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.LAVA_BUCKET).save(consumer);

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

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.SOURCE_GEM_BLOCK, 1)
                    .pattern("xx")
                    .pattern("xx").define('x', SOURCE_GEM).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer);
            shapelessBuilder(ItemsRegistry.SOURCE_GEM, 4).requires(BlockRegistry.SOURCE_GEM_BLOCK, 1).save(consumer, ArsNouveau.prefix( "source_gem_block_2"));
            shapelessBuilder(Items.LEATHER, 1).requires(ItemsRegistry.WILDEN_WING).save(consumer, ArsNouveau.prefix( "wing_to_leather"));
            shapelessBuilder(Items.BONE_MEAL, 3).requires(ItemsRegistry.WILDEN_HORN).save(consumer, ArsNouveau.prefix( "horn_to_bonemeal"));
            shapelessBuilder(Items.ORANGE_DYE, 5).requires(ItemsRegistry.WILDEN_SPIKE).save(consumer, ArsNouveau.prefix( "spike_to_dye"));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.ARROW, 32)
                    .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern(" y ")
                    .pattern(" z ")
                    .define('x', ItemsRegistry.WILDEN_SPIKE)
                    .define('y', Items.STICK)
                    .define('z', Items.FEATHER)
                    .save(consumer, ArsNouveau.prefix( "spike_to_arrow"));

            shapelessBuilder(BlockRegistry.POTION_JAR)
                    .requires(BlockRegistry.SOURCE_JAR)
                    .requires(ItemsRegistry.ABJURATION_ESSENCE)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.POTION_JAR).requires(BlockRegistry.POTION_JAR).save(consumer, ArsNouveau.prefix("potion_jar_empty"));

            shapelessBuilder(BlockRegistry.RITUAL_BLOCK)
                    .requires(BlockRegistry.ARCANE_PEDESTAL.get())
                    .requires(SOURCE_GEM_BLOCK)
                    .requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 3)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.GOLD_SCONCE_BLOCK)
                    .requires(RecipeDatagen.SOURCE_GEM)
                    .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 2)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.SOURCESTONE_SCONCE_BLOCK)
                    .requires(RecipeDatagen.SOURCE_GEM)
                    .requires(Ingredient.of(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)), 2)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.ARCHWOOD_SCONCE_BLOCK)
                    .requires(RecipeDatagen.SOURCE_GEM)
                    .requires(Ingredient.of(BlockRegistry.ARCHWOOD_PLANK), 2)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.POLISHED_SCONCE_BLOCK)
                    .requires(BlockRegistry.SOURCESTONE_SCONCE_BLOCK)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.SOURCESTONE_SCONCE_BLOCK).requires(BlockRegistry.POLISHED_SCONCE_BLOCK).save(consumer, ArsNouveau.prefix( "polished_source_sconce"));

            shapelessBuilder(getRitualItem(RitualLib.MOONFALL))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(Items.INK_SAC)
                    .requires(Tags.Items.STORAGE_BLOCKS_COAL)
                    .requires(Items.CLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.MOONFALL))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(ItemsRegistry.WILDEN_WING)
                    .save(consumer, ArsNouveau.prefix( "moonfall_2"));

            shapelessBuilder(getRitualItem(RitualLib.SUNRISE))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.DANDELION, 3)
                    .requires(Items.CLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.SUNRISE))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.SUNFLOWER)
                    .save(consumer, ArsNouveau.prefix( "sunrise_2"));

            shapelessBuilder(getRitualItem(RitualLib.DIG))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(Items.IRON_PICKAXE)
                    .requires(Tags.Items.STORAGE_BLOCKS_COAL)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.CLOUDSHAPER))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(Items.FEATHER)
                    .requires(SOURCE_GEM_BLOCK)
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
                    .save(consumer, ArsNouveau.prefix( "challenge_2"));

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
                    .save(consumer, ArsNouveau.prefix( "clear_allow"));

            shapelessBuilder(ItemsRegistry.DENY_ITEM_SCROLL)
                    .requires(ItemsRegistry.DENY_ITEM_SCROLL)
                    .save(consumer, ArsNouveau.prefix( "clear_deny"));

            shapelessBuilder(getRitualItem(RitualLib.SCRYING))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.SPIDER_EYE, 3)
                    .requires(Items.GLOWSTONE)
                    .requires(RecipeDatagen.SOURCE_GEM_BLOCK)
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
                    .requires(WILDEN_DROP, 3)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.WILDEN_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.EMERALD_BLOCK, 1)
                    .requires(Items.IRON_SWORD, 1)
                    .requires(Items.BOW, 1)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer, ArsNouveau.prefix( "wilden_summon_alt"));

            shapelessBuilder(getRitualItem(RitualLib.ANIMAL_SUMMON))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(SUMMON_SHARDS, 3)
                    .requires(Items.LAPIS_BLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.GRAVITY))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(ItemsRegistry.AIR_ESSENCE)
                    .requires(ItemsRegistry.EARTH_ESSENCE)
                    .requires(Items.FEATHER)
                    .requires(Items.ANVIL)
                    .save(consumer);


            STONECUTTER_COUNTER = 1;
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ALCHEMICAL_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.BREWING_STAND).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.VITALIC_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.GLISTERING_MELON_SLICE).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.MYCELIAL_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.MUSHROOM_STEW).save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.AGRONOMIC_SOURCELINK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
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
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.RELAY).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
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

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.BASIC_SPELL_TURRET).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xzy")
                    .pattern("yyy")
                    .define('z', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                    .define('x', SOURCE_GEM)
                    .define('y', Ingredient.of(Tags.Items.INGOTS_GOLD))
                    .save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ARCHWOOD_CHEST).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('y', Items.GOLD_NUGGET)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.SPELL_PRISM).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("gxg")
                    .pattern("xnx")
                    .pattern("gxg")
                    .define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('g', Ingredient.of(Tags.Items.INGOTS_GOLD))
                    .define('n', Ingredient.of(ItemTagProvider.STORAGE_BLOCKS_QUARTZ))
                    .save(consumer);

            shapelessBuilder(Items.CHEST).requires(BlockRegistry.ARCHWOOD_CHEST).save(consumer, ArsNouveau.prefix( "archwood_to_chest"));

            shapelessBuilder(getRitualItem(RitualLib.AWAKENING))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(BlockRegistry.BLAZING_SAPLING)
                    .requires(BlockRegistry.CASCADING_SAPLING)
                    .requires(BlockRegistry.FLOURISHING_SAPLING)
                    .requires(BlockRegistry.VEXING_SAPLING)
                    .requires(SOURCE_GEM, 4)
                    .save(consumer);

            shapelessBuilder(Items.PINK_DYE, 2).requires(ItemsRegistry.MAGE_BLOOM, 2).save(consumer, ArsNouveau.prefix( "magebloom_to_pink"));
            shapelessBuilder(Items.PURPLE_DYE).requires(BlockRegistry.SOURCEBERRY_BUSH).save(consumer, ArsNouveau.prefix( "sourceberry_to_purple"));

            shapelessBuilder(Items.WATER_BUCKET).requires(ItemsRegistry.WATER_ESSENCE).requires(Items.BUCKET).save(consumer, ArsNouveau.prefix( "water_essence_to_bucket"));
            shapelessBuilder(Items.FIRE_CHARGE, 3).requires(ItemsRegistry.FIRE_ESSENCE).requires(Items.GUNPOWDER).requires(Items.COAL).save(consumer, ArsNouveau.prefix( "fire_essence_to_charge"));


            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsRegistry.DOWSING_ROD).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern("a a")
                    .define('x', Tags.Items.INGOTS_GOLD)
                    .define('a', BlockRegistry.ARCHWOOD_PLANK)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.ANNOTATED_CODEX).requires(ItemsRegistry.BLANK_PARCHMENT).requires(Items.LEATHER).save(consumer);
            shapelessBuilder(Items.POWDER_SNOW_BUCKET).requires(ItemsRegistry.AIR_ESSENCE).requires(Items.BUCKET).requires(Items.SNOW_BLOCK).save(consumer, ArsNouveau.prefix( "air_essence_to_snow_bucket"));
            shapedBuilder(Items.SOUL_SAND, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Tags.Items.SANDS)
                    .define('y', ItemsRegistry.CONJURATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "conjuration_essence_to_soul_sand"));
            shapedBuilder(Items.END_STONE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Tags.Items.STONES)
                    .define('y', ItemsRegistry.CONJURATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "conjuration_essence_to_end_stone"));

            shapelessBuilder(Items.OBSIDIAN).requires(Items.LAVA_BUCKET).requires(ItemsRegistry.WATER_ESSENCE).save(consumer, ArsNouveau.prefix( "water_essence_to_obsidian"));
            shapedBuilder(Items.MAGMA_BLOCK, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Tags.Items.STONES)
                    .define('y', ItemsRegistry.FIRE_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "fire_essence_to_magma_block"));

            shapedBuilder(Items.GRANITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.DIORITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_granite"));
            shapedBuilder(Items.ANDESITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.GRANITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_andesite"));
            shapedBuilder(Items.DIORITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.ANDESITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_diorite"));

            shapedBuilder(Items.MYCELIUM, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.GRASS_BLOCK)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_mycelium"));

            shapedBuilder(Items.MOSS_BLOCK, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.MYCELIUM)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_moss_block"));

            shapedBuilder(Items.GRASS_BLOCK, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.MOSS_BLOCK)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_grass_block"));

            shapedBuilder(Items.TUFF, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.DEEPSLATE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_tuff"));

            shapedBuilder(Items.CALCITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.TUFF)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_calcite"));

            shapedBuilder(Items.DEEPSLATE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.CALCITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix( "manipulation_essence_to_deepslate"));

            shapelessBuilder(BlockRegistry.CASCADING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.BLAZING_SAPLING).save(consumer, ArsNouveau.prefix( "manipulation_essence_to_cascading_sapling"));

            shapelessBuilder(BlockRegistry.FLOURISHING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.CASCADING_SAPLING).save(consumer, ArsNouveau.prefix( "manipulation_essence_to_flourishing_sapling"));

            shapelessBuilder(BlockRegistry.VEXING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.FLOURISHING_SAPLING).save(consumer, ArsNouveau.prefix( "manipulation_essence_to_vexing_sapling"));

            shapelessBuilder(BlockRegistry.BLAZING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.VEXING_SAPLING).save(consumer, ArsNouveau.prefix( "manipulation_essence_to_blazin_sapling"));

            shapedBuilder(BlockRegistry.ORANGE_SBED)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', ItemsRegistry.MAGE_FIBER)
                    .define('y', Items.FEATHER)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.RED_SBED)
                    .requires(ItemTagProvider.SUMMON_BED_ITEMS)
                    .requires(Tags.Items.DYES_RED).save(consumer);

            shapelessBuilder(BlockRegistry.GREEN_SBED)
                    .requires(ItemTagProvider.SUMMON_BED_ITEMS)
                    .requires(Tags.Items.DYES_GREEN).save(consumer);

            shapelessBuilder(BlockRegistry.BLUE_SBED)
                    .requires(ItemTagProvider.SUMMON_BED_ITEMS)
                    .requires(Tags.Items.DYES_BLUE).save(consumer);

            shapelessBuilder(BlockRegistry.PURPLE_SBED)
                    .requires(ItemTagProvider.SUMMON_BED_ITEMS)
                    .requires(Tags.Items.DYES_PURPLE).save(consumer);

            shapelessBuilder(BlockRegistry.YELLOW_SBED)
                    .requires(ItemTagProvider.SUMMON_BED_ITEMS)
                    .requires(Tags.Items.DYES_YELLOW).save(consumer);
            shapelessBuilder(BlockRegistry.SCRYERS_CRYSTAL)
                    .requires(Items.ENDER_EYE)
                    .requires(RecipeDatagen.SOURCE_GEM).save(consumer);

            shapelessBuilder(ItemsRegistry.BLANK_PARCHMENT).requires(ItemsRegistry.SCRYER_SCROLL).save(consumer, ArsNouveau.prefix( "scry_to_blank_parchment"));
            shapelessBuilder(ItemsRegistry.BLANK_PARCHMENT).requires(ItemsRegistry.SPELL_PARCHMENT).save(consumer, ArsNouveau.prefix( "wipe_spell_parchment"));
            shapedBuilder(ItemsRegistry.STARBUNCLE_SHADES)
                    .pattern("xyx")
                    .define('x', Items.TINTED_GLASS)
                    .define('y', ItemsRegistry.SOURCE_GEM)
                    .save(consumer);
            // stonecutter for sourcestone
            for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
                if(s.equals(LibBlockNames.SOURCESTONE))
                    continue;
                makeStonecutter(consumer, BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), BlockRegistry.getBlock(s), LibBlockNames.SOURCESTONE);
                shapelessBuilder(SOURCESTONE).requires(BlockRegistry.getBlock(s)).save(consumer, ArsNouveau.prefix( s + "_to_sourcestone"));

                Block stair = BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s + "_stairs"));
                Block slab = BuiltInRegistries.BLOCK.get(ArsNouveau.prefix( s + "_slab"));
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(BlockRegistry.getBlock(s)), RecipeCategory.BUILDING_BLOCKS, stair)
                        .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                        .save(consumer, ArsNouveau.prefix( s + "_stonecutter_stair"));

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(BlockRegistry.getBlock(s)),RecipeCategory.BUILDING_BLOCKS, slab, 2)
                        .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                        .save(consumer, ArsNouveau.prefix( s + "_stone_cutterslab"));

                shapedWoodenStairs(consumer, stair, BlockRegistry.getBlock(s), s + "_stairs");
                shapedWoodenSlab(consumer, slab, BlockRegistry.getBlock(s), s + "_slab");

            }

            shapelessBuilder(getRitualItem(RitualLib.HARVEST)).requires(BlockRegistry.FLOURISHING_LOG).requires(ItemsRegistry.EARTH_ESSENCE).requires(Items.IRON_HOE).save(consumer);
            shapedBuilder(ItemsRegistry.WIXIE_HAT)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', ItemsRegistry.MAGE_FIBER)
                    .define('y', Tags.Items.INGOTS_GOLD).save(consumer);

            shapedBuilder(ItemsRegistry.BLANK_THREAD, 1)
                    .pattern("xxx")
                    .pattern("yyy")
                    .pattern("xxx")
                    .define('x', ItemsRegistry.MAGE_FIBER)
                    .define('y', Tags.Items.NUGGETS_GOLD).save(consumer);

            shapedBuilder(BlockRegistry.ALTERATION_TABLE)
                    .pattern(" x ")
                    .pattern("xyx")
                    .pattern(" x ")
                    .define('x', ItemsRegistry.MAGE_FIBER)
                    .define('y', BlockRegistry.SCRIBES_BLOCK).save(consumer);
            shapedBuilder(BlockRegistry.MOB_JAR)
                    .pattern("yyy")
                    .pattern("x x")
                    .pattern("xxx").define('y', BlockRegistry.ARCHWOOD_SLABS).define('x', Ingredient.of(Tags.Items.GLASS_BLOCKS)).save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.CONTAINMENT)).requires(BlockRegistry.VEXING_LOG).requires(ItemsRegistry.MANIPULATION_ESSENCE).requires(Items.GLASS_BOTTLE, 3).save(consumer);
            shapedBuilder(BlockRegistry.VOID_PRISM)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y', BlockRegistry.SPELL_PRISM).define('x', Ingredient.of(Tags.Items.OBSIDIANS)).save(consumer);

            shapedBuilder(BlockRegistry.MAGEBLOOM_BLOCK)
                    .pattern("xx ")
                    .pattern("xx ")
                    .define('x', ItemsRegistry.MAGE_FIBER).save(consumer);

            shapelessBuilder(ItemsRegistry.MAGE_FIBER, 4).requires(BlockRegistry.MAGEBLOOM_BLOCK).save(consumer, ArsNouveau.prefix("magebloom_block_to_magebloom"));

            shapedBuilder(BlockRegistry.FALSE_WEAVE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', BlockRegistry.MAGEBLOOM_BLOCK).define('y', ItemsRegistry.AIR_ESSENCE).save(consumer);

            shapedBuilder(BlockRegistry.GHOST_WEAVE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', BlockRegistry.MAGEBLOOM_BLOCK).define('y', ItemsRegistry.ABJURATION_ESSENCE).save(consumer);

            shapedBuilder(BlockRegistry.MIRROR_WEAVE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', BlockRegistry.MAGEBLOOM_BLOCK).define('y', ItemsRegistry.CONJURATION_ESSENCE).save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.PLAINS)).requires(BlockRegistry.FLOURISHING_LOG).requires(Blocks.GRASS_BLOCK).requires(ItemsRegistry.EARTH_ESSENCE).save(consumer);
            shapelessBuilder(getRitualItem(RitualLib.FORESTATION)).requires(BlockRegistry.FLOURISHING_LOG).requires(BlockRegistry.MENDOSTEEN_POD).requires(ItemsRegistry.EARTH_ESSENCE).save(consumer);
            shapelessBuilder(getRitualItem(RitualLib.FLOWERING)).requires(BlockRegistry.FLOURISHING_LOG).requires(Items.POPPY, 3).requires(Items.DANDELION, 3).requires(ItemsRegistry.EARTH_ESSENCE).save(consumer);
            shapelessBuilder(getRitualItem(RitualLib.DESERT)).requires(BlockRegistry.BLAZING_LOG).requires(Blocks.SAND).requires(ItemsRegistry.EARTH_ESSENCE).save(consumer);

            shapedBuilder(BlockRegistry.MAGELIGHT_TORCH, 1)
                    .pattern("   ")
                    .pattern("xyx")
                    .pattern(" x ").define('x', Tags.Items.NUGGETS_GOLD).define('y', SOURCE_GEM).save(consumer);

            shapelessBuilder(BlockRegistry.ARCANE_PLATFORM).requires(BlockRegistry.ARCANE_PEDESTAL).save(consumer);
            shapelessBuilder(BlockRegistry.ARCANE_PEDESTAL).requires(BlockRegistry.ARCANE_PLATFORM).save(consumer, ArsNouveau.prefix( "platform_to_pedestal"));

            shapedBuilder(BlockRegistry.SKY_WEAVE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', BlockRegistry.MAGEBLOOM_BLOCK).define('y', ItemsRegistry.MANIPULATION_ESSENCE).save(consumer);

            shapedBuilder(BlockRegistry.ITEM_DETECTOR)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("zzz").define('x', Tags.Items.GLASS_BLOCKS).define('y', Blocks.OBSERVER).define('z', BlockRegistry.ARCHWOOD_PLANK).save(consumer);
            shapedBuilder(BlockRegistry.REPOSITORY)
                    .pattern("yxy")
                    .pattern("x x")
                    .pattern("yxy").define('x', ARCHWOOD_LOG).define('y', Tags.Items.NUGGETS_GOLD).save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.SANCTUARY)).requires(BlockRegistry.CASCADING_LOG).requires(ItemsRegistry.WATER_ESSENCE).requires(Items.SEA_LANTERN).save(consumer);
            shapelessBuilder(BlockRegistry.ROTATING_TURRET).requires(BlockRegistry.BASIC_SPELL_TURRET).save(consumer);
            shapelessBuilder(BlockRegistry.BASIC_SPELL_TURRET).requires(BlockRegistry.ROTATING_TURRET).save(consumer, ArsNouveau.prefix( "rotating_turret_to_basic_spell_turret"));
            shapelessBuilder(ItemsRegistry.STARBUNCLE_SHARD).requires(ItemsRegistry.STARBUNCLE_SHARD).save(consumer, ArsNouveau.prefix( "wipe_starby_shard"));
            shapedBuilder(BlockRegistry.REDSTONE_RELAY)
                    .pattern("gxg")
                    .pattern("gMg")
                    .pattern("gxg")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('M', SOURCE_GEM_BLOCK)
                    .define('x', Tags.Items.DUSTS_REDSTONE)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.SOURCEBERRY_SACK).requires(BlockRegistry.SOURCEBERRY_BUSH, 9).save(consumer);
            shapelessBuilder(BlockRegistry.SOURCEBERRY_BUSH, 9).requires(BlockRegistry.SOURCEBERRY_SACK).save(consumer, ArsNouveau.prefix( "sourceberry_sack_to_bush"));
        }
    }

    public static RitualTablet getRitualItem(String name) {
        return RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix( name));
    }

    public ShapedRecipeBuilder shapedBuilder(ItemLike item) {
        return shapedBuilder(item, 1);
    }

    public ShapedRecipeBuilder shapedBuilder(ItemLike result, int count) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, count).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }


    public static ShapedRecipeBuilder makeWood(ItemLike logs, ItemLike wood, int count) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, wood, count).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .pattern("xx ")
                .pattern("xx ").define('x', logs);
    }

    private static void shapedWoodenTrapdoor(RecipeOutput recipeConsumer, ItemLike trapdoor, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, trapdoor, 2).define('#', input).pattern("###").pattern("###").group("wooden_trapdoor")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(recipeConsumer);
    }
    public static void shapedWoodenStairs(RecipeOutput recipeConsumer, ItemLike stairs, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stairs, 4)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###").unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);

    }
    public static void shapedWoodenStairs(RecipeOutput recipeConsumer, ItemLike stairs, ItemLike input, String name) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,stairs, 4)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###").unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer, ArsNouveau.prefix( name));

    }

    private static void shapelessWoodenButton(RecipeOutput recipeConsumer, ItemLike button, ItemLike input) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, button).requires(input)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void strippedLogToWood(RecipeOutput recipeConsumer, ItemLike stripped, ItemLike output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 3).define('#', stripped).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenDoor(RecipeOutput recipeConsumer, ItemLike door, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, door, 3).define('#', input).pattern("##").pattern("##").pattern("##").group("wooden_door")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenFence(RecipeOutput recipeConsumer, ItemLike fence, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, fence, 3).define('#', Items.STICK).define('W', input).pattern("W#W").pattern("W#W").group("wooden_fence")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenFenceGate(RecipeOutput recipeConsumer, ItemLike fenceGate, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, fenceGate).define('#', Items.STICK).define('W', input).pattern("#W#").pattern("#W#").group("wooden_fence_gate")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenPressurePlate(RecipeOutput recipeConsumer, ItemLike pressurePlate, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pressurePlate).define('#', input).pattern("##").group("wooden_pressure_plate")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenSlab(RecipeOutput recipeConsumer, ItemLike slab, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6).define('#', input).pattern("###").group("wooden_slab")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private static void shapedWoodenSlab(RecipeOutput recipeConsumer, ItemLike slab, ItemLike input, String name) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6).define('#', input).pattern("###").group("wooden_slab")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer, ArsNouveau.prefix( name));
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result) {
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result, int resultCount) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result, resultCount).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }

    private static int STONECUTTER_COUNTER = 0;

    public static void makeStonecutter(RecipeOutput consumer, ItemLike input, ItemLike output, String reg) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.DECORATIONS, output).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer, ArsNouveau.prefix( reg + "_" + STONECUTTER_COUNTER));
        STONECUTTER_COUNTER++;
    }
}
