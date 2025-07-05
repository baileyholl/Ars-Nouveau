package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.common.items.data.PrestidigitationData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hollingsworth.arsnouveau.common.crafting.recipes.ReactiveEnchantmentRecipe.getParchment;

public class PrestidigitationRecipe extends EnchantingApparatusRecipe {

    public PrestidigitationRecipe(List<Ingredient> pedestalItems, int cost) {
        super(Ingredient.EMPTY, ItemStack.EMPTY, pedestalItems, cost, true);
    }

    @Override
    public boolean excludeJei() {
        return true;
    }

    @Override
    public boolean doesReagentMatch(ApparatusRecipeInput input, Level level, @Nullable Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(ApparatusRecipeInput input, HolderLookup.@NotNull Provider p_346030_) {
        ItemStack parchment = getParchment(input.pedestals());
        AbstractCaster<?> caster = SpellCasterRegistry.from(parchment);
        ItemStack result = input.catalyst().copy();
        PrestidigitationData data = new PrestidigitationData(caster.getSpell().particleTimeline().get(ParticleTimelineRegistry.PRESTIDIGITATION_TIMELINE));
        result.set(DataComponentRegistry.PRESTIDIGITATION, data);
        return result;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.PRESTIDIGITATION_TYPE.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.PRESTIDIGITATION_SERIALIZER.get();
    }


    public static class Serializer implements RecipeSerializer<PrestidigitationRecipe> {

        public static final MapCodec<PrestidigitationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(PrestidigitationRecipe::pedestalItems),
                Codec.INT.fieldOf("sourceCost").forGetter(PrestidigitationRecipe::sourceCost)
        ).apply(instance, PrestidigitationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PrestidigitationRecipe> STREAM = CheatSerializer.create(CODEC);

        @Override
        public @NotNull MapCodec<PrestidigitationRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PrestidigitationRecipe> streamCodec() {
            return STREAM;
        }
    }
}
