package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

public class BookUpgradeRecipe extends ShapelessRecipe {

    private BookUpgradeRecipe(final ResourceLocation id, final String group, final ItemStack recipeOutput, final NonNullList<Ingredient> ingredients) {
        super(id, group, CraftingBookCategory.MISC, recipeOutput, ingredients);
    }

    @Override
    public ItemStack assemble(final CraftingContainer inv, RegistryAccess p_266797_) {
        final ItemStack output = super.assemble(inv, p_266797_); // Get the default output

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
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.BOOK_UPGRADE_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<BookUpgradeRecipe> {
        @Override
        public BookUpgradeRecipe fromJson(final ResourceLocation recipeID, final JsonObject json) {
            final String group = GsonHelper.getAsString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtil.parseShapeless(json);
            final ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);

            return new BookUpgradeRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public BookUpgradeRecipe fromNetwork(final ResourceLocation recipeID, final FriendlyByteBuf buffer) {
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
        public void toNetwork(final FriendlyByteBuf buffer, final BookUpgradeRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem(null));
        }
    }

}
