package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeDatagen extends RecipeProvider {

    public static Ingredient SOURCE_GEM = tagIngredient(ItemTagProvider.SOURCE_GEM_TAG);
    public static Ingredient SOURCE_GEM_BLOCK = tagIngredient(ItemTagProvider.SOURCE_GEM_BLOCK_TAG);
    public static Ingredient ARCHWOOD_LOG = tagIngredient(ItemTagProvider.ARCHWOOD_LOG_TAG);
    public static Ingredient WILDEN_DROP = tagIngredient(ItemTagProvider.WILDEN_DROP_TAG);
    public static Ingredient SUMMON_SHARDS = tagIngredient(ItemTagProvider.SUMMON_SHARDS_TAG);

    public RecipeDatagen(HolderLookup.Provider pRegistries, RecipeOutput pRecipeOutput) {
        super(pRegistries, pRecipeOutput);
    }

    /** Creates a named HolderSet from a TagKey using BuiltInRegistries as owner. */
    private static Ingredient tagIngredient(TagKey<Item> tag) {
        return Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, tag));
    }

    @Override
    protected void buildRecipes() {
        {
            RecipeOutput consumer = this.output;
            Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.NOVICE_SPELLBOOK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(Items.BOOK).requires(Items.IRON_SHOVEL).requires(Items.IRON_PICKAXE).requires(Items.IRON_AXE).requires(Items.IRON_SWORD).save(consumer);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.NOVICE_SPELLBOOK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.WORN_NOTEBOOK).requires(Items.IRON_SHOVEL).requires(Items.IRON_PICKAXE).requires(Items.IRON_AXE).requires(Items.IRON_SWORD).save(consumer, ArsNouveau.prefix("novice_spellbook_alt").toString());
            shapelessBuilder(ItemsRegistry.STABLE_WARP_SCROLL).requires(ItemsRegistry.STABLE_WARP_SCROLL).save(consumer, ArsNouveau.prefix("reset_stable_warp_scroll").toString());

            shapelessBuilder(ItemsRegistry.WARP_SCROLL).requires(ItemsRegistry.WARP_SCROLL).save(consumer, ArsNouveau.prefix("reset_warp_scroll").toString());


            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.WORN_NOTEBOOK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(tag(Tags.Items.GEMS_LAPIS), 1)
                    .requires(Items.BOOK).save(consumer);


            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.MAGE_FIBER, 4).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MAGE_BLOOM)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.RUNIC_CHALK, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE).requires(Items.BONE_MEAL).requires(ItemsRegistry.MAGE_FIBER)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.SOURCE_JAR).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("yyy")
                    .pattern("x x")
                    .pattern("yyy").define('x', Tags.Items.GLASS_BLOCKS).define('y', BlockRegistry.ARCHWOOD_SLABS).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.ARCANE_PEDESTAL.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xzx")
                    .pattern("yxy")
                    .pattern("yxy").define('x', SOURCESTONE).define('y', Tags.Items.NUGGETS_GOLD).define('z', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.ENCHANTING_APP_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("nsn")
                    .pattern("gdg")
                    .pattern("nsn").define('n', Tags.Items.NUGGETS_GOLD)
                    .define('s', SOURCESTONE)
                    .define('d', Tags.Items.GEMS_DIAMOND)
                    .define('g', Tags.Items.INGOTS_GOLD).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ItemsRegistry.MUNDANE_BELT).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("   ")
                    .pattern("xyx")
                    .pattern(" x ")
                    .define('x', Tags.Items.LEATHERS)
                    .define('y', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ItemsRegistry.RING_OF_POTENTIAL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', Tags.Items.NUGGETS_IRON).define('y', SOURCE_GEM).save(consumer);


            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.SCRIBES_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("y y")
                    .pattern("z z").define('x', Ingredient.of(BlockRegistry.ARCHWOOD_SLABS))
                    .define('y', Tags.Items.NUGGETS_GOLD)
                    .define('z', ARCHWOOD_LOG).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ItemsRegistry.DULL_TRINKET).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern("xyx")
                    .pattern(" x ").define('x', Tags.Items.NUGGETS_IRON).define('y', SOURCE_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.ARCANE_CORE_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("yzy")
                    .pattern("xxx").define('y', Tags.Items.INGOTS_GOLD).define('x', SOURCESTONE).define('z', SOURCE_GEM).save(consumer);


            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, SOURCESTONE, 8).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y', SOURCE_GEM).define('x', Tags.Items.STONES).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.IMBUEMENT_BLOCK.asItem(), 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xyx")
                    .pattern("x x")
                    .pattern("xyx").define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('y', Tags.Items.INGOTS_GOLD).save(consumer);


            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ItemsRegistry.BLANK_PARCHMENT, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("yyy")
                    .pattern("yxy")
                    .pattern("yyy").define('x', Items.PAPER).define('y', ItemsRegistry.MAGE_FIBER).save(consumer);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.ALLOW_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(tag(Tags.Items.CHESTS), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.DENY_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(tag(Tags.Items.COBBLESTONES), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemsRegistry.WARP_SCROLL).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .requires(tag(Tags.Items.GEMS_LAPIS), 4).requires(ItemsRegistry.BLANK_PARCHMENT).requires(SOURCE_GEM, 4)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.VOLCANIC_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
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
            shapedWoodenBoat(consumer, ItemsRegistry.ARCHWOOD_BOAT, BlockRegistry.ARCHWOOD_PLANK);

            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_BLUE, BlockRegistry.STRIPPED_AWWOOD_BLUE);
            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_GREEN, BlockRegistry.STRIPPED_AWWOOD_GREEN);
            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_RED, BlockRegistry.STRIPPED_AWWOOD_RED);
            strippedLogToWood(consumer, BlockRegistry.STRIPPED_AWLOG_PURPLE, BlockRegistry.STRIPPED_AWWOOD_PURPLE);
            shapedWoodenTrapdoor(consumer, BlockRegistry.ARCHWOOD_TRAPDOOR, BlockRegistry.ARCHWOOD_PLANK);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.SOURCE_GEM_BLOCK, 1)
                    .pattern("xx")
                    .pattern("xx").define('x', SOURCE_GEM).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer);
            shapelessBuilder(ItemsRegistry.SOURCE_GEM, 4).requires(BlockRegistry.SOURCE_GEM_BLOCK, 1).save(consumer, ArsNouveau.prefix("source_gem_block_2").toString());
            shapelessBuilder(Items.LEATHER, 1).requires(ItemsRegistry.WILDEN_WING).save(consumer, ArsNouveau.prefix("wing_to_leather").toString());
            shapelessBuilder(Items.BONE_MEAL, 3).requires(ItemsRegistry.WILDEN_HORN).save(consumer, ArsNouveau.prefix("horn_to_bonemeal").toString());
            shapelessBuilder(Items.ORANGE_DYE, 5).requires(ItemsRegistry.WILDEN_SPIKE).save(consumer, ArsNouveau.prefix("spike_to_dye").toString());
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, Items.ARROW, 32)
                    .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern(" y ")
                    .pattern(" z ")
                    .define('x', ItemsRegistry.WILDEN_SPIKE)
                    .define('y', Items.STICK)
                    .define('z', Items.FEATHER)
                    .save(consumer, ArsNouveau.prefix("spike_to_arrow").toString());

            shapelessBuilder(BlockRegistry.POTION_JAR)
                    .requires(BlockRegistry.SOURCE_JAR)
                    .requires(ItemsRegistry.ABJURATION_ESSENCE)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.POTION_JAR).requires(BlockRegistry.POTION_JAR).save(consumer, ArsNouveau.prefix("potion_jar_empty").toString());

            shapelessBuilder(BlockRegistry.RITUAL_BLOCK)
                    .requires(BlockRegistry.ARCANE_PEDESTAL.get())
                    .requires(SOURCE_GEM_BLOCK)
                    .requires(tag(Tags.Items.INGOTS_GOLD), 3)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.GOLD_SCONCE_BLOCK)
                    .requires(SOURCE_GEM)
                    .requires(tag(Tags.Items.NUGGETS_GOLD), 2)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.SOURCESTONE_SCONCE_BLOCK)
                    .requires(SOURCE_GEM)
                    .requires(Ingredient.of(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)), 2)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.ARCHWOOD_SCONCE_BLOCK)
                    .requires(SOURCE_GEM)
                    .requires(Ingredient.of(BlockRegistry.ARCHWOOD_PLANK), 2)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.POLISHED_SCONCE_BLOCK)
                    .requires(BlockRegistry.SOURCESTONE_SCONCE_BLOCK)
                    .save(consumer);

            shapelessBuilder(BlockRegistry.SOURCESTONE_SCONCE_BLOCK).requires(BlockRegistry.POLISHED_SCONCE_BLOCK).save(consumer, ArsNouveau.prefix("polished_source_sconce").toString());

            shapelessBuilder(getRitualItem(RitualLib.MOONFALL))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(Items.INK_SAC)
                    .requires(Tags.Items.STORAGE_BLOCKS_COAL)
                    .requires(Items.CLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.MOONFALL))
                    .requires(BlockRegistry.CASCADING_LOG)
                    .requires(ItemsRegistry.WILDEN_WING)
                    .save(consumer, ArsNouveau.prefix("moonfall_2").toString());

            shapelessBuilder(getRitualItem(RitualLib.SUNRISE))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.DANDELION, 3)
                    .requires(Items.CLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.SUNRISE))
                    .requires(BlockRegistry.BLAZING_LOG)
                    .requires(Items.SUNFLOWER)
                    .save(consumer, ArsNouveau.prefix("sunrise_2").toString());

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
                    .save(consumer, ArsNouveau.prefix("challenge_2").toString());

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
                    .save(consumer, ArsNouveau.prefix("clear_allow").toString());

            shapelessBuilder(ItemsRegistry.DENY_ITEM_SCROLL)
                    .requires(ItemsRegistry.DENY_ITEM_SCROLL)
                    .save(consumer, ArsNouveau.prefix("clear_deny").toString());

            shapelessBuilder(getRitualItem(RitualLib.SCRYING))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.SPIDER_EYE, 3)
                    .requires(Items.GLOWSTONE)
                    .requires(SOURCE_GEM_BLOCK)
                    .save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.FLIGHT))
                    .requires(BlockRegistry.VEXING_LOG)
                    .requires(ItemsRegistry.WILDEN_WING, 3)
                    .requires(tag(Tags.Items.GEMS_DIAMOND), 2)
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
                    .save(consumer, ArsNouveau.prefix("wilden_summon_alt").toString());

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
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.ALCHEMICAL_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.BREWING_STAND).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.VITALIC_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.GLISTERING_MELON_SLICE).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.MYCELIAL_BLOCK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" s ")
                    .pattern("gig")
                    .pattern(" s ")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('s', SOURCE_GEM)
                    .define('i', Items.MUSHROOM_STEW).save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.AGRONOMIC_SOURCELINK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
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
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.RELAY).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
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

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.BASIC_SPELL_TURRET).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xzy")
                    .pattern("yyy")
                    .define('z', tag(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                    .define('x', SOURCE_GEM)
                    .define('y', tag(Tags.Items.INGOTS_GOLD))
                    .save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.ARCHWOOD_CHEST).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('y', Items.GOLD_NUGGET)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, BlockRegistry.SPELL_PRISM).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern("gxg")
                    .pattern("xnx")
                    .pattern("gxg")
                    .define('x', BlockRegistry.ARCHWOOD_PLANK)
                    .define('g', tag(Tags.Items.INGOTS_GOLD))
                    .define('n', tag(ItemTagProvider.STORAGE_BLOCKS_QUARTZ))
                    .save(consumer);

            shapelessBuilder(Items.CHEST).requires(BlockRegistry.ARCHWOOD_CHEST).save(consumer, ArsNouveau.prefix("archwood_to_chest").toString());

            shapelessBuilder(getRitualItem(RitualLib.AWAKENING))
                    .requires(BlockRegistry.FLOURISHING_LOG)
                    .requires(BlockRegistry.BLAZING_SAPLING)
                    .requires(BlockRegistry.CASCADING_SAPLING)
                    .requires(BlockRegistry.FLOURISHING_SAPLING)
                    .requires(BlockRegistry.VEXING_SAPLING)
                    .requires(SOURCE_GEM, 4)
                    .save(consumer);

            shapelessBuilder(Items.PINK_DYE, 2).requires(ItemsRegistry.MAGE_BLOOM, 2).save(consumer, ArsNouveau.prefix("magebloom_to_pink").toString());
            shapelessBuilder(Items.PURPLE_DYE).requires(BlockRegistry.SOURCEBERRY_BUSH).save(consumer, ArsNouveau.prefix("sourceberry_to_purple").toString());
            shapelessBuilder(Items.LIME_DYE).requires(BlockRegistry.MENDOSTEEN_POD).save(consumer, ArsNouveau.prefix("mendosteen_to_lime").toString());
            shapelessBuilder(Items.PURPLE_DYE).requires(BlockRegistry.BASTION_POD).save(consumer, ArsNouveau.prefix("bastion_to_purple").toString());
            shapelessBuilder(Items.LIGHT_BLUE_DYE).requires(BlockRegistry.FROSTAYA_POD).save(consumer, ArsNouveau.prefix("frostaya_to_light_blue").toString());
            shapelessBuilder(Items.ORANGE_DYE).requires(BlockRegistry.BOMBEGRANTE_POD).save(consumer, ArsNouveau.prefix("bombegranate_to_orange").toString());

            shapelessBuilder(Items.WATER_BUCKET).requires(ItemsRegistry.WATER_ESSENCE).requires(Items.BUCKET).save(consumer, ArsNouveau.prefix("water_essence_to_bucket").toString());
            shapelessBuilder(Items.FIRE_CHARGE, 3).requires(ItemsRegistry.FIRE_ESSENCE).requires(Items.GUNPOWDER).requires(Items.COAL).save(consumer, ArsNouveau.prefix("fire_essence_to_charge").toString());


            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ItemsRegistry.DOWSING_ROD).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .pattern(" x ")
                    .pattern("a a")
                    .define('x', Tags.Items.INGOTS_GOLD)
                    .define('a', BlockRegistry.ARCHWOOD_PLANK)
                    .save(consumer);

            shapelessBuilder(ItemsRegistry.ANNOTATED_CODEX).requires(ItemsRegistry.BLANK_PARCHMENT).requires(Items.LEATHER).save(consumer);
            shapelessBuilder(Items.POWDER_SNOW_BUCKET).requires(ItemsRegistry.AIR_ESSENCE).requires(Items.BUCKET).requires(Items.SNOW_BLOCK).save(consumer, ArsNouveau.prefix("air_essence_to_snow_bucket").toString());
            shapedBuilder(Items.SOUL_SAND, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Tags.Items.SANDS)
                    .define('y', ItemsRegistry.CONJURATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("conjuration_essence_to_soul_sand").toString());
            shapedBuilder(Items.END_STONE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Tags.Items.STONES)
                    .define('y', ItemsRegistry.CONJURATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("conjuration_essence_to_end_stone").toString());

            shapelessBuilder(Items.OBSIDIAN).requires(Items.LAVA_BUCKET).requires(ItemsRegistry.WATER_ESSENCE).save(consumer, ArsNouveau.prefix("water_essence_to_obsidian").toString());
            shapedBuilder(Items.MAGMA_BLOCK, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Tags.Items.STONES)
                    .define('y', ItemsRegistry.FIRE_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("fire_essence_to_magma_block").toString());

            shapedBuilder(Items.GRANITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.DIORITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_granite").toString());
            shapedBuilder(Items.ANDESITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.GRANITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_andesite").toString());
            shapedBuilder(Items.DIORITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.ANDESITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_diorite").toString());

            shapedBuilder(Items.MYCELIUM, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.GRASS_BLOCK)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_mycelium").toString());

            shapedBuilder(Items.MOSS_BLOCK, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.MYCELIUM)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_moss_block").toString());

            shapedBuilder(Items.GRASS_BLOCK, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.MOSS_BLOCK)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_grass_block").toString());

            shapedBuilder(Items.TUFF, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.DEEPSLATE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_tuff").toString());

            shapedBuilder(Items.CALCITE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.TUFF)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_calcite").toString());

            shapedBuilder(Items.DEEPSLATE, 8)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx")
                    .define('x', Items.CALCITE)
                    .define('y', ItemsRegistry.MANIPULATION_ESSENCE)
                    .save(consumer, ArsNouveau.prefix("manipulation_essence_to_deepslate").toString());

            shapelessBuilder(BlockRegistry.CASCADING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.BLAZING_SAPLING).save(consumer, ArsNouveau.prefix("manipulation_essence_to_cascading_sapling").toString());

            shapelessBuilder(BlockRegistry.FLOURISHING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.CASCADING_SAPLING).save(consumer, ArsNouveau.prefix("manipulation_essence_to_flourishing_sapling").toString());

            shapelessBuilder(BlockRegistry.VEXING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.FLOURISHING_SAPLING).save(consumer, ArsNouveau.prefix("manipulation_essence_to_vexing_sapling").toString());

            shapelessBuilder(BlockRegistry.BLAZING_SAPLING)
                    .requires(ItemsRegistry.MANIPULATION_ESSENCE)
                    .requires(BlockRegistry.VEXING_SAPLING).save(consumer, ArsNouveau.prefix("manipulation_essence_to_blazin_sapling").toString());

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
                    .requires(SOURCE_GEM).save(consumer);

            shapelessBuilder(ItemsRegistry.BLANK_PARCHMENT).requires(ItemsRegistry.SCRYER_SCROLL).save(consumer, ArsNouveau.prefix("scry_to_blank_parchment").toString());
            shapelessBuilder(ItemsRegistry.BLANK_PARCHMENT).requires(ItemsRegistry.SPELL_PARCHMENT).save(consumer, ArsNouveau.prefix("wipe_spell_parchment").toString());
            shapedBuilder(ItemsRegistry.STARBUNCLE_SHADES)
                    .pattern("xyx")
                    .define('x', Items.TINTED_GLASS)
                    .define('y', ItemsRegistry.SOURCE_GEM)
                    .save(consumer);
            // stonecutter for sourcestone
            for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
                if (!s.equals(LibBlockNames.SOURCESTONE)) {
                    makeStonecutter(consumer, BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), BlockRegistry.getBlock(s), LibBlockNames.SOURCESTONE);
                    shapelessBuilder(SOURCESTONE).requires(BlockRegistry.getBlock(s)).save(consumer, ArsNouveau.prefix(s + "_to_sourcestone").toString());
                }

                Block stair = BuiltInRegistries.BLOCK.get(ArsNouveau.prefix(s + "_stairs")).map(h -> h.value()).orElse(Blocks.AIR);
                Block slab = BuiltInRegistries.BLOCK.get(ArsNouveau.prefix(s + "_slab")).map(h -> h.value()).orElse(Blocks.AIR);
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(BlockRegistry.getBlock(s)), RecipeCategory.BUILDING_BLOCKS, stair)
                        .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                        .save(consumer, ArsNouveau.prefix(s + "_stonecutter_stair").toString());

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(BlockRegistry.getBlock(s)), RecipeCategory.BUILDING_BLOCKS, slab, 2)
                        .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                        .save(consumer, ArsNouveau.prefix(s + "_stone_cutterslab").toString());

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
                    .pattern("xxx").define('y', BlockRegistry.ARCHWOOD_SLABS).define('x', tag(Tags.Items.GLASS_BLOCKS)).save(consumer);

            shapelessBuilder(getRitualItem(RitualLib.CONTAINMENT)).requires(BlockRegistry.VEXING_LOG).requires(ItemsRegistry.MANIPULATION_ESSENCE).requires(Items.GLASS_BOTTLE, 3).save(consumer);
            shapedBuilder(BlockRegistry.VOID_PRISM)
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y', BlockRegistry.SPELL_PRISM).define('x', tag(Tags.Items.OBSIDIANS)).save(consumer);

            shapedBuilder(BlockRegistry.MAGEBLOOM_BLOCK)
                    .pattern("xx ")
                    .pattern("xx ")
                    .define('x', ItemsRegistry.MAGE_FIBER).save(consumer);

            shapelessBuilder(ItemsRegistry.MAGE_FIBER, 4).requires(BlockRegistry.MAGEBLOOM_BLOCK).save(consumer, ArsNouveau.prefix("magebloom_block_to_magebloom").toString());

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
            shapelessBuilder(BlockRegistry.ARCANE_PEDESTAL).requires(BlockRegistry.ARCANE_PLATFORM).save(consumer, ArsNouveau.prefix("platform_to_pedestal").toString());

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
            shapelessBuilder(BlockRegistry.BASIC_SPELL_TURRET).requires(BlockRegistry.ROTATING_TURRET).save(consumer, ArsNouveau.prefix("rotating_turret_to_basic_spell_turret").toString());
            shapedBuilder(BlockRegistry.REDSTONE_RELAY)
                    .pattern("gxg")
                    .pattern("gMg")
                    .pattern("gxg")
                    .define('g', Tags.Items.INGOTS_GOLD)
                    .define('M', SOURCE_GEM_BLOCK)
                    .define('x', Tags.Items.DUSTS_REDSTONE)
                    .save(consumer);
            shapelessBuilder(BlockRegistry.SOURCEBERRY_SACK).requires(BlockRegistry.SOURCEBERRY_BUSH, 9).save(consumer);
            shapelessBuilder(BlockRegistry.SOURCEBERRY_BUSH, 9).requires(BlockRegistry.SOURCEBERRY_SACK).save(consumer, ArsNouveau.prefix("sourceberry_sack_to_bush").toString());

            shapedBuilder(BlockRegistry.GOLD_GRATE)
                    .pattern(" g ")
                    .pattern("gMg")
                    .pattern(" g ")
                    .define('g', Items.IRON_BARS)
                    .define('M', Tags.Items.INGOTS_GOLD)
                    .save(consumer);

            shapedBuilder(BlockRegistry.ARCHWOOD_GRATE)
                    .pattern(" g ")
                    .pattern("gMg")
                    .pattern(" g ")
                    .define('g', Items.IRON_BARS)
                    .define('M', ARCHWOOD_LOG)
                    .save(consumer);
            shapedBuilder(BlockRegistry.SOURCESTONE_GRATE)
                    .pattern(" g ")
                    .pattern("gMg")
                    .pattern(" g ")
                    .define('g', Items.IRON_BARS)
                    .define('M', SOURCESTONE)
                    .save(consumer);
            shapedBuilder(BlockRegistry.SMOOTH_SOURCESTONE_GRATE)
                    .pattern(" g ")
                    .pattern("gMg")
                    .pattern(" g ")
                    .define('g', Items.IRON_BARS)
                    .define('M', BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE))
                    .save(consumer);

            shapedBuilder(BlockRegistry.SOURCE_LAMP)
                    .pattern(" x ")
                    .pattern("xbx")
                    .pattern(" r ")
                    .define('x', SOURCE_GEM)
                    .define('b', Tags.Items.RODS_BLAZE)
                    .define('r', Tags.Items.DUSTS_REDSTONE)
                    .save(consumer);

            clearBuilder(ItemsRegistry.STARBUNCLE_CHARM).save(consumer, ArsNouveau.prefix("clear_starbuncle_charm").toString());
            clearBuilder(ItemsRegistry.STARBUNCLE_SHARD).save(consumer, ArsNouveau.prefix("clear_starbuncle_shard").toString());
            clearBuilder(ItemsRegistry.DRYGMY_CHARM).save(consumer, ArsNouveau.prefix("clear_drygmy_charm").toString());
            clearBuilder(ItemsRegistry.WIXIE_CHARM).save(consumer, ArsNouveau.prefix("clear_wixie_charm").toString());
            clearBuilder(ItemsRegistry.BOOKWYRM_CHARM).save(consumer, ArsNouveau.prefix("clear_bookwyrm_charm").toString());
            clearBuilder(ItemsRegistry.WHIRLISPRIG_CHARM).save(consumer, ArsNouveau.prefix("clear_whirlisprig_charm").toString());
            clearBuilder(ItemsRegistry.AMETHYST_GOLEM_CHARM).save(consumer, ArsNouveau.prefix("clear_amethyst_golem_charm").toString());
            clearBuilder(ItemsRegistry.ALAKARKINOS_CHARM).save(consumer, ArsNouveau.prefix("clear_alakarkinos_charm").toString());

            shapelessBuilder(ItemsRegistry.ARS_STENCIL.get()).requires(Items.PAPER).requires(ItemsRegistry.ABJURATION_ESSENCE.get()).save(consumer);
            shapelessBuilder(BlockRegistry.REPOSITORY_CONTROLLER.asItem()).requires(ItemsRegistry.MAGE_FIBER).requires(BlockRegistry.REPOSITORY).save(consumer);

            signBuilder(ItemsRegistry.ARCHWOOD_SIGN, Ingredient.of(BlockRegistry.ARCHWOOD_PLANK)).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.ARCHWOOD_PLANK)).save(consumer);
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ItemsRegistry.ARCHWOOD_HANGING_SIGN, 6)
                    .group("hanging_sign")
                    .define('#', tag(ItemTagProvider.ARCHWOOD_STRIPPED_LOG_TAG))
                    .define('X', Items.IRON_CHAIN)
                    .pattern("X X")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                    .save(consumer);
        }
    }

    public ShapelessRecipeBuilder clearBuilder(ItemLike item) {
        return shapelessBuilder(item).requires(item);
    }

    public static RitualTablet getRitualItem(String name) {
        return RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(name));
    }

    public ShapedRecipeBuilder shapedBuilder(ItemLike item) {
        return shapedBuilder(item, 1);
    }

    public ShapedRecipeBuilder shapedBuilder(ItemLike result, int count) {
        return ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, result, count).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }


    public ShapedRecipeBuilder makeWood(ItemLike logs, ItemLike wood, int count) {
        return ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, wood, count).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .pattern("xx ")
                .pattern("xx ").define('x', logs);
    }

    private void shapedWoodenTrapdoor(RecipeOutput recipeConsumer, ItemLike trapdoor, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, trapdoor, 2).define('#', input).pattern("###").pattern("###").group("wooden_trapdoor")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(recipeConsumer);
    }

    public void shapedWoodenStairs(RecipeOutput recipeConsumer, ItemLike stairs, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, stairs, 4)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###").unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);

    }

    public void shapedWoodenStairs(RecipeOutput recipeConsumer, ItemLike stairs, ItemLike input, String name) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, stairs, 4)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###").unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer, ArsNouveau.prefix(name).toString());

    }

    private void shapelessWoodenButton(RecipeOutput recipeConsumer, ItemLike button, ItemLike input) {
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, button).requires(input)
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void strippedLogToWood(RecipeOutput recipeConsumer, ItemLike stripped, ItemLike output) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, output, 3).define('#', stripped).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void shapedWoodenDoor(RecipeOutput recipeConsumer, ItemLike door, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, door, 3).define('#', input).pattern("##").pattern("##").pattern("##").group("wooden_door")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void shapedWoodenFence(RecipeOutput recipeConsumer, ItemLike fence, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, fence, 3).define('#', Items.STICK).define('W', input).pattern("W#W").pattern("W#W").group("wooden_fence")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void shapedWoodenFenceGate(RecipeOutput recipeConsumer, ItemLike fenceGate, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, fenceGate).define('#', Items.STICK).define('W', input).pattern("#W#").pattern("#W#").group("wooden_fence_gate")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void shapedWoodenPressurePlate(RecipeOutput recipeConsumer, ItemLike pressurePlate, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, pressurePlate).define('#', input).pattern("##").group("wooden_pressure_plate")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void shapedWoodenSlab(RecipeOutput recipeConsumer, ItemLike slab, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, slab, 6).define('#', input).pattern("###").group("wooden_slab")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer);
    }

    private void shapedWoodenSlab(RecipeOutput recipeConsumer, ItemLike slab, ItemLike input, String name) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, slab, 6).define('#', input).pattern("###").group("wooden_slab")
                .unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .save(recipeConsumer, ArsNouveau.prefix(name).toString());
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result) {
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result, int resultCount) {
        return ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, result, resultCount).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }

    private static int STONECUTTER_COUNTER = 0;

    public static void makeStonecutter(RecipeOutput consumer, ItemLike input, ItemLike output, String reg) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.DECORATIONS, output).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer, ArsNouveau.prefix(reg + "_" + STONECUTTER_COUNTER).toString());
        STONECUTTER_COUNTER++;
    }

    public void shapedWoodenBoat(RecipeOutput recipeConsumer, ItemLike boat, ItemLike input) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TRANSPORTATION, boat)
                .define('#', input)
                .pattern("# #")
                .pattern("###")
                .unlockedBy("has_planks", InventoryChangeTrigger.TriggerInstance.hasItems(input))
                .save(recipeConsumer);
    }

    /** DataProvider runner — wraps RecipeDatagen for use with GatherDataEvent. */
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, java.util.concurrent.CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeDatagen(registries, output);
        }

        @Override
        public String getName() {
            return "Ars Nouveau Recipes";
        }
    }
}
