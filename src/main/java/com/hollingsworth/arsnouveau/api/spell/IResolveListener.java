package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IResolveListener {
    void onResolve(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver);
}
