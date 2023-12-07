package com.hollingsworth.arsnouveau.api.spell;

public interface IContextManipulator {
    enum EscapeResult{ESCAPE, IGNORE, ERROR}
    default EscapeResult CanEscape(AbstractEffect effect){
        return EscapeResult.ESCAPE;
    }
    SpellContext manipulate(SpellContext context, boolean filterSucceeded);
}
