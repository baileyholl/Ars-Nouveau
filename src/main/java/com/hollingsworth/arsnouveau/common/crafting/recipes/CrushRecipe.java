package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record CrushRecipe(Ingredient input, List<CrushOutput> outputs,
                          boolean skipBlockPlace) implements SpecialSingleInputRecipe {

    public CrushRecipe(Ingredient input, List<CrushOutput> outputs) {
        this(input, outputs, false);
    }

    public List<ItemStack> getRolledOutputs(RandomSource random) {
        List<ItemStack> finalOutputs = new ArrayList<>();
        for (CrushOutput crushRoll : outputs) {
            if (random.nextDouble() <= crushRoll.chance) {
                if (crushRoll.maxRange > 1) {
                    // get a number between 1 and max
                    int num = random.nextInt(crushRoll.maxRange) + 1;
                    for (int i = 0; i < num; i++) {
                        finalOutputs.add(crushRoll.stack.copy());
                    }
                } else {
                    finalOutputs.add(crushRoll.stack.copy());
                }
            }
        }

        return finalOutputs;
    }

    public boolean shouldSkipBlockPlace() {
        return this.skipBlockPlace;
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return this.input.test(p_346065_.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CRUSH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CRUSH_TYPE.get();
    }

    public record CrushOutput(ItemStack stack, float chance, int maxRange) {

        public CrushOutput(ItemStack stack, float chance) {
            this(stack, chance, 1);
        }

        public static final Codec<CrushOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(CrushOutput::stack),
                Codec.FLOAT.fieldOf("chance").forGetter(CrushOutput::chance),
                Codec.INT.fieldOf("maxRange").forGetter(CrushOutput::maxRange)
        ).apply(instance, CrushOutput::new));
    }

    public static class Serializer implements RecipeSerializer<CrushRecipe> {

        public static final MapCodec<CrushRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(CrushRecipe::input),
                CrushOutput.CODEC.listOf().fieldOf("output").forGetter(CrushRecipe::outputs),
                Codec.BOOL.optionalFieldOf("skip_block_place", false).forGetter(CrushRecipe::shouldSkipBlockPlace)
        ).apply(instance, CrushRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrushRecipe> STREAM_CODEC = StreamCodec.of(
                CrushRecipe.Serializer::toNetwork, CrushRecipe.Serializer::fromNetwork
        );

        public static void toNetwork(RegistryFriendlyByteBuf buf, CrushRecipe recipe) {
            buf.writeInt(recipe.outputs.size());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            for (CrushOutput i : recipe.outputs) {
                buf.writeFloat(i.chance);
                ItemStack.STREAM_CODEC.encode(buf, i.stack);
                buf.writeInt(i.maxRange);
            }
            buf.writeBoolean(recipe.skipBlockPlace);
        }

        public static CrushRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            List<CrushOutput> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    float chance = buffer.readFloat();
                    ItemStack outStack = ItemStack.STREAM_CODEC.decode(buffer);
                    int maxRange = buffer.readInt();
                    stacks.add(new CrushOutput(outStack, chance, maxRange));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            boolean skipBlockPlace = buffer.readBoolean();
            return new CrushRecipe(input, stacks, skipBlockPlace);
        }

        @Override
        public MapCodec<CrushRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrushRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
