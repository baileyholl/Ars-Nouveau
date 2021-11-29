package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrushRecipe implements IRecipe<IInventory> {

    public final Ingredient input;
    public final List<CrushOutput> outputs;
    public final ResourceLocation id;
    public static final String RECIPE_ID = "crush";
    public CrushRecipe(ResourceLocation id, Ingredient input, List<CrushOutput> outputs){
        this.input = input;
        this.outputs = outputs;
        this.id = id;
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

    @Override
    public boolean matches(IInventory inventory, World world) {
        return this.input.test(inventory.getItem(0));
    }

    @Nonnull
    @Override
    public ItemStack assemble(IInventory inventory) {
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
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CRUSH_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(new ResourceLocation(ArsNouveau.MODID, RECIPE_ID));
    }

    public static class CrushOutput{
        public ItemStack stack;
        public float chance;

        public CrushOutput(ItemStack stack, float chance){
            this.stack = stack;
            this.chance = chance;
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CrushRecipe> {

        @Override
        public CrushRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "input"));
            JsonArray outputs = JSONUtils.getAsJsonArray(json,"output");
            List<CrushOutput> parsedOutputs = new ArrayList<>();

            for(JsonElement e : outputs){
                JsonObject obj = e.getAsJsonObject();
                float chance = JSONUtils.getAsFloat(obj, "chance");
                ItemStack output = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(obj, "item"));
                parsedOutputs.add(new CrushOutput(output, chance));
            }

            return new CrushRecipe(recipeId, input, parsedOutputs);
        }


        @Override
        public void toNetwork(PacketBuffer buf, CrushRecipe recipe) {
            buf.writeInt(recipe.outputs.size());
            recipe.input.toNetwork(buf);
            for(CrushOutput i : recipe.outputs){
                buf.writeFloat(i.chance);
                buf.writeItemStack(i.stack, false);
            }
        }

        @Nullable
        @Override
        public CrushRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
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
