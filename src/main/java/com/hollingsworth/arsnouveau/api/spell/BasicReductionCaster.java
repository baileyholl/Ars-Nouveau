package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BasicReductionCaster extends SpellCaster{

    Function<Spell, Spell> modificationFunc;

    public BasicReductionCaster(ItemStack stack, Function<Spell, Spell> modifyFunc) {
        super(stack);
    }

    public BasicReductionCaster(CompoundTag itemTag, Function<Spell, Spell> modifyFunc) {
        super(itemTag);
    }

    @Override
    public Spell modifySpellBeforeCasting(Level worldIn, @Nullable Entity playerIn, @Nullable InteractionHand handIn, Spell spell) {
        return modificationFunc.apply(spell);
    }
}
