package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ApparatusRecipe implements IRecipe<IInventory> {
    private final ResourceLocation id;
    public final Ingredient reagent;

    public final ItemStack output;
    public final List<Ingredient> pedestalItems;
    public final int manaCost;

    public ApparatusRecipe(ResourceLocation id, List<Ingredient> pedestalItems, Ingredient reagent, ItemStack output, int manaCost){
        this.manaCost = manaCost;
        this.id = id;
        this.reagent = reagent;
        this.output = output;
        this.pedestalItems = pedestalItems;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.APPARATUS_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation(ArsNouveau.MODID, "enchanting_apparatus"));
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ApparatusRecipe> {

        @Override
        public ApparatusRecipe read(ResourceLocation recipeId, JsonObject json) {
            Ingredient reagent = Ingredient.deserialize(JSONUtils.getJsonArray(json, "reagent"));
            ItemStack output = new ItemStack(JSONUtils.getItem(json, "output"));
            List<Ingredient> stacks = new ArrayList<>();
            for(int i =1; i < 9; i++){
                if(json.has("item_"+i))
                    stacks.add(Ingredient.deserialize(JSONUtils.getJsonArray(json, "item_" + i)));
            }
            return new ApparatusRecipe(recipeId, stacks, reagent, output, 0);
        }

        @Nullable
        @Override
        public ApparatusRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient reagent = Ingredient.read(buffer);
            ItemStack output = buffer.readItemStack();
            List<Ingredient> stacks = new ArrayList<>();

            for(int i =1; i < 9; i++){
                try{ Ingredient.read(buffer); }catch (Exception e){break;}
            }
            return new ApparatusRecipe(recipeId, stacks, reagent, output, 0);
        }

        @Override
        public void write(PacketBuffer buf, ApparatusRecipe recipe) {
            recipe.reagent.write(buf);
            buf.writeItemStack(recipe.output);
            for(Ingredient i : recipe.pedestalItems){
                i.write(buf);
            }
        }
    }
}
