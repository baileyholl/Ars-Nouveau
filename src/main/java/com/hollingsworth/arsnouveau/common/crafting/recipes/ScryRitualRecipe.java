package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.scrying.EntityTagScryer;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record ScryRitualRecipe(TagKey<Item> augment, Either<BlockHighlight, EntityHighlight> highlight) implements SpecialSingleInputRecipe {

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
                Codec.mapEither(
                        BlockHighlight.LEGACY_CODEC,
                        EntityHighlight.CODEC.fieldOf("entity_highlight")
                ).forGetter(ScryRitualRecipe::highlight)
        ).apply(instance, ScryRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ScryRitualRecipe> STREAM_CODEC = CheatSerializer.create(CODEC);

        @Override
        public MapCodec<ScryRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ScryRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public record BlockHighlight(TagKey<Block> tag) {
        public static final MapCodec<BlockHighlight> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.BLOCK).fieldOf("highlight").forGetter(BlockHighlight::tag)
        ).apply(instance, BlockHighlight::new));

        public static final MapCodec<BlockHighlight> LEGACY_CODEC = Codec.mapEither(
                CODEC.fieldOf("block_highlight"),
                TagKey.codec(Registries.BLOCK).fieldOf("highlight").xmap(BlockHighlight::new, BlockHighlight::tag)
        ).xmap(Either::unwrap, Either::left);

        public IScryer getScryer() {
            return new TagScryer(tag);
        }
    }

    public record EntityHighlight(TagKey<EntityType<?>> tag, ParticleColor color) {
        public static final MapCodec<EntityHighlight> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ENTITY_TYPE).fieldOf("highlight").forGetter(EntityHighlight::tag),
                ParticleColor.CODEC.fieldOf("color").forGetter(EntityHighlight::color)
        ).apply(instance, EntityHighlight::new));

        public IScryer getScryer() {
            return new EntityTagScryer(tag, color);
        }
    }
}
