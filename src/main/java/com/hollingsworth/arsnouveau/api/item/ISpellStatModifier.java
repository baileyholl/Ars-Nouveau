package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;

public interface ISpellStatModifier {
    /**
     * Adjust spell stats with this interface as it pertains to a specific spellPart, usually an effect.
     */
    SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart);
}
