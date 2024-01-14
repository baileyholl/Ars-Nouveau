package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public interface IEffectResolvePerk {

    @Deprecated(forRemoval = true)
    void onPreResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance);

    @Deprecated(forRemoval = true)
    void onPostResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance);

    default void onPreResolve(EffectResolveEvent.Pre event, PerkInstance perkInstance){
        onPreResolve(event.rayTraceResult, event.world, event.shooter, event.spellStats, event.context, event.resolver, event.resolveEffect, perkInstance);
    }

    default void onPostResolve(EffectResolveEvent.Post event, PerkInstance perkInstance){
        onPostResolve(event.rayTraceResult, event.world, event.shooter, event.spellStats, event.context, event.resolver, event.resolveEffect, perkInstance);
    }

    default void onPreSpellDamageEvent(final SpellDamageEvent.Pre event, PerkInstance perkInstance){

    }

    default void onPostSpellDamageEvent(final SpellDamageEvent.Post event, PerkInstance perkInstance){

    }
}
