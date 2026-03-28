package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

/**
 * Adapted for MC 1.21.11 ShapelessRecipe API:
 * - group() replaces getGroup()
 * - ingredients accessed via placementInfo().ingredients() (ShapelessRecipe.ingredients is package-private)
 * - result accessed via assemble() (result field is package-private)
 * - Ingredient.CODEC replaces CODEC_NONEMPTY
 * - Ingredient.of() replaces Ingredient.EMPTY
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

    /** Get the result ItemStack from a ShapelessRecipe subclass (result field is package-private). */
    private static ItemStack getResult(ShapelessRecipe r) {
        // assemble() on a ShapelessRecipe returns result.copy(); safe for serialization
        return r.assemble(CraftingInput.EMPTY, null);
    }

    /** Get the ingredients from a ShapelessRecipe subclass via placementInfo (public API). */
    private static List<Ingredient> getIngredients(ShapelessRecipe r) {
        return r.placementInfo().ingredients();
    }

    public static <T extends ShapelessRecipe> MapCodec<T> createMap(ShapelessConstructor<T> create) {
        int maxSize = ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth();
        return RecordCodecBuilder.mapCodec(
                p -> p.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ExtendableShapelessSerializer::getResult),
                        Ingredient.CODEC.listOf(1, maxSize).fieldOf("ingredients")
                                .forGetter(ExtendableShapelessSerializer::getIngredients)
                ).apply(p, (group, category, result, ingredients) ->
                        create.create(group, category, result, NonNullList.copyOf(ingredients))
                )
        );
    }

    public static <T extends ShapelessRecipe> StreamCodec<RegistryFriendlyByteBuf, T> createStream(ShapelessConstructor<T> create) {
        return StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapelessRecipe::group,
                CraftingBookCategory.STREAM_CODEC, ShapelessRecipe::category,
                ItemStack.OPTIONAL_STREAM_CODEC, ExtendableShapelessSerializer::getResult,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                ExtendableShapelessSerializer::getIngredients,
                (group, category, result, ingredients) ->
                        create.create(group, category, result, NonNullList.copyOf(ingredients))
        );
    }

    @FunctionalInterface
    public interface ShapelessConstructor<T> {
        T create(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients);
    }
}
