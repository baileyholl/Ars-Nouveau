package com.hollingsworth.arsnouveau.api.spell;

import java.util.Map;

public interface ISpellCaster {

    Spell getSpell();

    Spell getSpell(int slot);

    int getMaxSlots();

    int getCurrentSlot();

    void setCurrentSlot(int slot);

    void setSpell(Spell spell, int slot);

    void setSpell(Spell spell);

    Map<Integer, Spell> getSpells();
}
