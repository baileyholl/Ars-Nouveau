package com.hollingsworth.arsnouveau.api.mana;

/**
 * Interface for a generic tile that holds mana.
 */
public interface IManaTile{

    int getTransferRate();

    boolean canAcceptMana();

    int getCurrentMana();

    int getMaxMana();

    void setMaxMana(int max);

    int setMana(final int mana);

    int addMana(final int manaToAdd);

    int removeMana(final int manaToRemove);
}
