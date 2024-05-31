package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record SummonRitualRecipe(ResourceLocation id, Ingredient augment, Integer count, WeightedRandomList<MobSpawnSettings.SpawnerData> mobs) implements Recipe<Container> {
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    public boolean matches(List<ItemStack> augments) {
        return EnchantingApparatusRecipe.doItemsMatch(augments, Arrays.stream(this.augment.getItems()).map(Ingredient::of).toList());
    }

    @Override
    public ItemStack assemble(Container pCraftingContainer, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
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
        Optional<JsonElement> encoded = Serializer.CODEC.encoder().encodeStart(JsonOps.INSTANCE, this).result();
        if (encoded.isEmpty()) return null;

        JsonObject obj = encoded.get().getAsJsonObject();
        obj.addProperty("type", "ars_nouveau:summon_ritual");

        return obj;
    }

    public static class Serializer implements RecipeSerializer<SummonRitualRecipe> {
        public static MapCodec<SummonRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SummonRitualRecipe::id),
            net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("augment").forGetter(SummonRitualRecipe::augment),
            Codec.INT.fieldOf("count").forGetter(SummonRitualRecipe::count),
            WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("mobs").forGetter(SummonRitualRecipe::mobs)
        ).apply(instance, SummonRitualRecipe::new));

        @Override
        public MapCodec<SummonRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SummonRitualRecipe> streamCodec() {
            return StreamCodec.of(this::toNetwork, this::fromNetwork);
        }

        private void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, SummonRitualRecipe recipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC.codec(), recipe);
        }

        private SummonRitualRecipe fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC.codec());
        }
    }
}
