package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

public class EnchantmentEntry extends PedestalRecipeEntry {
    RecipeHolder<? extends EnchantmentRecipe> enchantmentRecipe;

    public EnchantmentEntry(RecipeHolder<? extends EnchantmentRecipe> enchantmentRecipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        this.enchantmentRecipe = enchantmentRecipe;
        if (enchantmentRecipe != null) {
            this.ingredients = enchantmentRecipe.value().pedestalItems();
        }
        Level level = parent.getMinecraft().level;
        EnchantmentRecipe recipe = enchantmentRecipe.value();
        if (recipe != null) {
            ItemStack outputBook = new ItemStack(Items.ENCHANTED_BOOK);

            Holder<Enchantment> enchantment = level.registryAccess().holderOrThrow(recipe.enchantmentKey);
            ItemEnchantments.Mutable outputEnchants = new ItemEnchantments.Mutable(outputBook.get(DataComponents.ENCHANTMENTS));
            outputEnchants.set(enchantment, recipe.enchantLevel);
            outputBook.set(DataComponents.ENCHANTMENTS, outputEnchants.toImmutable());

            if (recipe.enchantLevel == 1) {
                this.reagentStack = Ingredient.of(Items.BOOK);
            } else {
                ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(enchantedBook.get(DataComponents.ENCHANTMENTS));
                enchantments.set(enchantment, recipe.enchantLevel - 1);
                enchantedBook.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
                this.reagentStack = Ingredient.of(enchantedBook);
            }
            this.outputStack = EnchantmentHelper.enchantItem(level.random, outputBook, recipe.enchantLevel, Stream.of(enchantment));
        }
    }

    public static SinglePageCtor create(RecipeHolder<? extends EnchantmentRecipe> enchantmentRecipe) {
        return (parent, x, y, width, height) -> new EnchantmentEntry(enchantmentRecipe, parent, x, y, width, height);
    }

    public static SinglePageCtor create(ResourceLocation enchantmentRecipe) {
        return (parent, x, y, width, height) -> new EnchantmentEntry(parent.recipeManager().byKeyTyped(RecipeRegistry.ENCHANTMENT_TYPE.get(), enchantmentRecipe), parent, x, y, width, height);
    }


    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if (enchantmentRecipe != null) {
            object.addProperty(DocExporter.RECIPE_PROPERTY, enchantmentRecipe.id().toString());
        }
    }
}
