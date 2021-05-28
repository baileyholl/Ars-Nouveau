package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.crafting.ModCrafting;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class PotionFlaskRecipe extends ShapelessRecipe {
    public PotionFlaskRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
    }


    @Override
    public ItemStack assemble(final CraftingInventory inv) {
        final ItemStack output = super.assemble(inv); // Get the default output
        int newCount = 0;
        Potion flaskPotion = Potions.EMPTY;
        List<EffectInstance> effectsList = new ArrayList<>();
        List<EffectInstance> flaskEffects = new ArrayList<>();
        if(output.isEmpty())
            return ItemStack.EMPTY;

        ItemStack flaskPotionStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) { // For each slot in the crafting inventory,
            final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
            if (!ingredient.isEmpty() && ingredient.getItem() instanceof PotionFlask) {
                if(((PotionFlask) ingredient.getItem()).isMax(ingredient))
                    return ItemStack.EMPTY;

                CompoundNBT tag = ingredient.hasTag() ? ingredient.getTag() : new CompoundNBT();

                newCount = tag.getInt("count") + 1;
                flaskPotion = PotionUtils.getPotion(ingredient);
                flaskPotionStack = ingredient;
                flaskEffects = PotionUtils.getCustomEffects(ingredient.getTag());
            }
        }
        for (int i = 0; i < inv.getContainerSize(); i++) { // For each slot in the crafting inventory,
            final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
            if (!ingredient.isEmpty() && ingredient.getItem() instanceof PotionItem) {
                Potion stackPotion = PotionUtils.getPotion(ingredient);
                effectsList = PotionUtils.getCustomEffects(ingredient.getTag());
                if(flaskPotion != Potions.EMPTY && !PotionUtils.getCustomEffects(ingredient.getTag()).equals(PotionUtils.getCustomEffects(flaskPotionStack.getTag())))
                    return ItemStack.EMPTY;
                if(flaskPotion == Potions.EMPTY){
                    flaskPotion = stackPotion;
                    System.out.println(effectsList.size());
                }
                if(!flaskPotion.equals(stackPotion))
                    return ItemStack.EMPTY;
            }
        }

        if(!output.hasTag()){
            output.setTag(new CompoundNBT());
            output.getTag().putInt("count", newCount);
            PotionUtils.setPotion(output, flaskPotion);
//            for(EffectInstance e : flaskPotion.getEffects()){
//                //effectsList.remove(e);
//                System.out.println(e.getPotion().getRegistryName().toString());
//            }
//            for(EffectInstance e : effectsList){
//                //effectsList.remove(e);
//                System.out.println(e.getPotion().getRegistryName().toString());
//            }
            PotionUtils.setCustomEffects(output, effectsList);
        }
        return output; // Return the modified output
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (item.hasContainerItem()) {
                nonnulllist.set(i, item.getContainerItem());
            }else if(item.getItem() instanceof PotionItem){
                nonnulllist.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return nonnulllist;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModCrafting.Recipes.POTION_FLASK_RECIPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PotionFlaskRecipe> {
        @Override
        public PotionFlaskRecipe fromJson(final ResourceLocation recipeID, final JsonObject json) {
            final String group = JSONUtils.getAsString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtil.parseShapeless(json);
            final ItemStack result = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);

            return new PotionFlaskRecipe(recipeID, group, result, ingredients);
        }

        @Override
        public PotionFlaskRecipe fromNetwork(final ResourceLocation recipeID, final PacketBuffer buffer) {
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
        public void toNetwork(final PacketBuffer buffer, final PotionFlaskRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
