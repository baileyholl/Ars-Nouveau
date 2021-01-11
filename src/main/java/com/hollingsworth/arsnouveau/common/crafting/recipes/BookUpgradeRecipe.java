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
    public ItemStack getCraftingResult(final CraftingInventory inv) {
        final ItemStack output = super.getCraftingResult(inv); // Get the default output

        if (!output.isEmpty()) {
            for (int i = 0; i < inv.getSizeInventory(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getStackInSlot(i); // Get the ingredient in the slot
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
        public BookUpgradeRecipe read(final ResourceLocation recipeID, final JsonObject json) {
            final String group = JSONUtils.getString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtil.parseShapeless(json);
            final ItemStack result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);

            return new BookUpgradeRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public BookUpgradeRecipe read(final ResourceLocation recipeID, final PacketBuffer buffer) {
            final String group = buffer.readString(Short.MAX_VALUE);
            final int numIngredients = buffer.readVarInt();
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(numIngredients, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.read(buffer));
            }

            final ItemStack result = buffer.readItemStack();

            return new BookUpgradeRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public void write(final PacketBuffer buffer, final BookUpgradeRecipe recipe) {
            buffer.writeString(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }

}
