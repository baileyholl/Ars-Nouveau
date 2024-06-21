package com.hollingsworth.arsnouveau.api.mana;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IManaCap extends INBTSerializable<CompoundTag> {

    double getCurrentMana();

    int getMaxMana();

    void setMaxMana(int max);

    double setMana(final double mana);

    double addMana(final double manaToAdd);

    double removeMana(final double manaToRemove);

    default int getGlyphBonus() {
        return 0;
    }

    default int getBookTier() {
        return 0;
    }

    default void setGlyphBonus(int bonus) {
    }

    default void setBookTier(int tier) {
    }
}
