package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
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

public class GlyphPressRecipe implements IRecipe<IInventory> {

    private final ResourceLocation id;

    public final ISpellTier.Tier tier;

    public final ItemStack reagent;

    public final ItemStack output;

    public GlyphPressRecipe(ResourceLocation id, ISpellTier.Tier tier, ItemStack reagent, ItemStack output){
        this.id = id;
        this.tier = tier;
        this.reagent = reagent;
        this.output = output;
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
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.PRESS_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation(ArsNouveau.MODID, "glyph_recipe"));
    }

    public ItemStack getClay(){
        return getClayFromTier(tier);
    }

    public static ItemStack getClayFromTier(ISpellTier.Tier tier){
        switch (tier) {
            case ONE:
                return new ItemStack(ItemsRegistry.magicClay);
            case TWO:
                return new ItemStack(ItemsRegistry.marvelousClay);
            default:
                return new ItemStack(ItemsRegistry.mythicalClay);
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GlyphPressRecipe> {

        @Override
        public GlyphPressRecipe read(ResourceLocation recipeId, JsonObject json) {
            ISpellTier.Tier tier = ISpellTier.Tier.valueOf(JSONUtils.getString(json, "tier", "ONE"));
            ItemStack input = new ItemStack(JSONUtils.getItem(json, "input"));
            ItemStack output = new ItemStack(JSONUtils.getItem(json, "output"));
            return new GlyphPressRecipe(recipeId, tier, input, output);
        }

        @Nullable
        @Override
        public GlyphPressRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            ISpellTier.Tier tier = ISpellTier.Tier.valueOf(buffer.readString());
            ItemStack input = buffer.readItemStack();
            ItemStack output = buffer.readItemStack();
            return new GlyphPressRecipe(recipeId, tier, input, output);
        }

        @Override
        public void write(PacketBuffer buf, GlyphPressRecipe recipe) {
            buf.writeString(recipe.tier.toString());
            buf.writeItemStack(recipe.reagent);
            buf.writeItemStack(recipe.output);
        }
    }
}
