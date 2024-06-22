package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpellCaster implements ISpellCaster {

    private Map<Integer, Spell> spells = new HashMap<>();
    private int slot;
    public ItemStack stack = ItemStack.EMPTY;
    public String flavorText = "";
    public boolean isHidden;
    public String hiddenText = "";

    public SpellCaster(ItemStack stack) {
        this(stack.getOrCreateTag());
        this.stack = stack;
    }

    @NotNull
    @Override
    public Spell getSpell() {
        return spells.getOrDefault(getCurrentSlot(), new Spell());
    }

    @Override
    public@NotNull Spell getSpell(int slot) {
        return spells.getOrDefault(slot, new Spell());
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
        setSpell(spell, getCurrentSlot());
    }

    @Override
    public ParticleColor getColor(int slot) {
        return this.getSpell(slot).color;
    }

    @Override
    public void setFlavorText(String str) {
        this.flavorText = str;
        writeItem(stack);
    }

    @Override
    public String getSpellName(int slot) {
        return this.getSpell(slot).name;
    }

    @Override
    public String getSpellName() {
        return this.getSpellName(getCurrentSlot());
    }

    @Override
    public void setSpellName(String name) {
        setSpellName(name, getCurrentSlot());
    }

    @Override
    public void setSpellName(String name, int slot) {
        this.getSpell(slot).name = name;
        writeItem(stack);
    }

    @Override
    public void setSpellHidden(boolean hidden) {
        this.isHidden = hidden;
        writeItem(stack);
    }

    @Override
    public boolean isSpellHidden() {
        return isHidden;
    }

    @Override
    public void setHiddenRecipe(String recipe) {
        this.hiddenText = recipe;
        writeItem(stack);
    }

    @Override
    public String getHiddenRecipe() {
        return hiddenText;
    }

    @Override
    public String getFlavorText() {
        return flavorText == null ? "" : flavorText;
    }

    @Override
    public void setColor(ParticleColor color) {
        setColor(color, getCurrentSlot());
    }

    @Override
    public void setColor(ParticleColor color, int slot) {
        this.getSpell(slot).color = color;
        writeItem(stack);
    }

   @NotNull
    @Override
    public ConfiguredSpellSound getSound(int slot) {
        return this.getSpell(slot).sound;
    }

    @Override
    public void setSound(ConfiguredSpellSound sound) {
        this.setSound(sound, getCurrentSlot());
    }

    @Override
    public void setSound(ConfiguredSpellSound sound, int slot) {
        this.getSpell(slot).sound = sound;
        writeItem(stack);
    }

   @NotNull
    @Override
    public ParticleColor getColor() {
        return this.getSpell().color;
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    public CompoundTag writeTag(CompoundTag tag) {
        tag.putInt("current_slot", getCurrentSlot());
        tag.putString("flavor", getFlavorText());
        CompoundTag spellTag = new CompoundTag();

        for (int i = 0; i < getMaxSlots(); i++) {
            Spell spell = getSpell(i);
            spellTag.put("spell" + i, spell.serialize());
        }
        tag.put("spells", spellTag);
        tag.putInt("spell_count", getSpells().size());
        tag.putBoolean("is_hidden", isSpellHidden());
        tag.putString("hidden_recipe", getHiddenRecipe());
        return tag;
    }

    public SpellCaster(CompoundTag itemTag) {
        CompoundTag tag = itemTag.getCompound(getTagID().toString());

        this.slot = tag.getInt("current_slot");
        this.flavorText = tag.getString("flavor");
        this.isHidden = tag.getBoolean("is_hidden");
        this.hiddenText = tag.getString("hidden_recipe");
        CompoundTag spellTag = tag.getCompound("spells");
        for (int i = 0; i < getMaxSlots(); i++) {
            if (spellTag.contains("spell" + i)) {
                Spell spell = Spell.fromTag(spellTag.getCompound("spell" + i));
                spells.put(i, spell);
            }
        }
    }

    public void writeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag casterTag = new CompoundTag(); // Nest our tags so we dont cause conflicts
        writeTag(casterTag);
        tag.put(getTagID().toString(), casterTag);
        stack.setTag(tag);
    }

    /**
     * Writes this compound data to the provided tag, stored with the caster ID.
     *
     * @param tag The tag to add this serialized tag to.
     */
    public void serializeOnTag(CompoundTag tag) {
        CompoundTag thisData = writeTag(new CompoundTag());
        tag.put(getTagID().toString(), thisData);
    }

    @Override
    public ResourceLocation getTagID() {
        return ArsNouveau.prefix( "caster");
    }
}
