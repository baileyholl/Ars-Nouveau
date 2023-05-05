package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
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
import org.jetbrains.annotations.Nullable;

public class SummonRitualRecipe implements Recipe<Container> {

    public final ResourceLocation id;
    public final Ingredient catalyst;
    public final ResourceLocation mob;

    public SummonRitualRecipe(ResourceLocation id, Ingredient catalyst, ResourceLocation mob) {
        this.id = id;
        this.catalyst = catalyst;
        this.mob = mob;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    public boolean matches(ItemStack augment) {
        return catalyst.test(augment);
    }


    @Override
    public ItemStack assemble(Container pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
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
        return RecipeRegistry.SUMMON_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SUMMON_RITUAL_TYPE.get();
    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:summon_ritual");
        jsonobject.addProperty("mob", this.mob.toString());
        jsonobject.add("augment", catalyst.toJson());
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<SummonRitualRecipe>{

        @Override
        public SummonRitualRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            Ingredient augment = Ingredient.fromJson(GsonHelper.isArrayNode(json, "augment") ? GsonHelper.getAsJsonArray(json, "augment") : GsonHelper.getAsJsonObject(json, "augment"));
            ResourceLocation mob = ResourceLocation.tryParse(GsonHelper.getAsString(json, "mob"));
            return new SummonRitualRecipe(pRecipeId, augment, mob);
        }

        @Override
        public @Nullable SummonRitualRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient catalyst = Ingredient.fromNetwork(pBuffer);
            ResourceLocation mob = pBuffer.readResourceLocation();
            return new SummonRitualRecipe(pRecipeId, catalyst, mob);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SummonRitualRecipe pRecipe) {
            pRecipe.catalyst.toNetwork(pBuffer);
            pBuffer.writeResourceLocation(pRecipe.mob);
        }
    }
}
