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

    public static ITag.INamedTag<Item> MANA_GEM_TAG = ItemTags.makeWrapperTag("forge:gems/mana");
    public static ITag.INamedTag<Item> MANA_GEM_BLOCK_TAG = ItemTags.makeWrapperTag("forge:storage_blocks/mana");
    public static ITag.INamedTag<Item> ARCHWOOD_LOG_TAG = ItemTags.makeWrapperTag("forge:logs/archwood");
    public static ITag.INamedTag<Block> DECORATIVE_AN =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "an_decorative"));

    public static Ingredient MANA_GEM = Ingredient.fromTag(MANA_GEM_TAG);
    public static Ingredient MANA_GEM_BLOCK = Ingredient.fromTag(MANA_GEM_BLOCK_TAG);
    public static Ingredient ARCHWOOD_LOG = Ingredient.fromTag(ARCHWOOD_LOG_TAG);
    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        {
            makeArmor("novice", consumer, ItemsRegistry.manaFiber);
            makeArmor("apprentice", consumer, ItemsRegistry.blazeFiber);
            makeArmor("archmage", consumer, ItemsRegistry.endFiber);

            CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(BlockRegistry.ARCANE_ORE), ItemsRegistry.manaGem,0.5f, 200)
                    .addCriterion("has_ore", InventoryChangeTrigger.Instance.forItems(BlockRegistry.ARCANE_ORE)).build(consumer);

           ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.wornNotebook).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                   .addIngredient(MANA_GEM, 1)
                   .addIngredient(Items.BOOK).build(consumer);


            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.magicClay).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(Items.CLAY_BALL)
                    .addIngredient(MANA_GEM, 1)
                    .addIngredient(Items.REDSTONE, 2)
                    .build(consumer);
            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.marvelousClay).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.magicClay)
                    .addIngredient(Tags.Items.INGOTS_GOLD)
                    .addIngredient(MANA_GEM, 1)
                    .addIngredient(Items.LAPIS_LAZULI, 2)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.mythicalClay).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.marvelousClay)
                    .addIngredient(Items.DIAMOND, 2)
                    .addIngredient(Items.BLAZE_POWDER, 2)
                    .build(consumer);



            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.manaFiber, 4).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.manaBloom)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.runicChalk, 1).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.magicClay).addIngredient(Items.BONE_MEAL).addIngredient(ItemsRegistry.manaFiber)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.blazeFiber, 2).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.manaFiber, 2)
                    .addIngredient(Items.BLAZE_POWDER)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.endFiber, 2).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.blazeFiber, 2)
                    .addIngredient(Items.POPPED_CHORUS_FRUIT)
                    .build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.MANA_JAR).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xyx")
                    .patternLine("x x")
                    .patternLine("xxx").key('x', Tags.Items.GLASS).key('y', BlockRegistry.ARCANE_STONE).build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.GLYPH_PRESS_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("xyx")
                    .patternLine("aba").key('x', BlockRegistry.ARCANE_STONE).key('y', Items.PISTON)
                    .key('a', Tags.Items.STONE).key('b', Tags.Items.STORAGE_BLOCKS_IRON).build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ARCANE_PEDESTAL).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine(" x ")
                    .patternLine("xxx").key('x', BlockRegistry.ARCANE_STONE).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ENCHANTING_APP_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xyx")
                    .patternLine("x x")
                    .patternLine("zzz").key('x', Tags.Items.INGOTS_IRON)
                    .key('y', Tags.Items.GEMS_DIAMOND)
                    .key('z', BlockRegistry.ARCANE_STONE).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(ItemsRegistry.mundaneBelt).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("   ")
                    .patternLine("xyx")
                    .patternLine(" x ")
                    .key('x', Tags.Items.LEATHER)
                    .key('y', MANA_GEM).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(ItemsRegistry.ringOfPotential).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("xyx")
                    .patternLine("xxx").key('x', Tags.Items.NUGGETS_IRON).key('y', MANA_GEM).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.MANA_CONDENSER).addCriterion("has_journal",
                    InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine(" y ")
                    .patternLine("zzz")
                    .key('x',  Tags.Items.INGOTS_IRON)
                    .key('y', Items.HOPPER)
                    .key('z', BlockRegistry.ARCANE_STONE).build(consumer);;

            ShapelessRecipeBuilder.shapelessRecipe(BlockRegistry.WARD_BLOCK, 2).addCriterion("has_journal",
                    InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(BlockRegistry.ARCANE_STONE, 9)
                    .build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ARCANE_BRICKS, 4).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xx ")
                    .patternLine("xx ")
                    .patternLine("   ").key('x', BlockRegistry.ARCANE_STONE).build(consumer);;


            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.SCRIBES_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("yzy")
                    .patternLine("y y").key('x',Ingredient.fromTag(ItemTags.WOODEN_SLABS))
                    .key('y', Items.STICK)
                    .key('z', Ingredient.fromTag(ItemTags.LOGS)).build(consumer);

            ShapedRecipeBuilder.shapedRecipe(ItemsRegistry.dullTrinket).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine(" x ")
                    .patternLine("xyx")
                    .patternLine(" x ").key('x',  Tags.Items.NUGGETS_IRON).key('y',MANA_GEM).build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ARCANE_CORE_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("y y")
                    .patternLine("xxx").key('y',  Tags.Items.INGOTS_GOLD).key('x', BlockRegistry.ARCANE_STONE).build(consumer);


            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ARCANE_STONE, 8).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("xyx")
                    .patternLine("xxx").key('y',MANA_GEM).key('x',  Tags.Items.STONE).build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.CRYSTALLIZER_BLOCK.asItem(), 1).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("yxy")
                    .patternLine("yzy")
                    .patternLine("yxy").key('x', BlockRegistry.ARCANE_STONE)
                    .key('y', Tags.Items.INGOTS_GOLD)
                    .key('z', MANA_GEM).build(consumer);

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

            ShapedRecipeBuilder.shapedRecipe(ItemsRegistry.BLANK_PARCHMENT, 1).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("yyy")
                    .patternLine("yxy")
                    .patternLine("yyy").key('x', Items.PAPER).key('y', ItemsRegistry.manaFiber).build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.spellParchment, 1).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .addIngredient(Ingredient.fromTag(ItemTags.makeWrapperTag("forge:gems/mana")), 4)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.ALLOW_ITEM_SCROLL, 1).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .addIngredient(Ingredient.fromTag(Tags.Items.CHESTS), 1)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.DENY_ITEM_SCROLL, 1).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.BLANK_PARCHMENT, 1)
                    .addIngredient(Ingredient.fromTag(Tags.Items.COBBLESTONE), 1)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.warpScroll).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(Ingredient.fromTag(Tags.Items.GEMS_LAPIS), 4).addIngredient(ItemsRegistry.BLANK_PARCHMENT).addIngredient(MANA_GEM, 4)
                    .build(consumer);

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.VOLCANIC_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("vxv")
                    .patternLine("xyx")
                    .patternLine("zzz").key('v', MANA_GEM).key('x', Items.LAVA_BUCKET).key('y', BlockRegistry.MANA_JAR).key('z', Items.GOLD_INGOT).build(consumer);
            ShapelessRecipeBuilder.shapelessRecipe(BlockRegistry.LAVA_LILY, 8).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(Items.LILY_PAD, 1).addIngredient(MANA_GEM, 8)
                    .build(consumer);

            shapelessBuilder(BlockRegistry.ARCHWOOD_PLANK, 4).addIngredient(ARCHWOOD_LOG).build(consumer);
            makeWood(BlockRegistry.VEXING_LOG, BlockRegistry.VEXING_WOOD, 3).build(consumer);
            makeWood(BlockRegistry.CASCADING_LOG, BlockRegistry.CASCADING_WOOD, 3).build(consumer);
            makeWood(BlockRegistry.BLAZING_LOG, BlockRegistry.BLAZING_WOOD, 3).build(consumer);
            makeWood(BlockRegistry.FLOURISHING_LOG, BlockRegistry.FLOURISHING_WOOD, 3).build(consumer);
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
            shapelessBuilder(BlockRegistry.MANA_GEM_BLOCK,1).addIngredient(MANA_GEM, 9).build(consumer);
            shapelessBuilder(ItemsRegistry.manaGem, 9).addIngredient(BlockRegistry.MANA_GEM_BLOCK,1).build(consumer, new ResourceLocation(ArsNouveau.MODID, "mana_gem_2"));
            shapelessBuilder(Items.LEATHER, 1).addIngredient(ItemsRegistry.WILDEN_WING).build(consumer,  new ResourceLocation(ArsNouveau.MODID, "wing_to_leather"));
            shapelessBuilder(Items.BONE_MEAL, 3).addIngredient(ItemsRegistry.WILDEN_HORN).build(consumer,  new ResourceLocation(ArsNouveau.MODID, "horn_to_bonemeal"));
            shapelessBuilder(Items.ORANGE_DYE, 5).addIngredient(ItemsRegistry.WILDEN_SPIKE).build(consumer,  new ResourceLocation(ArsNouveau.MODID, "spike_to_dye"));
            ShapedRecipeBuilder.shapedRecipe(Items.ARROW, 32)
                    .addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .patternLine(" x ")
                    .patternLine(" y ")
                    .patternLine(" z ")
                    .key('x', ItemsRegistry.WILDEN_SPIKE)
                    .key('y', Items.STICK)
                    .key('z', Items.FEATHER)
                .build(consumer, new ResourceLocation(ArsNouveau.MODID, "spike_to_arrow"));
            shapelessBuilder(BlockRegistry.POTION_JAR)
                    .addIngredient(BlockRegistry.MANA_JAR)
                    .addIngredient(Items.NETHER_WART)
                    .build(consumer);

        }
    }

    public static ShapedRecipeBuilder makeWood(IItemProvider logs, IItemProvider wood, int count){
        return ShapedRecipeBuilder.shapedRecipe(wood, count).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .patternLine("xx ")
                .patternLine("xx ").key('x', logs);
    }
    private static void shapedWoodenTrapdoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider trapdoor, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(trapdoor, 2).key('#', input).patternLine("###").patternLine("###").setGroup("wooden_trapdoor")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook)).build(recipeConsumer);
    }

    public static void shapedWoodenStairs(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stairs, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(stairs, 4)
                .key('#', input)
                .patternLine("#  ")
                .patternLine("## ")
                .patternLine("###").addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);

    }

    private static void shapelessWoodenButton(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider button, IItemProvider input) {
        ShapelessRecipeBuilder.shapelessRecipe(button).addIngredient(input)
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }

    private static void strippedLogToWood(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stripped, IItemProvider output) {
        ShapedRecipeBuilder.shapedRecipe(output, 3).key('#', stripped).patternLine("##").patternLine("##").setGroup("bark")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }
    private static void shapedWoodenDoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider door, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(door, 3).key('#', input).patternLine("##").patternLine("##").patternLine("##").setGroup("wooden_door")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }

    private static void shapedWoodenFence(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fence, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(fence, 3).key('#', Items.STICK).key('W', input).patternLine("W#W").patternLine("W#W").setGroup("wooden_fence")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }

    private static void shapedWoodenFenceGate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fenceGate, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(fenceGate).key('#', Items.STICK).key('W', input).patternLine("#W#").patternLine("#W#").setGroup("wooden_fence_gate")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }

    private static void shapedWoodenPressurePlate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider pressurePlate, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(pressurePlate).key('#', input).patternLine("##").setGroup("wooden_pressure_plate")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }

    private static void shapedWoodenSlab(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider slab, IItemProvider input) {
        ShapedRecipeBuilder.shapedRecipe(slab, 6).key('#', input).patternLine("###").setGroup("wooden_slab")
                .addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(recipeConsumer);
    }

    public ShapelessRecipeBuilder shapelessBuilder(IItemProvider result){
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(IItemProvider result, int resultCount){
        return ShapelessRecipeBuilder.shapelessRecipe(result, resultCount).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook));
    }

    private static int STONECUTTER_COUNTER = 0;
    public static void makeStonecutter(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, String reg){
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(input), output).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook)).build(consumer, new ResourceLocation(ArsNouveau.MODID, reg + "_"+STONECUTTER_COUNTER));
        STONECUTTER_COUNTER++;
    }

    public static void makeArmor(String prefix, Consumer<IFinishedRecipe> consumer, Item material){
        ShapedRecipeBuilder.shapedRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_boots")))
                .patternLine("   ")
                .patternLine("x x")
                .patternLine("x x").key('x', material).setGroup(ArsNouveau.MODID)
                .addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_leggings")))
                .patternLine("xxx")
                .patternLine("x x")
                .patternLine("x x").key('x', material).setGroup(ArsNouveau.MODID)
                .addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_hood")))
                .patternLine("xxx")
                .patternLine("x x")
                .patternLine("   ").key('x', material).setGroup(ArsNouveau.MODID)
                .addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ArsNouveau.MODID, prefix + "_robes")))
                .patternLine("x x")
                .patternLine("xxx")
                .patternLine("xxx").key('x', material).setGroup(ArsNouveau.MODID)
                .addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                .build(consumer);
    }
}
