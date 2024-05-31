package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

public class PotionFlaskRecipe extends ShapelessRecipe {
    public PotionFlaskRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, CraftingBookCategory.MISC, recipeOutputIn, recipeItemsIn);
    }

    @Override
    public ItemStack assemble(final CraftingContainer inv, RegistryAccess p_266797_) {
        final ItemStack output = super.assemble(inv, p_266797_); // Get the default output
        if (output.isEmpty())
            return ItemStack.EMPTY;
        ItemStack flaskPotionStack = ItemStack.EMPTY;
        ItemStack potionStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof PotionFlask flask) {
                flaskPotionStack = stack.copy();
                if(flask.isMax(stack))
                    return ItemStack.EMPTY;
            }
            if(stack.getItem() instanceof PotionItem){
                potionStack = stack;
            }
        }
        if(flaskPotionStack.isEmpty() || potionStack.isEmpty())
            return ItemStack.EMPTY;
        PotionFlask.FlaskData flaskData = new PotionFlask.FlaskData(flaskPotionStack);
        PotionData potionData = new PotionData(potionStack);
        if(flaskData.getCount() <= 0){
            flaskData.setPotion(potionData);
            flaskData.setCount(1);
            return flaskPotionStack.copy();
        }
        if(flaskData.getPotion().areSameEffects(potionData)){
            flaskData.setCount(flaskData.getCount() + 1);
            return flaskPotionStack.copy();
        }
        return ItemStack.EMPTY; // Return the modified output
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            } else if (item.getItem() instanceof PotionItem) {
                nonnulllist.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.POTION_FLASK_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<PotionFlaskRecipe> {
        @Override
        public PotionFlaskRecipe fromJson(final ResourceLocation recipeID, final JsonObject json) {
            final String group = GsonHelper.getAsString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtil.parseShapeless(json);
            final ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return new PotionFlaskRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public PotionFlaskRecipe fromNetwork(final ResourceLocation recipeID, final FriendlyByteBuf buffer) {
            final String group = buffer.readUtf(Short.MAX_VALUE);
            final int numIngredients = buffer.readVarInt();
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(numIngredients, Ingredient.EMPTY);
            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(buffer));
            }
            final ItemStack result = buffer.readItem();
            return new PotionFlaskRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buffer, final PotionFlaskRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());
            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.getResultItem(null));
        }
    }
}
