package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record ScryRitualRecipe(ResourceLocation id, TagKey<Item> augment, TagKey<Block> highlight) implements Recipe<Container> {
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public boolean matches(ItemStack input) {
        return input.is(augment);
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
        return RecipeRegistry.SCRY_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SCRY_RITUAL_TYPE.get();
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

    public static class Serializer implements RecipeSerializer<ScryRitualRecipe> {
        public static final Codec<ScryRitualRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ScryRitualRecipe::id),
                TagKey.codec(Registries.ITEM).fieldOf("augment").forGetter(ScryRitualRecipe::augment),
                TagKey.codec(Registries.BLOCK).fieldOf("highlight").forGetter(ScryRitualRecipe::highlight)
        ).apply(instance, ScryRitualRecipe::new));

        @Override
        public ScryRitualRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable ScryRitualRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ScryRitualRecipe buddingConversionRecipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, buddingConversionRecipe);
        }
    }
}
