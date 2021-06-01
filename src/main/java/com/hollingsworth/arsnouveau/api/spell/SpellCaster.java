package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class SpellCaster implements ISpellCaster{
    private Map<Integer, Spell> spells = new HashMap<>();

    private int slot;
    ItemStack stack;
    ParticleColor.IntWrapper color = ParticleUtil.defaultParticleColorWrapper();

    private SpellCaster(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public Spell getSpell() {
        return spells.getOrDefault(getCurrentSlot(), new Spell());
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
    public void setColor(ParticleColor.IntWrapper color) {
        this.color = color;
    }

    @Override
    public ParticleColor.IntWrapper getColor() {
        return color;
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
        instance.color = tag.getString("color").isEmpty() ? ParticleUtil.defaultParticleColorWrapper(): ParticleColor.IntWrapper.deserialize(tag.getString("color"));
        return instance;
    }

    public void write(ItemStack stack){
        CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
        tag.putInt("current_slot", getCurrentSlot());
        tag.putInt("max_slot", getMaxSlots());
        tag.putString("color", color.serialize());
        int i = 0;
        for(Integer s : getSpells().keySet()){
            tag.putString("spell_" + i, getSpells().get(s).serialize());
            i++;
        }
        stack.setTag(tag);
    }
}
