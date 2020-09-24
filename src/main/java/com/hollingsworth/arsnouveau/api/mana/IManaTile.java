package com.hollingsworth.arsnouveau.api.mana;

/**
 * Interface for a generic tile that holds mana.
 */
public interface IManaTile extends IMana{

    int getTransferRate();

    boolean canAcceptMana();
}
