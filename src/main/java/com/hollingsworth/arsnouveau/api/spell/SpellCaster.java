package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class SpellCaster implements ISpellCaster{
    private Map<Integer, Spell> spells = new HashMap<>();

    private int slot;
    ItemStack stack;

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
        return 1;
    }

    @Override
    public int getCurrentSlot() {
        return slot;
    }

    @Override
    public void setCurrentSlot(int slot) {
        this.slot = slot;
        write(stack);
    }

    @Override
    public void setSpell(Spell spell, int slot) {
        this.spells.put(slot, spell);
        write(stack);
    }

    @Override
    public void setSpell(Spell spell) {
        this.spells.put(getCurrentSlot(), spell);
        write(stack);
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    public static SpellCaster deserialize(ItemStack stack){
        SpellCaster instance = new SpellCaster(stack);
        CompoundNBT tag = stack.getTag() != null ? stack.getTag() : new CompoundNBT();
        instance.slot = tag.getInt("current_slot");
        for(int i = 0; i < instance.getMaxSlots(); i++){
            if(tag.contains("spell_" + i)){
                instance.getSpells().put(i, Spell.deserialize(tag.getString("spell_" + i)));
            }
        }
        return instance;
    }

    public void write(ItemStack stack){
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("current_slot", getCurrentSlot());
        tag.putInt("max_slot", getMaxSlots());
        int i = 0;
        for(Integer s : getSpells().keySet()){
            tag.putString("spell_" + i, getSpells().get(s).serialize());
            i++;
        }
        stack.setTag(tag);
    }
}
