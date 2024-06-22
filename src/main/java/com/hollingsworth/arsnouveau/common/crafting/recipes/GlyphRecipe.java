package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class GlyphRecipe implements Recipe<ScribesTile> {


    public ItemStack output;
    public List<Ingredient> inputs;
    public ResourceLocation id;
    public int exp;

    public GlyphRecipe(ResourceLocation id, ItemStack output, List<Ingredient> inputs, int exp) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.exp = exp;
    }

    public GlyphRecipe withIngredient(Ingredient i) {
        this.inputs.add(i);
        return this;
    }

    public GlyphRecipe withIngredient(Ingredient ingredient, int count) {
        for (int i = 0; i < count; i++) {
            withIngredient(ingredient);
        }
        return this;
    }

    public GlyphRecipe withIngredient(TagKey<Item> tag, int count) {
        for (int i = 0; i < count; i++) {
            withIngredient(Ingredient.of(tag));
        }
        return this;
    }


    public GlyphRecipe withItem(ItemLike i) {
        this.inputs.add(Ingredient.of(i));
        return this;
    }

    public GlyphRecipe withItem(ItemLike item, int count) {
        for (int i = 0; i < count; i++) {
            withItem(item);
        }
        return this;
    }

    public GlyphRecipe withStack(ItemStack i) {
        this.inputs.add(Ingredient.of(i));
        return this;
    }

    public GlyphRecipe withStack(ItemStack stack, int count) {
        for (int i = 0; i < count; i++) {
            withStack(stack);
        }
        return this;
    }

    public AbstractSpellPart getSpellPart() {
        return ((Glyph) this.output.getItem()).spellPart;
    }

    @Override
    public boolean matches(ScribesTile pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(ScribesTile p_44001_, RegistryAccess p_267165_) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.GLYPH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.GLYPH_TYPE.get();
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RecipeRegistry.GLYPH_RECIPE_ID);
        jsonobject.addProperty("count", this.output.getCount());
        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : this.inputs) {
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }
        jsonobject.add("inputItems", pedestalArr);
        jsonobject.addProperty("exp", exp);
        jsonobject.addProperty("output", getRegistryName(output.getItem()).toString());
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<GlyphRecipe> {

        @Override
        public GlyphRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Item output = GsonHelper.getAsItem(json, "output");
            int count = GsonHelper.getAsInt(json, "count");
            ItemStack outputStack = new ItemStack(output, count);
            int levels = GsonHelper.getAsInt(json, "exp");
            JsonArray inputItems = GsonHelper.getAsJsonArray(json, "inputItems");
            List<Ingredient> stacks = new ArrayList<>();

            for (JsonElement e : inputItems) {
                JsonObject obj = e.getAsJsonObject();
                Ingredient input = null;
                if (GsonHelper.isArrayNode(obj, "item")) {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
                } else {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
                }
                stacks.add(input);
            }
            return new GlyphRecipe(recipeId, outputStack, stacks, levels);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, GlyphRecipe recipe) {
            buf.writeInt(recipe.inputs.size());
            for (Ingredient i : recipe.inputs) {
                i.toNetwork(buf);
            }
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.exp);
        }

        @Nullable
        @Override
        public GlyphRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            return new GlyphRecipe(recipeId, buffer.readItem(), stacks, buffer.readInt());
        }
    }
}
