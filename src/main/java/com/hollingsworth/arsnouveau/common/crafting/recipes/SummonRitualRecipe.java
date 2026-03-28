package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class SummonRitualRecipe implements SpecialSingleInputRecipe {

    public final Ingredient augment;
    public final MobSource mobSource;
    public final int count;
    public final WeightedList<WeightedMobType> mobs;

    public SummonRitualRecipe(Ingredient augment, MobSource source, int count, WeightedList<WeightedMobType> mobs) {
        this.augment = augment;
        this.mobSource = source;
        this.count = count;
        this.mobs = mobs;
    }

    public SummonRitualRecipe(Ingredient augment, String source, int count, List<WeightedMobType> mobs) {
        this.augment = augment;
        this.mobSource = MobSource.valueOf(source);
        this.count = count;
        this.mobs = WeightedList.of(mobs.stream().map(m -> new Weighted<>(m, m.weight())).toList());
    }

    public SummonRitualRecipe(Ingredient augment, String source, int count, WeightedList<WeightedMobType> mobs) {
        this(augment, MobSource.valueOf(source), count, mobs);
    }

    public SummonRitualRecipe(Ingredient augment, MobSource source, int count) {
        this(augment, source, count, WeightedList.of());
    }

    public boolean matches(List<ItemStack> augments) {
        return EnchantingApparatusRecipe.doItemsMatch(augments, List.of(this.augment));
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<SummonRitualRecipe> getSerializer() {
        return (RecipeSerializer<SummonRitualRecipe>) RecipeRegistry.SUMMON_RITUAL_SERIALIZER.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<SummonRitualRecipe> getType() {
        return (RecipeType<SummonRitualRecipe>) RecipeRegistry.SUMMON_RITUAL_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<SummonRitualRecipe> {

        public static final MapCodec<SummonRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("augment").forGetter(r -> r.augment),
                Codec.STRING.fieldOf("mobSource").fieldOf("source").forGetter(r -> r.mobSource.toString()),
                Codec.INT.fieldOf("count").forGetter(r -> r.count),
                Codec.list(WeightedMobType.CODEC.codec()).fieldOf("mobs").forGetter(r -> r.mobs.unwrap().stream().map(Weighted::value).toList())
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
    public record WeightedMobType(Identifier mob, int weight) {

        public WeightedMobType(Identifier mob) {
            this(mob, 1);
        }

        public static final MapCodec<WeightedMobType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("mob").forGetter(WeightedMobType::mob),
                Codec.INT.fieldOf("weight").forGetter(WeightedMobType::weight)
        ).apply(instance, WeightedMobType::new));
    }

    public enum MobSource {
        CURRENT_BIOME,
        MOB_LIST
    }

}