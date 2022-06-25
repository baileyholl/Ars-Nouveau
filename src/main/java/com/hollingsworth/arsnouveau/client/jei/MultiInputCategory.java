package com.hollingsworth.arsnouveau.client.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public abstract class MultiInputCategory<T> implements IRecipeCategory<T> {
    Function<T, MultiProvider> multiProvider;

    public MultiInputCategory(IGuiHelper helper, Function<T, MultiProvider> multiProvider) {
        this.multiProvider = multiProvider;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        MultiProvider provider = multiProvider.apply(recipe);
        List<Ingredient> inputs = provider.input;
        double angleBetweenEach = 360.0 / inputs.size();
        if(provider.optionalCenter != null)
            builder.addSlot(RecipeIngredientRole.INPUT, 48, 45).addIngredients(provider.optionalCenter);

        Vec2 point = new Vec2(48, 13), center = new Vec2(48, 45);

        for (int i = 0; i < inputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y)
                    .addIngredients(inputs.get(i));
            point = rotatePointAbout(point, center, angleBetweenEach);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 10).addIngredient(VanillaTypes.ITEM_STACK, provider.output);
    }

    public static Vec2 rotatePointAbout(Vec2 in, Vec2 about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new Vec2((float) newX, (float) newY);
    }

    public record MultiProvider(ItemStack output, List<Ingredient> input, @Nullable Ingredient optionalCenter){}
}
