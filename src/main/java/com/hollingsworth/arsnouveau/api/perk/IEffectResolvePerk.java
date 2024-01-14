package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
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
    default void onPreResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance){

    }

    @Deprecated(forRemoval = true)
    default void onPostResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance){

    }

    default void onSpellCast(SpellCastEvent spellCastEvent, PerkInstance perkInstance){

    }

    default void onSpellPreResolve(SpellResolveEvent.Pre spellResolveEvent, PerkInstance perkInstance){

    }

    default void onSpellPostResolve(SpellResolveEvent.Post spellResolveEvent, PerkInstance perkInstance){

    }

    default void onEffectPreResolve(EffectResolveEvent.Pre event, PerkInstance perkInstance){
        onPreResolve(event.rayTraceResult, event.world, event.shooter, event.spellStats, event.context, event.resolver, event.resolveEffect, perkInstance);
    }

    default void onEffectPostResolve(EffectResolveEvent.Post event, PerkInstance perkInstance){
        onPostResolve(event.rayTraceResult, event.world, event.shooter, event.spellStats, event.context, event.resolver, event.resolveEffect, perkInstance);
    }

    default void onPreSpellDamageEvent(final SpellDamageEvent.Pre event, PerkInstance perkInstance){

    }

    default void onPostSpellDamageEvent(final SpellDamageEvent.Post event, PerkInstance perkInstance){

    }
}
