package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.armor.NoviceArmor;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ModBlock;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.BlockStatePaletteRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        {
            makeArmor("novice", consumer, ItemsRegistry.manaFiber);
            makeArmor("apprentice", consumer, ItemsRegistry.blazeFiber);
            makeArmor("archmage", consumer, ItemsRegistry.endFiber);

            CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(BlockRegistry.ARCANE_ORE), ItemsRegistry.arcaneBrick,0, 200)
                    .addCriterion("has_ore", InventoryChangeTrigger.Instance.forItems(BlockRegistry.ARCANE_ORE)).build(consumer);

           ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.wornNotebook).addCriterion("has_brick", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.arcaneBrick))
                   .addIngredient(ItemsRegistry.arcaneBrick, 1)
                   .addIngredient(Items.BOOK).build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.noviceSpellBook).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.wornNotebook)
                    .addIngredient(Items.IRON_SHOVEL)
                    .addIngredient(Items.IRON_PICKAXE)
                    .addIngredient(Items.IRON_AXE)
                    .addIngredient(Items.IRON_SWORD)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.apprenticeSpellBook).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.noviceSpellBook)
                    .addIngredient(Items.DIAMOND, 3)
                    .addIngredient(Items.BLAZE_ROD, 2)
                    .addIngredient(Items.QUARTZ_BLOCK, 2)
                    .addIngredient(Items.OBSIDIAN)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.archmageSpellBook).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.apprenticeSpellBook)
                    .addIngredient(Items.NETHER_STAR)
                    .addIngredient(Items.EMERALD, 3)
                    .addIngredient(Items.ENDER_PEARL, 3)
                    .addIngredient(Items.TOTEM_OF_UNDYING)
                    .build(consumer);

            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.magicClay).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(Items.CLAY_BALL)
                    .addIngredient(Items.IRON_INGOT, 2)
                    .addIngredient(Items.REDSTONE, 2)
                    .build(consumer);
            ShapelessRecipeBuilder.shapelessRecipe(ItemsRegistry.marvelousClay).addCriterion("has_journal", InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .addIngredient(ItemsRegistry.magicClay)
                    .addIngredient(Items.GOLD_INGOT, 2)
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
                    .patternLine("xxx").key('x', Blocks.GLASS).key('y', ItemsRegistry.arcaneBrick).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.GLYPH_PRESS_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("xyx")
                    .patternLine("aba").key('x', ItemsRegistry.arcaneBrick).key('y', Items.PISTON).key('a', Items.STONE).key('b', Items.IRON_BLOCK).build(consumer);;
            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ARCANE_PEDESTAL).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine(" x ")
                    .patternLine("xxx").key('x', ItemsRegistry.arcaneBrick).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ENCHANTING_APP_BLOCK).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xyx")
                    .patternLine("x x")
                    .patternLine("zzz").key('x', Items.IRON_INGOT).key('y', Items.DIAMOND).key('z', ItemsRegistry.arcaneBrick).build(consumer);;
            ShapedRecipeBuilder.shapedRecipe(ItemsRegistry.mundaneBelt).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("x x")
                    .patternLine("xxx").key('x', Items.LEATHER).build(consumer);;
            ShapedRecipeBuilder.shapedRecipe(ItemsRegistry.ringOfPotential).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine("x x")
                    .patternLine("xxx").key('x', Items.IRON_NUGGET).build(consumer);;

            ShapedRecipeBuilder.shapedRecipe(BlockRegistry.MANA_CONDENSER).addCriterion("has_journal",InventoryChangeTrigger.Instance.forItems(ItemsRegistry.wornNotebook))
                    .patternLine("xxx")
                    .patternLine(" y ")
                    .patternLine("zzz").key('x', Items.IRON_INGOT).key('y', Items.HOPPER).key('z', ItemsRegistry.arcaneBrick).build(consumer);;



        }
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
