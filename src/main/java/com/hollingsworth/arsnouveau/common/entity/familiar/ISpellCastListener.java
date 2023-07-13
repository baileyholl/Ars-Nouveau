package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;

public interface ISpellCastListener {

    default void onCast(SpellCastEvent event) {
    }

    default void onModifier(SpellModifierEvent event) {
    }

    default void onCostCalc(SpellCostCalcEvent event) {
    }

}
