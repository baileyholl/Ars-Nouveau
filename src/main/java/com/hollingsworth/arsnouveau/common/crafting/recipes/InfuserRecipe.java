package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;


public class InfuserRecipe implements Recipe<Container> {
    public final Ingredient input;
    public final ItemStack output;
    public final int source;
    public final ResourceLocation id;
    public static final String RECIPE_ID = "infuser";

    public InfuserRecipe(ResourceLocation resourceLocation, Ingredient input, ItemStack output, int source){
        this.id = resourceLocation;
        this.input = input;
        this.output = output;
        this.source = source;
    }

    public InfuserRecipe(String id, Ingredient ingredient, ItemStack output, int source){
        this(new ResourceLocation(ArsNouveau.MODID, RECIPE_ID + "_" + id), ingredient, output, source);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container p_44001_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.INFUSER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(new ResourceLocation(ArsNouveau.MODID, RECIPE_ID));
    }

    public JsonElement asRecipe(){
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RECIPE_ID);
        jsonobject.add("input", input.toJson());
        jsonobject.addProperty("output", output.getItem().getRegistryName().toString());
        jsonobject.addProperty("count", output.getCount());
        jsonobject.addProperty("source", source);
        return jsonobject;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<InfuserRecipe> {

        @Override
        public InfuserRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = null;
            if(GsonHelper.isArrayNode(json, "input")){
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            }else{
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));;
            }
            Item output = GsonHelper.getAsItem(json,"output");
            int count = GsonHelper.getAsInt(json, "count");
            ItemStack outputStack = new ItemStack(output, count);
            int source = GsonHelper.getAsInt(json,  "source");
            return new InfuserRecipe(recipeId, input, outputStack, source);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, InfuserRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.source);
        }

        @Nullable
        @Override
        public InfuserRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new InfuserRecipe(recipeId, Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readInt());
        }
    }
}
