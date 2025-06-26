package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.items.data.ReactiveCasterData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
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

import java.util.List;

public class ReactiveEnchantmentRecipe extends EnchantmentRecipe {

    public ReactiveEnchantmentRecipe(List<Ingredient> pedestalItems, int sourceCost) {
        super(pedestalItems, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 1, sourceCost);
    }

    @Override
    public @NotNull ItemStack assemble(ApparatusRecipeInput input, HolderLookup.@NotNull Provider lookup) {
        ItemStack result = super.assemble(input, lookup);
        ItemStack parchment = getParchment(input.pedestals());
        AbstractCaster<?> parchmentCaster = SpellCasterRegistry.from(parchment);
        result.set(DataComponentRegistry.REACTIVE_CASTER, new ReactiveCasterData(0, "", false, "", 1).setSpell(parchmentCaster.getSpell()));
        return result;
    }

    @Override
    public boolean matches(ApparatusRecipeInput input, Level level, @org.jetbrains.annotations.Nullable Player player) {
        ItemStack parchment = getParchment(input.pedestals());
        if (parchment.isEmpty() || SpellCasterRegistry.from(parchment).getSpell().isEmpty()) {
            return false;
        }
        return super.matches(input, level, player);
    }

    public static @NotNull ItemStack getParchment(List<ItemStack> pedestalItems) {
        for (ItemStack stack : pedestalItems) {
            if (stack.getItem() instanceof SpellParchment) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.REACTIVE_TYPE.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.REACTIVE_RECIPE.get();
    }


    public static class Serializer implements RecipeSerializer<ReactiveEnchantmentRecipe> {

        public static final MapCodec<ReactiveEnchantmentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(ReactiveEnchantmentRecipe::pedestalItems),
                Codec.INT.fieldOf("sourceCost").forGetter(ReactiveEnchantmentRecipe::sourceCost)
        ).apply(instance, ReactiveEnchantmentRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ReactiveEnchantmentRecipe> STREAM = CheatSerializer.create(CODEC);

        @Override
        public @NotNull MapCodec<ReactiveEnchantmentRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ReactiveEnchantmentRecipe> streamCodec() {
            return STREAM;
        }
    }
}
