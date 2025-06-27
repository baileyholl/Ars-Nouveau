package com.hollingsworth.arsnouveau.api.spell;

import java.util.HashSet;
import java.util.Set;

public interface IContextManipulator {

    /**
     * Called before manipulate to determine if the context should be pushed or not.
     * Allows you to determine if you should push a new context or not. Returning false will not push a new context in SpellContext#makeChildContext
     */
    default boolean shouldPushContext(SpellContext context) {
        return true;
    }

    /**
     * Allows you to manipulate the existing context or return a new context if shouldPushContext returns true
     */
    SpellContext manipulate(SpellContext context);

    /**
     * Resets the combination limit validator for this set of glpyhs if THIS glyph appears before them.
     */
    default Set<AbstractSpellPart> bypassCombinationLimitsFor() {
        return new HashSet<>();
    }

    /**
     * Resets the occurrence limit validator for this set of glpyhs if THIS glyph appears before them.
     */
    default Set<AbstractSpellPart> bypassOccurrenceLimitsFor() {
        return new HashSet<>();
    }
}
