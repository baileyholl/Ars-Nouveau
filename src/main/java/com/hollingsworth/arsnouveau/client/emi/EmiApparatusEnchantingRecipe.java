package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EmiApparatusEnchantingRecipe extends EmiEnchantingApparatusRecipe<EnchantmentRecipe> {
    public EmiApparatusEnchantingRecipe(ResourceLocation id, EnchantmentRecipe recipe) {
        super(id, recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.APPARATUS_ENCHANTING_CATEGORY;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        this.reset();
        double angleBetweenEach = 360.0 / this.recipe.pedestalItems().size();

        MultiProvider provider = multiProvider;
        Level level = Minecraft.getInstance().level;
        ItemStack dummy = this.getRecipe().enchantLevel > 1 ? EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(level, recipe.enchantmentKey), recipe.enchantLevel-1)) : Items.BOOK.getDefaultInstance();
        Component message = this.getRecipe().enchantLevel == 1 ? Component.literal("Any compatible item") : Component.literal("Needs lower level enchantment");
        dummy.set(DataComponents.CUSTOM_NAME, message); //TODO Translatable

        widgets.addSlot(EmiIngredient.of(Ingredient.of(dummy)), (int) this.center.x, (int) this.center.y);

        for (EmiIngredient input : provider.getEmiInputs()) {
            widgets.addSlot(input, (int) point.x, (int) point.y);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        widgets.addSlot(EmiIngredient.of(Ingredient.of(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(level, this.getRecipe().enchantmentKey), this.getRecipe().enchantLevel)))), 100, 3).recipeContext(this);

        this.addSourceWidget(widgets);
    }
}
