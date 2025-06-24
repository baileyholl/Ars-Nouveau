package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * Copied from {@link net.minecraft.world.item.crafting.ShapelessRecipe.Serializer}
 * and adapted to take any conforming {@link ShapelessRecipe} implementation.
 */
public class ExtendableShapelessSerializer {

    public static <T extends ShapelessRecipe> RecipeSerializer<T> create(ShapelessConstructor<T> creator) {
        return new RecipeSerializer<T>() {
            public final MapCodec<T> CODEC = ExtendableShapelessSerializer.createMap(creator);
            public final StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC = ExtendableShapelessSerializer.createStream(creator);

            @Override
            public MapCodec<T> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return STREAM_CODEC;
            }
        };
    }

    public static <T extends ShapelessRecipe> MapCodec<T> createMap(ShapelessConstructor<T> create) {
        return RecordCodecBuilder.mapCodec(
                p_340779_ -> p_340779_.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.getResultItem(null)),
                                Ingredient.CODEC_NONEMPTY
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                p_301021_ -> {
                                                    Ingredient[] aingredient = p_301021_.toArray(Ingredient[]::new); // Neo skip the empty check and immediately create the array.
                                                    if (aingredient.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                                    } else {
                                                        return aingredient.length > ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()
                                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()))
                                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(ShapelessRecipe::getIngredients)
                        )
                        .apply(p_340779_, create::create)
        );
    }

    public static <T extends ShapelessRecipe> StreamCodec<RegistryFriendlyByteBuf, T> createStream(ShapelessConstructor<T> create) {
        return StreamCodec.of(
                ExtendableShapelessSerializer::toNetwork, (a) -> ExtendableShapelessSerializer.fromNetwork(a, create)
        );
    }

    private static <T extends ShapelessRecipe> T fromNetwork(RegistryFriendlyByteBuf p_319905_, ShapelessConstructor<T> create) {
        String s = p_319905_.readUtf();
        CraftingBookCategory craftingbookcategory = p_319905_.readEnum(CraftingBookCategory.class);
        int i = p_319905_.readVarInt();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
        nonnulllist.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(p_319905_));
        ItemStack itemstack = ItemStack.STREAM_CODEC.decode(p_319905_);
        return create.create(s, craftingbookcategory, itemstack, nonnulllist);
    }

    private static void toNetwork(RegistryFriendlyByteBuf p_320371_, ShapelessRecipe p_320323_) {
        p_320371_.writeUtf(p_320323_.getGroup());
        p_320371_.writeEnum(p_320323_.category());
        p_320371_.writeVarInt(p_320323_.getIngredients().size());

        for (Ingredient ingredient : p_320323_.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320371_, ingredient);
        }

        ItemStack.STREAM_CODEC.encode(p_320371_, p_320323_.getResultItem(null));
    }

    @FunctionalInterface
    public interface ShapelessConstructor<T> {
        T create(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients);
    }
}
