package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SummonRitualRecipe implements SpecialSingleInputRecipe {

    public final Ingredient catalyst;
    public final MobSource mobSource;
    public final int count;
    public final List<WeightedMobType> mobs;

    public SummonRitualRecipe(Ingredient catalyst, MobSource source, int count, List<WeightedMobType> mobs) {
        this.catalyst = catalyst;
        this.mobSource = source;
        this.count = count;
        this.mobs = mobs;
    }


    public SummonRitualRecipe(Ingredient catalyst, String source, int count, List<WeightedMobType> mobs) {
        this(catalyst, MobSource.valueOf(source), count, mobs);
    }

    public SummonRitualRecipe(Ingredient catalyst, MobSource source, int count) {
        this(catalyst, source, count, new ArrayList<>());
    }

    public boolean matches(List<ItemStack> augments) {
        return EnchantingApparatusRecipe.doItemsMatch(augments, Arrays.stream(this.catalyst.getItems()).map(Ingredient::of).toList());
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SUMMON_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SUMMON_RITUAL_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<SummonRitualRecipe> {

        public static final MapCodec<SummonRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("augment").forGetter(r -> r.catalyst),
                Codec.STRING.fieldOf("mobSource").fieldOf("source").forGetter(r -> r.mobSource.toString()),
                Codec.INT.fieldOf("count").forGetter(r -> r.count),
                Codec.list(WeightedMobType.CODEC.codec()).fieldOf("mobs").forGetter(r -> r.mobs)
        ).apply(instance, SummonRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SummonRitualRecipe> STREAM_CODEC = CheatSerializer.create(CODEC);

        @Override
        public MapCodec<SummonRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SummonRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    /**
     * A mob type with a weight and a chance to be selected for spawning
     *
     * @param mob    The mob to spawn
     * @param weight If there is more than one mob in the list, this is the chance that this mob will be selected
     */
    public record WeightedMobType(ResourceLocation mob, int weight) implements WeightedEntry {

        public WeightedMobType(ResourceLocation mob) {
            this(mob, 1);
        }

        public static final MapCodec<WeightedMobType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("mob").forGetter(WeightedMobType::mob),
                Codec.INT.fieldOf("weight").forGetter(WeightedMobType::weight)
        ).apply(instance, WeightedMobType::new));

        @Override
        public @NotNull Weight getWeight() {
            return Weight.of(this.weight);
        }
    }

    public enum MobSource {
        CURRENT_BIOME,
        MOB_LIST
    }

}