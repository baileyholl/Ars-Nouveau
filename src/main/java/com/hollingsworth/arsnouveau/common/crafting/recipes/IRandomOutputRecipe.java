package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IRandomOutputRecipe {
    void rollOutputs(Consumer<ItemStack> consumer, @Nullable SpellResolver resolver, @Nullable SpellStats spellStats, RandomSource random);

    boolean isOutputDeterministic();
}