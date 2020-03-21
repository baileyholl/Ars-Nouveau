package com.hollingsworth.arsnouveau.api.mana;

public interface IMana {

    int getCurrentMana();

    int getMaxMana();

    void setMaxMana(int max);

    int setMana(final int mana);

    int addMana(final int manaToAdd);

    int removeMana(final int manaToRemove);
}
