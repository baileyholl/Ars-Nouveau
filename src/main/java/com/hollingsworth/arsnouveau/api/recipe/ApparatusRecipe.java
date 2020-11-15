package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
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
    public final ItemStack reagent;

    public final ItemStack output;
    public final List<ItemStack> pedestalItems;
    public final int manaCost;

    public ApparatusRecipe(ResourceLocation id, List<ItemStack> pedestalItems, ItemStack reagent, ItemStack output, int manaCost){
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
            ItemStack reagent = new ItemStack(JSONUtils.getItem(json, "reagent"));
            ItemStack output = new ItemStack(JSONUtils.getItem(json, "output"));
            List<ItemStack> stacks = new ArrayList<>();
            for(int i =1; i < 9; i++){
                if(json.has("item_"+i))
                    stacks.add(new ItemStack(JSONUtils.getItem(json, "item_" + i)));
            }
            return new ApparatusRecipe(recipeId, stacks, reagent, output, 0);
        }

        @Nullable
        @Override
        public ApparatusRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            ItemStack reagent = buffer.readItemStack();
            ItemStack output = buffer.readItemStack();
            List<ItemStack> stacks = new ArrayList<>();

            for(int i =1; i < 9; i++){
                try{ stacks.add(buffer.readItemStack()); }catch (Exception e){break;}
            }
            return new ApparatusRecipe(recipeId, stacks, reagent, output, 0);
        }

        @Override
        public void write(PacketBuffer buf, ApparatusRecipe recipe) {
            buf.writeItemStack(recipe.reagent);
            buf.writeItemStack(recipe.output);
            for(ItemStack i : recipe.pedestalItems){
                buf.writeItemStack(i);
            }
        }
    }
}
