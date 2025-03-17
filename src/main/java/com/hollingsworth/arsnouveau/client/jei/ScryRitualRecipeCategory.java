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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScryRitualRecipeCategory implements IRecipeCategory<ScryRitualRecipe> {
    public static final ResourceLocation SCRY_RITUAL = ArsNouveau.prefix(RitualLib.SCRYING);
    private final IDrawableAnimated arrow;
    public IDrawable background;
    public IDrawable icon;

    public ScryRitualRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(120, 24);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, RitualRegistry.getRitualItemMap().get(SCRY_RITUAL).asItem().getDefaultInstance());
        arrow = helper.createAnimatedRecipeArrow(40);
    }

    @Override
    public RecipeType<ScryRitualRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.SCRY_RITUAL_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.scry_ritual_recipe");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ScryRitualRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        background.draw(matrixStack, 0, 0);
        arrow.draw(matrixStack, 48, 5);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ScryRitualRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> items = new ArrayList<>();
        recipe.highlight().left().ifPresent(block -> {
            for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(block.tag())) {
                items.add(blockHolder.value().asItem().getDefaultInstance());
            }
        });
        recipe.highlight().right().ifPresent(entity -> {
            for (Holder<EntityType<?>> entityTypeHolder : BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(entity.tag())) {
                SpawnEggItem item = SpawnEggItem.byId(entityTypeHolder.value());
                if (item != null) {
                    items.add(item.getDefaultInstance());
                }
            }
        });
        ItemStack[] stacks = items.toArray(new ItemStack[]{});
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120-16-6, 4).addIngredients(Ingredient.of(stacks));
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 4).addIngredients(Ingredient.of(recipe.augment()));
    }
}
