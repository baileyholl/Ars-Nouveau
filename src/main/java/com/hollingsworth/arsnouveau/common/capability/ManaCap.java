package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;

public class ManaCap implements IManaCap {

    private double mana;

    private int maxMana;

    private int glyphBonus;

    private int bookTier;


    public ManaCap() {}

    @Override
    public double getCurrentMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    @Override
    public double setMana(double mana) {
        if (mana > getMaxMana()) {
            this.mana = getMaxMana();
        } else if (mana < 0) {
            this.mana = 0;
        } else {
            this.mana = mana;
        }
        return this.getCurrentMana();
    }

    @Override
    public double addMana(double manaToAdd) {
        this.setMana(this.getCurrentMana() + manaToAdd);
        return this.getCurrentMana();
    }

    @Override
    public double removeMana(double manaToRemove) {
        if (manaToRemove < 0)
            manaToRemove = 0;
        this.setMana(this.getCurrentMana() - manaToRemove);
        return this.getCurrentMana();
    }

    @Override
    public int getGlyphBonus() {
        return glyphBonus;
    }

    @Override
    public void setGlyphBonus(int glyphBonus) {
        this.glyphBonus = glyphBonus;
    }

    @Override
    public int getBookTier() {
        return bookTier;
    }

    @Override
    public void setBookTier(int bookTier) {
        this.bookTier = bookTier;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("current", getCurrentMana());
        tag.putInt("max", getMaxMana());
        tag.putInt("glyph", getGlyphBonus());
        tag.putInt("book_tier", getBookTier());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        setMaxMana(tag.getInt("max"));
        setMana(tag.getDouble("current"));
        setBookTier(tag.getInt("book_tier"));
        setGlyphBonus(tag.getInt("glyph"));
    }
}
