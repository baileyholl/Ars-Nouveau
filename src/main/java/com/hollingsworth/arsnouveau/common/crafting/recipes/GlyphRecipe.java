package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class GlyphRecipe implements Recipe<ScribesTile> {

    public final ItemStack output;
    public final List<Ingredient> inputs;
    public final int exp;

    public GlyphRecipe(ItemStack output, List<Ingredient> inputs, int exp) {
        this.output = output;
        this.inputs = inputs;
        this.exp = exp;
    }

    public GlyphRecipe withIngredient(Ingredient i) {
        this.inputs.add(i);
        return this;
    }

    public GlyphRecipe withIngredient(Ingredient ingredient, int count) {
        for (int i = 0; i < count; i++) {
            withIngredient(ingredient);
        }
        return this;
    }

    public GlyphRecipe withIngredient(TagKey<Item> tag, int count) {
        for (int i = 0; i < count; i++) {
            withIngredient(Ingredient.of(tag));
        }
        return this;
    }


    public GlyphRecipe withItem(ItemLike i) {
        this.inputs.add(Ingredient.of(i));
        return this;
    }

    public GlyphRecipe withItem(ItemLike item, int count) {
        for (int i = 0; i < count; i++) {
            withItem(item);
        }
        return this;
    }

    public GlyphRecipe withStack(ItemStack i) {
        this.inputs.add(Ingredient.of(i));
        return this;
    }

    public GlyphRecipe withStack(ItemStack stack, int count) {
        for (int i = 0; i < count; i++) {
            withStack(stack);
        }
        return this;
    }

    public AbstractSpellPart getSpellPart() {
        return ((Glyph) this.output.getItem()).spellPart;
    }

    @Override
    public boolean matches(ScribesTile pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(ScribesTile p_345149_, HolderLookup.Provider p_346030_) {
        return output.copy();
    }


    public ItemStack getOutput() {
        return output;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    public int getExp() {
        return exp;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.GLYPH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.GLYPH_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GlyphRecipe> {

        public static void toNetwork(RegistryFriendlyByteBuf buffer, GlyphRecipe recipe) {
            buffer.writeInt(recipe.inputs.size());
            for (Ingredient i : recipe.inputs) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, i);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            buffer.writeInt(recipe.exp);
        }

        public static GlyphRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                stacks.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            return new GlyphRecipe(ItemStack.STREAM_CODEC.decode(buffer), stacks, buffer.readInt());
        }

        public static final MapCodec<GlyphRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("output").forGetter(GlyphRecipe::getOutput),
                Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(GlyphRecipe::getInputs),
                Codec.INT.fieldOf("exp").forGetter(GlyphRecipe::getExp)
        ).apply(instance, GlyphRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GlyphRecipe> STREAM_CODEC = StreamCodec.of(
                GlyphRecipe.Serializer::toNetwork, GlyphRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<GlyphRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GlyphRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
