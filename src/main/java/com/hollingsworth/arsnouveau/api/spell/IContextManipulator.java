package com.hollingsworth.arsnouveau.api.spell;

public interface IContextManipulator {

    /**
     * most context manipulators, such as linger or burst, should return true so they work with context escape.
     * context manipulators that directly continue the spell, such as delay, should return false to not waste glyph slots or confuse players
     */

    public boolean isEscapable();
}
