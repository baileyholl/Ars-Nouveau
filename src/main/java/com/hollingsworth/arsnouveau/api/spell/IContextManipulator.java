package com.hollingsworth.arsnouveau.api.spell;

public interface IContextManipulator {
    default boolean CanEscape(AbstractEffect effect){
        return true;
    }
    SpellContext manipulate(SpellContext context, boolean filterSucceeded);
}
