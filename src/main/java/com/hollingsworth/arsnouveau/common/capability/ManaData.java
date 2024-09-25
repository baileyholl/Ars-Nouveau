package com.hollingsworth.arsnouveau.common.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class ManaData implements INBTSerializable<CompoundTag> {

    private double mana;

    private int maxMana;

    private int glyphBonus;

    private int bookTier;

    private float reservedMana;


    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getGlyphBonus() {
        return glyphBonus;
    }

    public void setGlyphBonus(int glyphBonus) {
        this.glyphBonus = glyphBonus;
    }

    public int getBookTier() {
        return bookTier;
    }

    public void setBookTier(int bookTier) {
        this.bookTier = bookTier;
    }

    public float getReservedMana() {
        return reservedMana;
    }

    public void setReservedMana(float reservedMana) {
        this.reservedMana = reservedMana;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("current", getMana());
        tag.putInt("max", getMaxMana());
        tag.putInt("glyph", getGlyphBonus());
        tag.putInt("book_tier", getBookTier());
        tag.putFloat("reserved", getReservedMana());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        setMaxMana(tag.getInt("max"));
        setMana(tag.getDouble("current"));
        setBookTier(tag.getInt("book_tier"));
        setGlyphBonus(tag.getInt("glyph"));
        setReservedMana(tag.getFloat("reserved"));
    }
}
