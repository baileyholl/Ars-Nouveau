package com.hollingsworth.arsnouveau.api.spell;

public interface ISpellTier {

    enum Tier{
        ONE,
        TWO,
        THREE
    }

    /**
     * Get the tier of spells this thing can access
     */
    Tier getTier();


}
