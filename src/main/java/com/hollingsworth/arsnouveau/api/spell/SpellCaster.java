package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class SpellCaster implements ISpellCaster{
    private Map<Integer, Spell> spells = new HashMap<>();

    private Map<Integer, String> spellNames = new HashMap<>();

    private Map<Integer, ParticleColor.IntWrapper> spellColors = new HashMap<>();

    private Map<Integer, ConfiguredSpellSound> spellSounds = new HashMap<>();

    private int slot;
    public ItemStack stack = ItemStack.EMPTY;
    public String flavorText = "";

    public SpellCaster(ItemStack stack){
        this(stack.getOrCreateTag());
        this.stack = stack;
    }

    @Nonnull
    @Override
    public Spell getSpell() {
        return spells.getOrDefault(getCurrentSlot(), Spell.EMPTY);
    }

    @Override
    public @Nonnull Spell getSpell(int slot) {
        return spells.getOrDefault(slot, Spell.EMPTY);
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
        writeItem(stack);
    }

    @Override
    public void setSpell(Spell spell, int slot) {
        this.spells.put(slot, spell);
        writeItem(stack);
    }

    @Override
    public void setSpell(Spell spell) {
        this.spells.put(getCurrentSlot(), spell);
        writeItem(stack);
    }

    @NotNull
    @Override
    public ParticleColor.IntWrapper getColor(int slot) {
        return this.spellColors.getOrDefault(slot, ParticleUtil.defaultParticleColorWrapper());
    }

    @Override
    public void setFlavorText(String str) {
        this.flavorText = str;
        writeItem(stack);
    }

    @Override
    public String getSpellName(int slot) {
        return this.spellNames.getOrDefault(slot, "");
    }

    @Override
    public String getSpellName() {
        return this.spellNames.getOrDefault(getCurrentSlot(), "");
    }

    @Override
    public void setSpellName(String name) {
        this.spellNames.put(getCurrentSlot(), name);
        writeItem(stack);
    }

    @Override
    public void setSpellName(String name, int slot) {
        this.spellNames.put(slot, name);
        writeItem(stack);
    }

    @Override
    public String getFlavorText() {
        return flavorText == null ? "" : flavorText;
    }

    @Override
    public void setColor(ParticleColor.IntWrapper color) {
        this.spellColors.put(getCurrentSlot(), color);
        writeItem(stack);
    }

    @Override
    public void setColor(ParticleColor.IntWrapper color, int slot) {
        this.spellColors.put(slot, color);
        writeItem(stack);
    }

    @Nonnull
    @Override
    public ConfiguredSpellSound getSound(int slot) {
        return this.spellSounds.get(slot) == null ? ConfiguredSpellSound.DEFAULT : this.spellSounds.get(slot);
    }

    @Override
    public void setSound(ConfiguredSpellSound sound, int slot) {
        this.spellSounds.put(slot, sound);
        writeItem(stack);
    }

    @Nonnull
    @Override
    public ParticleColor.IntWrapper getColor() {
        return this.spellColors.getOrDefault(getCurrentSlot(), ParticleUtil.defaultParticleColorWrapper());
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    @Override
    public Map<Integer, String> getSpellNames() {
        return this.spellNames;
    }

    @Override
    public Map<Integer, ParticleColor.IntWrapper> getColors() {
        return this.spellColors;
    }

    public CompoundTag writeTag(CompoundTag tag){
        tag.putInt("current_slot", getCurrentSlot());
        tag.putString("flavor", getFlavorText());

        for(int i = 0; i < getMaxSlots() + 1; i++){
            tag.putString("spell_" + i, getSpell(i).serialize());
            tag.putString("spell_name_" + i, getSpellName(i));
            tag.putString("spell_color_" + i, getColor(i).serialize());
            tag.put("spell_sound_" + i, getSound(i).serialize());
        }
        return tag;
    }

    public SpellCaster(CompoundTag itemTag){
        CompoundTag tag = itemTag.getCompound(getTagID());

        this.slot = tag.contains("current_slot") ? tag.getInt("current_slot") : 1;
        this.flavorText = tag.getString("flavor");
        for(int i = 0; i < this.getMaxSlots() + 1; i++){
            if(tag.contains("spell_" + i)){
                this.setSpell(Spell.deserialize(tag.getString("spell_" + i)), i);
            }
            if(tag.contains("spell_name_" + i)){
                this.setSpellName(tag.getString("spell_name_" + i), i);
            }

            if(tag.contains("spell_color_" + i)){
                this.setColor(ParticleColor.IntWrapper.deserialize(tag.getString("spell_color_" + i)), i);
            }
            if(tag.contains("spell_sound_" + i)){
                this.setSound(ConfiguredSpellSound.fromTag(tag.getCompound("spell_sound_" + i)), i);
            }
        }
    }

    public void writeItem(ItemStack stack){
        if(stack == null || stack.isEmpty()){
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag casterTag = new CompoundTag(); // Nest our tags so we dont cause conflicts
        writeTag(casterTag);
        tag.put(getTagID(), casterTag);
        stack.setTag(tag);
    }

    /**
     * Writes this compound data to the provided tag, stored with the caster ID.
     * @param tag The tag to add this serialized tag to.
     */
    public void serializeOnTag(CompoundTag tag){
        CompoundTag thisData = writeTag(new CompoundTag());
        tag.put(getTagID(), thisData);
    }

    @Override
    public String getTagID() {
        return "ars_nouveau_spellCaster";
    }
}
