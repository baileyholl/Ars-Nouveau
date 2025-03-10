package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record ScryRitualRecipe(TagKey<Item> augment, TagKey<Block> highlight) implements SpecialSingleInputRecipe {

    @Override
    public boolean matches(SingleRecipeInput input, Level p_345375_) {
        return input.getItem(0).is(augment);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SCRY_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SCRY_RITUAL_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ScryRitualRecipe> {
        public static final MapCodec<ScryRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ITEM).fieldOf("augment").forGetter(ScryRitualRecipe::augment),
                TagKey.codec(Registries.BLOCK).fieldOf("highlight").forGetter(ScryRitualRecipe::highlight)
        ).apply(instance, ScryRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ScryRitualRecipe> STREAM_CODEC = StreamCodec.composite(
                ANCodecs.streamTagKey(Registries.ITEM), ScryRitualRecipe::augment,
                ANCodecs.streamTagKey(Registries.BLOCK), ScryRitualRecipe::highlight,
                ScryRitualRecipe::new
        );

        @Override
        public MapCodec<ScryRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ScryRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
