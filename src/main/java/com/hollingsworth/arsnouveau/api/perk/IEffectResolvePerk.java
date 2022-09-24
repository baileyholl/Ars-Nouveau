package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;

public interface IEffectResolvePerk {
    void onPreResolve(HitResult rayTraceResult, Level world, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance);

    void onPostResolve(HitResult rayTraceResult, Level world, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance);
}
