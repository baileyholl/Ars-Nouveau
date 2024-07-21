package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record BuddingConversionRecipe(ResourceLocation id, Block input, Block result) implements Recipe<Container> {
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public boolean matches(BlockState block) {
        return block.is(input);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.BUDDING_CONVERSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.BUDDING_CONVERSION_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public JsonElement asRecipe() {
        JsonElement recipe = Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    public static class Serializer implements RecipeSerializer<BuddingConversionRecipe> {
        public static final Codec<BuddingConversionRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(BuddingConversionRecipe::id),
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input").forGetter(BuddingConversionRecipe::input),
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("result").forGetter(BuddingConversionRecipe::result)
        ).apply(instance, BuddingConversionRecipe::new));

        @Override
        public BuddingConversionRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable BuddingConversionRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, BuddingConversionRecipe buddingConversionRecipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, buddingConversionRecipe);
        }
    }
}
