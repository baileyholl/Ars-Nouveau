package com.hollingsworth.craftedmagic.api.mana;

public interface IMana {

    int getCurrentMana();

    int getMaxMana();

    void setMaxMana(int max);

    void setMana(final int mana);

    int addMana(final int manaToAdd);

    int removeMana(final int manaToRemove);

    /**
     * Sync mana with observing clients
     */
    void synchronise();
}
