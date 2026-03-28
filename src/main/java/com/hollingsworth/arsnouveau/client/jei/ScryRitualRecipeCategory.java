package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScryRitualRecipeCategory implements IRecipeCategory<RecipeHolder<ScryRitualRecipe>> {
    public static final Identifier SCRY_RITUAL = ArsNouveau.prefix(RitualLib.SCRYING);
    private final IDrawableAnimated arrow;
    public IDrawable background;
    public IDrawable icon;

    public ScryRitualRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(120, 24);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, RitualRegistry.getRitualItemMap().get(SCRY_RITUAL).asItem().getDefaultInstance());
        arrow = helper.createAnimatedRecipeArrow(40);
    }

    @Override
    public RecipeType<RecipeHolder<ScryRitualRecipe>> getRecipeType() {
        return JEIArsNouveauPlugin.SCRY_RITUAL_RECIPE_TYPE.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.scry_ritual_recipe");
    }

    // getBackground() removed from IRecipeCategory in JEI 27.4

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public void draw(RecipeHolder<ScryRitualRecipe> recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        background.draw(matrixStack, 0, 0);
        arrow.draw(matrixStack, 48, 5);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ScryRitualRecipe> recipeHolder, IFocusGroup focuses) {
        ScryRitualRecipe recipe = recipeHolder.value();
        List<ItemStack> items = new ArrayList<>();
        for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(recipe.highlight())) {
            items.add(blockHolder.value().asItem().getDefaultInstance());
        }
        // 1.21.11: Ingredient.of(ItemStack[]) removed - add each item stack individually to the slot
        var outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 120 - 16 - 6, 4);
        for (ItemStack stack : items) {
            outputSlot.addItemStack(stack);
        }
        // 1.21.11: Ingredient.of(TagKey<Item>) removed - use tag lookup via BuiltInRegistries
        var inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6, 4);
        for (Holder<Item> itemHolder : BuiltInRegistries.ITEM.getTagOrEmpty(recipe.augment())) {
            inputSlot.addItemStack(itemHolder.value().getDefaultInstance());
        }
    }
}
