package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.crafting.ModCrafting;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class PotionFlaskRecipe extends ShapelessRecipe {
    public PotionFlaskRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
    }


    @Override
    public ItemStack assemble(final CraftingContainer inv) {
        final ItemStack output = super.assemble(inv); // Get the default output
        int newCount = 0;
        Potion flaskPotion = Potions.EMPTY;
        List<MobEffectInstance> effectsList = new ArrayList<>();
        List<MobEffectInstance> flaskEffects = new ArrayList<>();
        if(output.isEmpty())
            return ItemStack.EMPTY;

        ItemStack flaskPotionStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) { // For each slot in the crafting inventory,
            final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
            if (!ingredient.isEmpty() && ingredient.getItem() instanceof PotionFlask) {
                if(((PotionFlask) ingredient.getItem()).isMax(ingredient))
                    return ItemStack.EMPTY;

                CompoundTag tag = ingredient.hasTag() ? ingredient.getTag() : new CompoundTag();

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
            output.setTag(new CompoundTag());
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
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
    public RecipeSerializer<?> getSerializer() {
        return ModCrafting.Recipes.POTION_FLASK_RECIPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<PotionFlaskRecipe> {
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

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
