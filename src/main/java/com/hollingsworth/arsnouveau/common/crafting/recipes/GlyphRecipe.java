package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GlyphRecipe implements Recipe<ScribesTile> {

    public static final String RECIPE_ID = "glyph";
    ItemStack output;
    List<Ingredient> inputs;
    ResourceLocation id;
    int exp;

    public GlyphRecipe(ResourceLocation id, ItemStack output, List<Ingredient> inputs, int exp){
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.exp = exp;
    }

    @Override
    public boolean matches(ScribesTile pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(ScribesTile pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.GLYPH_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.GLYPH_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<GlyphRecipe> {

        @Override
        public GlyphRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Item output = GsonHelper.getAsItem(json,"output");
            int count = GsonHelper.getAsInt(json, "count");
            ItemStack outputStack = new ItemStack(output, count);
            int levels = GsonHelper.getAsInt(json,  "exp");
            JsonArray inputItems = GsonHelper.getAsJsonArray(json,"inputItems");
            List<Ingredient> stacks = new ArrayList<>();

            for(JsonElement e : inputItems){
                JsonObject obj = e.getAsJsonObject();
                Ingredient input = null;
                if(GsonHelper.isArrayNode(obj, "item")){
                    input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
                }else{
                    input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
                }
                stacks.add(input);
            }
            return new GlyphRecipe(recipeId, outputStack, stacks, levels);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, GlyphRecipe recipe) {
            buf.writeInt(recipe.inputs.size());
            for(Ingredient i : recipe.inputs){
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

            for(int i = 0; i < length; i++){
                try{ stacks.add(Ingredient.fromNetwork(buffer)); }catch (Exception e){
                    e.printStackTrace();
                    break;q 
                }
            }
            return new GlyphRecipe(recipeId, buffer.readItem(), stacks, buffer.readInt());
        }
    }
}
