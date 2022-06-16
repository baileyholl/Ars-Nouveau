package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class CrushRecipe implements Recipe<Container> {

    public final Ingredient input;
    public final List<CrushOutput> outputs;
    public final ResourceLocation id;
    public static final String RECIPE_ID = "crush";

    public CrushRecipe(ResourceLocation id, Ingredient input, List<CrushOutput> outputs) {
        this.input = input;
        this.outputs = outputs;
        this.id = id;
    }

    public CrushRecipe(String id, Ingredient input, List<CrushOutput> outputs){
        this(new ResourceLocation(ArsNouveau.MODID, "crush_" + id), input, outputs);
    }

    public CrushRecipe(String id, Ingredient input){
        this(id, input, new ArrayList<>());
    }

    public List<ItemStack> getRolledOutputs(Random random){
        List<ItemStack> finalOutputs = new ArrayList<>();
        for(CrushOutput crushRoll : outputs){
            if(random.nextDouble() <= crushRoll.chance){
                finalOutputs.add(crushRoll.stack.copy());
            }
        }

        return finalOutputs;
    }

    public CrushRecipe withItems(ItemStack output, float chance){
        this.outputs.add(new CrushOutput(output, chance));
        return this;
    }

    public CrushRecipe withItems(ItemStack output){
        this.outputs.add(new CrushOutput(output, 1.0f));
        return this;
    }
    @Override
    public boolean matches(Container inventory, Level world) {
        return this.input.test(inventory.getItem(0));
    }

    public boolean matches(ItemStack i, Level world){
        return this.input.test(i);
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CRUSH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CRUSH_TYPE.get();
    }

    public JsonElement asRecipe(){
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:crush");
        jsonobject.add("input", input.toJson());
        JsonArray array = new JsonArray();
        for(CrushOutput output : outputs){
            JsonObject element = new JsonObject();
            element.addProperty("item", getRegistryName(output.stack.getItem()).toString());
            element.addProperty("chance", output.chance);
            element.addProperty("count", output.stack.getCount());
            array.add(element);
        }
        jsonobject.add("output", array);
        return jsonobject;
    }

    public static class CrushOutput{
        public ItemStack stack;
        public float chance;

        public CrushOutput(ItemStack stack, float chance){
            this.stack = stack;
            this.chance = chance;
        }
    }

    public static class Serializer implements RecipeSerializer<CrushRecipe> {

        @Override
        public CrushRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = null;
            if (GsonHelper.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            } else {
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            }
            JsonArray outputs = GsonHelper.getAsJsonArray(json, "output");
            List<CrushOutput> parsedOutputs = new ArrayList<>();

            for(JsonElement e : outputs){
                JsonObject obj = e.getAsJsonObject();
                float chance = GsonHelper.getAsFloat(obj, "chance");
                String itemId = GsonHelper.getAsString(obj, "item");
                int count = obj.has("count") ? GsonHelper.getAsInt(obj, "count") : 1;
                ItemStack output = new ItemStack(Registry.ITEM.get(new ResourceLocation(itemId)), count);
                parsedOutputs.add(new CrushOutput(output, chance));
            }

            return new CrushRecipe(recipeId, input, parsedOutputs);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, CrushRecipe recipe) {
            buf.writeInt(recipe.outputs.size());
            recipe.input.toNetwork(buf);
            for(CrushOutput i : recipe.outputs){
                buf.writeFloat(i.chance);
                buf.writeItemStack(i.stack, false);
            }
        }

        @Nullable
        @Override
        public CrushRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient input = Ingredient.fromNetwork(buffer);
            List<CrushOutput> stacks = new ArrayList<>();

            for(int i = 0; i < length; i++){
                try{
                    float chance = buffer.readFloat();
                    ItemStack outStack = buffer.readItem();
                    stacks.add(new CrushOutput(outStack, chance));
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
            return new CrushRecipe(recipeId, input, stacks);
        }
    }
}
