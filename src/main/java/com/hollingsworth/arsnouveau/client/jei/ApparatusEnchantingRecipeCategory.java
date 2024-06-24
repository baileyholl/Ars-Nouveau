package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.List;

public class ApparatusEnchantingRecipeCategory extends EnchantingApparatusRecipeCategory<EnchantmentRecipe> {

    public ApparatusEnchantingRecipeCategory(IGuiHelper helper) {
        super(helper);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantmentRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> inputs = multiProvider.apply(recipe).input();
        double angleBetweenEach = 360.0 / inputs.size();

        Level level = Minecraft.getInstance().level;
        ItemStack dummy = recipe.enchantLevel > 1 ? EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(level, recipe.enchantmentKey), recipe.enchantLevel-1)) : Items.BOOK.getDefaultInstance();
        Component message = recipe.enchantLevel == 1 ? Component.literal("Any compatible item") : Component.literal("Needs lower level enchantment");
        dummy.set(DataComponents.CUSTOM_NAME, message); //TODO Translatable

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 45).addItemStack(dummy);

        for (Ingredient input : inputs) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y)
                    .addIngredients(input);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 10).addItemStack(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(level, recipe.enchantmentKey), recipe.enchantLevel)));

    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.enchanting");
    }

    @Override
    public RecipeType<EnchantmentRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.ENCHANTING_RECIPE_TYPE;
    }

}
