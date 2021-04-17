package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    public static ITag.INamedTag<Item> MANA_GEM_TAG = ItemTags.bind("forge:gems/mana");
    public static ITag.INamedTag<Item> MANA_GEM_BLOCK_TAG = ItemTags.bind("forge:storage_blocks/mana");
    public static ITag.INamedTag<Item> ARCHWOOD_LOG_TAG = ItemTags.bind("forge:logs/archwood");
    public static ITag.INamedTag<Block> DECORATIVE_AN =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "an_decorative"));

    public static Ingredient MANA_GEM = Ingredient.of(MANA_GEM_TAG);
    public static Ingredient MANA_GEM_BLOCK = Ingredient.of(MANA_GEM_BLOCK_TAG);
    public static Ingredient ARCHWOOD_LOG = Ingredient.of(ARCHWOOD_LOG_TAG);
    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        {
            makeArmor("novice", consumer, ItemsRegistry.manaFiber);
            makeArmor("apprentice", consumer, ItemsRegistry.blazeFiber);
            makeArmor("archmage", consumer, ItemsRegistry.endFiber);

            CookingRecipeBuilder.smelting(Ingredient.of(BlockRegistry.ARCANE_ORE), ItemsRegistry.manaGem,0.5f, 200)
                    .unlockedBy("has_ore", InventoryChangeTrigger.Instance.hasItems(BlockRegistry.ARCANE_ORE)).save(consumer);

           ShapelessRecipeBuilder.shapeless(ItemsRegistry.wornNotebook).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                   .requires(MANA_GEM, 1)
                   .requires(Items.BOOK).save(consumer);


            ShapelessRecipeBuilder.shapeless(ItemsRegistry.magicClay).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(Items.CLAY_BALL)
                    .requires(MANA_GEM, 1)
                    .requires(Items.REDSTONE, 2)
                    .save(consumer);
            ShapelessRecipeBuilder.shapeless(ItemsRegistry.marvelousClay).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.magicClay)
                    .requires(Tags.Items.INGOTS_GOLD)
                    .requires(MANA_GEM, 1)
                    .requires(Items.LAPIS_LAZULI, 2)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.mythicalClay).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.marvelousClay)
                    .requires(Items.DIAMOND, 2)
                    .requires(Items.BLAZE_POWDER, 2)
                    .save(consumer);



            ShapelessRecipeBuilder.shapeless(ItemsRegistry.manaFiber, 4).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.manaBloom)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.runicChalk, 1).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.magicClay).requires(Items.BONE_MEAL).requires(ItemsRegistry.manaFiber)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.blazeFiber, 2).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.manaFiber, 2)
                    .requires(Items.BLAZE_POWDER)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.endFiber, 2).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.blazeFiber, 2)
                    .requires(Items.POPPED_CHORUS_FRUIT)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.MANA_JAR).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xyx")
                    .pattern("x x")
                    .pattern("xxx").define('x', Tags.Items.GLASS).define('y', BlockRegistry.ARCANE_STONE).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.GLYPH_PRESS_BLOCK).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("aba").define('x', BlockRegistry.ARCANE_STONE).define('y', Items.PISTON)
                    .define('a', Tags.Items.STONE).define('b', Tags.Items.STORAGE_BLOCKS_IRON).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_PEDESTAL).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern(" x ")
                    .pattern("xxx").define('x', BlockRegistry.ARCANE_STONE).save(consumer);;

            ShapedRecipeBuilder.shaped(BlockRegistry.ENCHANTING_APP_BLOCK).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xyx")
                    .pattern("x x")
                    .pattern("zzz").define('x', Tags.Items.INGOTS_IRON)
                    .define('y', Tags.Items.GEMS_DIAMOND)
                    .define('z', BlockRegistry.ARCANE_STONE).save(consumer);;

            ShapedRecipeBuilder.shaped(ItemsRegistry.mundaneBelt).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("   ")
                    .pattern("xyx")
                    .pattern(" x ")
                    .define('x', Tags.Items.LEATHER)
                    .define('y', MANA_GEM).save(consumer);;

            ShapedRecipeBuilder.shaped(ItemsRegistry.ringOfPotential).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('x', Tags.Items.NUGGETS_IRON).define('y', MANA_GEM).save(consumer);;

            ShapedRecipeBuilder.shaped(BlockRegistry.MANA_CONDENSER).unlockedBy("has_journal",
                    InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern(" y ")
                    .pattern("zzz")
                    .define('x',  Tags.Items.INGOTS_IRON)
                    .define('y', Items.HOPPER)
                    .define('z', BlockRegistry.ARCANE_STONE).save(consumer);;

            ShapelessRecipeBuilder.shapeless(BlockRegistry.WARD_BLOCK, 2).unlockedBy("has_journal",
                    InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(BlockRegistry.ARCANE_STONE, 9)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_BRICKS, 4).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xx ")
                    .pattern("xx ")
                    .pattern("   ").define('x', BlockRegistry.ARCANE_STONE).save(consumer);;


            ShapedRecipeBuilder.shaped(BlockRegistry.SCRIBES_BLOCK).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern("yzy")
                    .pattern("y y").define('x',Ingredient.of(ItemTags.WOODEN_SLABS))
                    .define('y', Items.STICK)
                    .define('z', Ingredient.of(ItemTags.LOGS)).save(consumer);

            ShapedRecipeBuilder.shaped(ItemsRegistry.dullTrinket).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern(" x ")
                    .pattern("xyx")
                    .pattern(" x ").define('x',  Tags.Items.NUGGETS_IRON).define('y',MANA_GEM).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_CORE_BLOCK).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern("y y")
                    .pattern("xxx").define('y',  Tags.Items.INGOTS_GOLD).define('x', BlockRegistry.ARCANE_STONE).save(consumer);


            ShapedRecipeBuilder.shaped(BlockRegistry.ARCANE_STONE, 8).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("xxx")
                    .pattern("xyx")
                    .pattern("xxx").define('y',MANA_GEM).define('x',  Tags.Items.STONE).save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.CRYSTALLIZER_BLOCK.asItem(), 1).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("yxy")
                    .pattern("yzy")
                    .pattern("yxy").define('x', BlockRegistry.ARCANE_STONE)
                    .define('y', Tags.Items.INGOTS_GOLD)
                    .define('z', MANA_GEM).save(consumer);

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

            ShapedRecipeBuilder.shaped(ItemsRegistry.BLANK_PARCHMENT, 1).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("yyy")
                    .pattern("yxy")
                    .pattern("yyy").define('x', Items.PAPER).define('y', ItemsRegistry.manaFiber).save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.spellParchment, 1).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(ItemTags.bind("forge:gems/mana")), 4)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.ALLOW_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(Tags.Items.CHESTS), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.DENY_ITEM_SCROLL, 1).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .requires(Ingredient.of(Tags.Items.COBBLESTONE), 1)
                    .save(consumer);

            ShapelessRecipeBuilder.shapeless(ItemsRegistry.warpScroll).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(Ingredient.of(Tags.Items.GEMS_LAPIS), 4).requires(ItemsRegistry.BLANK_PARCHMENT).requires(MANA_GEM, 4)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(BlockRegistry.VOLCANIC_BLOCK).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .pattern("vxv")
                    .pattern("xyx")
                    .pattern("zzz").define('v', MANA_GEM).define('x', Items.LAVA_BUCKET).define('y', BlockRegistry.MANA_JAR).define('z', Items.GOLD_INGOT).save(consumer);
            ShapelessRecipeBuilder.shapeless(BlockRegistry.LAVA_LILY, 8).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                    .requires(Items.LILY_PAD, 1).requires(MANA_GEM, 8)
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
            shapelessBuilder(BlockRegistry.MANA_GEM_BLOCK,1).requires(MANA_GEM, 9).save(consumer);
            shapelessBuilder(ItemsRegistry.manaGem, 9).requires(BlockRegistry.MANA_GEM_BLOCK,1).save(consumer, new ResourceLocation(ArsNouveau.MODID, "mana_gem_2"));
            shapelessBuilder(Items.LEATHER, 1).requires(ItemsRegistry.WILDEN_WING).save(consumer,  new ResourceLocation(ArsNouveau.MODID, "wing_to_leather"));
            shapelessBuilder(Items.BONE_MEAL, 3).requires(ItemsRegistry.WILDEN_HORN).save(consumer,  new ResourceLocation(ArsNouveau.MODID, "horn_to_bonemeal"));
            shapelessBuilder(Items.ORANGE_DYE, 5).requires(ItemsRegistry.WILDEN_SPIKE).save(consumer,  new ResourceLocation(ArsNouveau.MODID, "spike_to_dye"));
            ShapedRecipeBuilder.shaped(Items.ARROW, 32)
                    .unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .pattern(" x ")
                    .pattern(" y ")
                    .pattern(" z ")
                    .define('x', ItemsRegistry.WILDEN_SPIKE)
                    .define('y', Items.STICK)
                    .define('z', Items.FEATHER)
                .save(consumer, new ResourceLocation(ArsNouveau.MODID, "spike_to_arrow"));
            shapelessBuilder(BlockRegistry.POTION_JAR)
                    .requires(BlockRegistry.MANA_JAR)
                    .requires(Items.NETHER_WART)
                    .save(consumer);

        }
    }

    public static ShapedRecipeBuilder makeWood(IItemProvider logs, IItemProvider wood, int count){
        return ShapedRecipeBuilder.shaped(wood, count).unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .pattern("xx ")
                .pattern("xx ").define('x', logs);
    }
    private static void shapedWoodenTrapdoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider trapdoor, IItemProvider input) {
        ShapedRecipeBuilder.shaped(trapdoor, 2).define('#', input).pattern("###").pattern("###").group("wooden_trapdoor")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook)).save(recipeConsumer);
    }

    public static void shapedWoodenStairs(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stairs, IItemProvider input) {
        ShapedRecipeBuilder.shaped(stairs, 4)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###").unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);

    }

    private static void shapelessWoodenButton(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider button, IItemProvider input) {
        ShapelessRecipeBuilder.shapeless(button).requires(input)
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }

    private static void strippedLogToWood(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stripped, IItemProvider output) {
        ShapedRecipeBuilder.shaped(output, 3).define('#', stripped).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }
    private static void shapedWoodenDoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider door, IItemProvider input) {
        ShapedRecipeBuilder.shaped(door, 3).define('#', input).pattern("##").pattern("##").pattern("##").group("wooden_door")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }

    private static void shapedWoodenFence(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fence, IItemProvider input) {
        ShapedRecipeBuilder.shaped(fence, 3).define('#', Items.STICK).define('W', input).pattern("W#W").pattern("W#W").group("wooden_fence")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }

    private static void shapedWoodenFenceGate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fenceGate, IItemProvider input) {
        ShapedRecipeBuilder.shaped(fenceGate).define('#', Items.STICK).define('W', input).pattern("#W#").pattern("#W#").group("wooden_fence_gate")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }

    private static void shapedWoodenPressurePlate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider pressurePlate, IItemProvider input) {
        ShapedRecipeBuilder.shaped(pressurePlate).define('#', input).pattern("##").group("wooden_pressure_plate")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }

    private static void shapedWoodenSlab(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider slab, IItemProvider input) {
        ShapedRecipeBuilder.shaped(slab, 6).define('#', input).pattern("###").group("wooden_slab")
                .unlockedBy("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(recipeConsumer);
    }

    public ShapelessRecipeBuilder shapelessBuilder(IItemProvider result){
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(IItemProvider result, int resultCount){
        return ShapelessRecipeBuilder.shapeless(result, resultCount).unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook));
    }

    private static int STONECUTTER_COUNTER = 0;
    public static void makeStonecutter(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, String reg){
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), output).unlocks("has_journal",InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook)).save(consumer, new ResourceLocation(ArsNouveau.MODID, reg + "_"+STONECUTTER_COUNTER));
        STONECUTTER_COUNTER++;
    }

    public static void makeArmor(String prefix, Consumer<IFinishedRecipe> consumer, Item material){
        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_boots")))
                .pattern("   ")
                .pattern("x x")
                .pattern("x x").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_leggings")))
                .pattern("xxx")
                .pattern("x x")
                .pattern("x x").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_hood")))
                .pattern("xxx")
                .pattern("x x")
                .pattern("   ").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_robes")))
                .pattern("x x")
                .pattern("xxx")
                .pattern("xxx").define('x', material).group(ArsNouveau.MODID)
                .unlockedBy("has_journal", InventoryChangeTrigger.Instance.hasItems(ItemsRegistry.wornNotebook))
                .save(consumer);
    }
}
