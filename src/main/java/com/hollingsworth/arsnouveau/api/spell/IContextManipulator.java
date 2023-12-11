package com.hollingsworth.arsnouveau.api.spell;

public interface IContextManipulator {

    /**
     * Called before manipulate to determine if the context should be pushed or not.
     * Allows you to determine if you should push a new context or not. Returning false will not push a new context in SpellContext#makeChildContext
     */
    default boolean shouldPushContext(SpellContext context){
        return true;
    }

    /**
     * Allows you to manipulate the existing context or return a new context if shouldPushContext returns true
     */
    SpellContext manipulate(SpellContext context);
}
