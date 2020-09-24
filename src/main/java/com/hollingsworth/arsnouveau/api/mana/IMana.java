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
}
