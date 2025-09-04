package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public interface IResolveListener {

    default ResolveStatus onPreResolve(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
        return ResolveStatus.CONTINUE;
    }

    default void onPostResolve(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {

    }


    enum ResolveStatus {
        /* Continue the effect resolution as normal */
        CONTINUE,
        /* Skip the resolution of the current effect and continue the spell*/
        CONSUME,
        /* Stop the spell resolution entirely */
        STOP_ALL
    }
}
