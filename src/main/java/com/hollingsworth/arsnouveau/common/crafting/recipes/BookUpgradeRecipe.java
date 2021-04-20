package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.crafting.ModCrafting;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BookUpgradeRecipe extends ShapelessRecipe {

    private BookUpgradeRecipe(final ResourceLocation id, final String group, final ItemStack recipeOutput, final NonNullList<Ingredient> ingredients) {
        super(id, group, recipeOutput, ingredients);
    }

    @Override
    public ItemStack assemble(final CraftingInventory inv) {
        final ItemStack output = super.assemble(inv); // Get the default output

        if (!output.isEmpty()) {
            for (int i = 0; i < inv.getContainerSize(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
                if (!ingredient.isEmpty() && ingredient.getItem() instanceof SpellBook) {
                    output.setTag(ingredient.getTag());
                }
            }
        }

        return output; // Return the modified output
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModCrafting.Recipes.BOOK_UPGRADE_RECIPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BookUpgradeRecipe> {
        @Override
        public BookUpgradeRecipe fromJson(final ResourceLocation recipeID, final JsonObject json) {
            final String group = JSONUtils.getAsString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtil.parseShapeless(json);
            final ItemStack result = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);

            return new BookUpgradeRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public BookUpgradeRecipe fromNetwork(final ResourceLocation recipeID, final PacketBuffer buffer) {
            final String group = buffer.readUtf(Short.MAX_VALUE);
            final int numIngredients = buffer.readVarInt();
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(numIngredients, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(buffer));
            }

            final ItemStack result = buffer.readItem();

            return new BookUpgradeRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public void toNetwork(final PacketBuffer buffer, final BookUpgradeRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }

}
