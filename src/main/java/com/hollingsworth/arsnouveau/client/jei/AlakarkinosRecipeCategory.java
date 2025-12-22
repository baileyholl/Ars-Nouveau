package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry.LootDrop;
import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry.LootDrops;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Optional;

public class AlakarkinosRecipeCategory implements IRecipeCategory<RecipeHolder<AlakarkinosRecipe>> {
    public static float ITEMS_PER_ROW = 7f;

    public IDrawable background;
    public IDrawable icon;

    public AlakarkinosRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(126, 140);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemsRegistry.ALAKARKINOS_CHARM.asItem().getDefaultInstance());
    }

    @Override
    public RecipeType<RecipeHolder<AlakarkinosRecipe>> getRecipeType() {
        return JEIArsNouveauPlugin.ALAKARKINOS_RECIPE_TYPE.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.alakarkinos_recipe");
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
    public void draw(RecipeHolder<AlakarkinosRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        AlakarkinosRecipe recipe = recipeHolder.value();
        Minecraft minecraft = Minecraft.getInstance();
        String prepared = recipe.table().location().getPath().replace("archaeology/", "").replaceAll("_[0-9]", "").replaceAll("_", " ").toLowerCase(Locale.ROOT);
        String name = WordUtils.capitalizeFully(prepared);
        guiGraphics.drawString(minecraft.font, Component.literal(name), 22, 4, 0xFF000000, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlakarkinosRecipe> recipeHolder, IFocusGroup focuses) {
        AlakarkinosRecipe recipe = recipeHolder.value();
        DecimalFormat df = new DecimalFormat("##.##%");
        Optional<LootDrops> lootDrops = recipe.drops();
        if (lootDrops.isEmpty()) return;

        LootDrops drops = lootDrops.get();

        String recipeChance = df.format((float) recipe.weight() / drops.weight());
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addIngredient(VanillaTypes.ITEM_STACK, recipe.input().asItem().getDefaultInstance()).addRichTooltipCallback(
                (view, tooltip) -> tooltip.add(Component.translatable("ars_nouveau.alakarkinos_recipe.chance", recipeChance))
        );

        int yOffset = 9;
        int i = (int) ITEMS_PER_ROW;

        for (LootDrop drop : drops.list()) {
            int row = (int) Math.floor(i / ITEMS_PER_ROW);
            int x = (int) ((i - (row * ITEMS_PER_ROW)) * 18);
            int y = row * 18 + yOffset;

            String chance = df.format(drop.chance());
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(drop.item()).addRichTooltipCallback(
                    (view, tooltip) -> tooltip.add(Component.translatable("ars_nouveau.alakarkinos_recipe.chance", chance))
            );
            i += 1;
        }
    }
}
