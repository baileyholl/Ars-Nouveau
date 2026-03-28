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
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public class ApparatusEnchantingRecipeCategory extends EnchantingApparatusRecipeCategory<EnchantmentRecipe> {

    public ApparatusEnchantingRecipeCategory(IGuiHelper helper) {
        super(helper);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EnchantmentRecipe> recipeHolder, IFocusGroup focuses) {
        EnchantmentRecipe recipe = recipeHolder.value();
        List<Ingredient> inputs = multiProvider.apply(recipe).input();
        double angleBetweenEach = 360.0 / inputs.size();

        Level level = Minecraft.getInstance().level;
        ItemStack dummy = recipe.enchantLevel > 1 ? createEnchantedBook(level, recipe.enchantmentKey, recipe.enchantLevel - 1) : Items.BOOK.getDefaultInstance();
        Component message = recipe.enchantLevel == 1 ? Component.translatable("ars_nouveau.jei.apparatus.any_item") : Component.translatable("ars_nouveau.jei.apparatus.needs_lower");
        dummy.set(DataComponents.CUSTOM_NAME, message);

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 45).addItemStack(dummy);

        for (Ingredient input : inputs) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y)
                    .addIngredients(input);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 10).addItemStack(createEnchantedBook(level, recipe.enchantmentKey, recipe.enchantLevel));

    }

    /** Creates an enchanted book ItemStack with the given enchantment (1.21.11: EnchantedBookItem.createForEnchantment is gone). */
    private static ItemStack createEnchantedBook(Level level, ResourceKey<Enchantment> key, int enchantLevel) {
        Holder<Enchantment> holder = HolderHelper.unwrap(level, key);
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holder, enchantLevel);
        book.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return book;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.enchanting");
    }

    @Override
    public RecipeType<RecipeHolder<EnchantmentRecipe>> getRecipeType() {
        return JEIArsNouveauPlugin.ENCHANTING_RECIPE_TYPE.get();
    }

}
