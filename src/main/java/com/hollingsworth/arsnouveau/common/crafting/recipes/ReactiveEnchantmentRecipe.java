package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReactiveEnchantmentRecipe extends EnchantmentRecipe {

    public ReactiveEnchantmentRecipe(List<Ingredient> pedestalItems, int sourceCost) {
        super(pedestalItems, EnchantmentRegistry.REACTIVE_ENCHANTMENT.get(), 1, sourceCost);
    }

    @Override
    public boolean matches(List<ItemStack> pedestalItems, ItemStack reagent, @Nullable Player player) {
        ItemStack parchment = getParchment(pedestalItems);
        return super.matches(pedestalItems, reagent, player) && !parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty();
    }

    public static@NotNull ItemStack getParchment(List<ItemStack> pedestalItems) {
        for (ItemStack stack : pedestalItems) {
            if (stack.getItem() instanceof SpellParchment) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.REACTIVE_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.REACTIVE_RECIPE.get();
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack resultStack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        ItemStack parchment = getParchment(pedestalItems);
        ISpellCaster parchmentCaster = CasterUtil.getCaster(parchment);
        ReactiveCaster reactiveCaster = new ReactiveCaster(resultStack);
        reactiveCaster.setColor(parchmentCaster.getColor());
        reactiveCaster.setSpell(parchmentCaster.getSpell());
        return resultStack;
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RecipeRegistry.REACTIVE_RECIPE_ID);
        jsonobject.addProperty("sourceCost", sourceCost());
        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : this.pedestalItems) {
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }
        jsonobject.add("pedestalItems", pedestalArr);
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<ReactiveEnchantmentRecipe> {

        @Override
        public ReactiveEnchantmentRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int sourceCost = GsonHelper.getAsInt(json, "sourceCost", 0);
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = new ArrayList<>();

            for (JsonElement e : pedestalItems) {
                JsonObject obj = e.getAsJsonObject();
                Ingredient input;
                if (GsonHelper.isArrayNode(obj, "item")) {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
                } else {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
                }
                stacks.add(input);
            }
            return new ReactiveEnchantmentRecipe(stacks, sourceCost);
        }

        @Nullable
        @Override
        public ReactiveEnchantmentRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            int sourceCost = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            return new ReactiveEnchantmentRecipe(stacks, sourceCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ReactiveEnchantmentRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            buf.writeInt(recipe.sourceCost());
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
        }
    }
}
