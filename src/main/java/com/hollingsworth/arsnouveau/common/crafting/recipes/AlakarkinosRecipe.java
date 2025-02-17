package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry;
import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry.LootDrop;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.*;

import static com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry.LootDrops;

public record AlakarkinosRecipe(Block input, ResourceKey<LootTable> table, int weight, Optional<LootDrops> drops) implements SpecialSingleInputRecipe {
    public AlakarkinosRecipe(Block input, ResourceKey<LootTable> table, int weight) {
        this(input, table, weight, Optional.empty());
    }

    public boolean matches(BlockState block) {
        return block.is(input);
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ALAKARKINOS_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ALAKARKINOS_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AlakarkinosRecipe> {
        public static final MapCodec<AlakarkinosRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input").forGetter(AlakarkinosRecipe::input),
                ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("table").forGetter(AlakarkinosRecipe::table),
                Codec.INT.fieldOf("weight").forGetter(AlakarkinosRecipe::weight),
                LootDrops.CODEC.optionalFieldOf("drops").forGetter(AlakarkinosRecipe::drops)
        ).apply(instance, AlakarkinosRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlakarkinosRecipe> STREAM = StreamCodec.of(
                AlakarkinosRecipe.Serializer::toNetwork, AlakarkinosRecipe.Serializer::fromNetwork
        );

        private static AlakarkinosRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            return CheatSerializer.fromNetwork(CODEC, buf);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, AlakarkinosRecipe karky) {
            LootDrops drops = LootDrop.getLootDrops(karky.table);
            AlakarkinosRecipe recipe = new AlakarkinosRecipe(karky.input, karky.table, karky.weight, Optional.ofNullable(drops));
            CheatSerializer.toNetwork(CODEC, buf, recipe);
        }

        @Override
        public MapCodec<AlakarkinosRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlakarkinosRecipe> streamCodec() {
            return STREAM;
        }
    }
}
