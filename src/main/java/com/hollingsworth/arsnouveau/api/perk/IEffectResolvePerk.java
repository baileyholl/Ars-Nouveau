package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;

public interface IEffectResolvePerk {

    default void onSpellCast(SpellCastEvent spellCastEvent, PerkInstance perkInstance){

    }

    default void onSpellPreResolve(SpellResolveEvent.Pre spellResolveEvent, PerkInstance perkInstance){

    }

    default void onSpellPostResolve(SpellResolveEvent.Post spellResolveEvent, PerkInstance perkInstance){

    }

    default void onEffectPreResolve(EffectResolveEvent.Pre event, PerkInstance perkInstance){
    }

    default void onEffectPostResolve(EffectResolveEvent.Post event, PerkInstance perkInstance){
    }

    default void onPreSpellDamageEvent(final SpellDamageEvent.Pre event, PerkInstance perkInstance){

    }

    default void onPostSpellDamageEvent(final SpellDamageEvent.Post event, PerkInstance perkInstance){

    }
}
