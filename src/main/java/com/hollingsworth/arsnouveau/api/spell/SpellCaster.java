package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SpellCaster implements ISpellCaster{
    private Map<Integer, Spell> spells = new HashMap<>();

    private int slot;

    private int maxSlots;

    public final ItemStack stack;

    public SpellCaster(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public Spell getSpell() {
        return spells.get(getCurrentSlot());
    }

    @Override
    public Spell getSpell(int slot) {
        return spells.get(slot);
    }

    @Override
    public int getMaxSlots() {
        return maxSlots;
    }

    @Override
    public void setMaxSlots(int slots) {
        this.maxSlots = slots;
    }

    @Override
    public int getCurrentSlot() {
        return slot;
    }

    @Override
    public void setCurrentSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void setSpell(Spell spell, int slot) {
        this.spells.put(slot, spell);
    }

    @Override
    public void setSpell(Spell spell) {
        this.spells.put(getCurrentSlot(), spell);
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }
}
