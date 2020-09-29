package com.hollingsworth.arsnouveau.api.mana;

/**
 * A minimum representation of a Mana holding object or entity.
 */
public interface IMana {

    int getCurrentMana();

    int getMaxMana();

    void setMaxMana(int max);

    int setMana(final int mana);

    int addMana(final int manaToAdd);

    int removeMana(final int manaToRemove);

    default int getGlyphBonus(){
        return 0;
    }

    default int getBookTier(){
        return 0;
    }

    default void setGlyphBonus(int bonus){}

    default void setBookTier(int tier){};
}
