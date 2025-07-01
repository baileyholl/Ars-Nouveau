package com.hollingsworth.arsnouveau.common.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class ManaData implements INBTSerializable<CompoundTag> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ManaData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ManaData::getMana,
            ByteBufCodecs.INT, ManaData::getMaxMana,
            ByteBufCodecs.FLOAT, ManaData::getReservedMana,
            ByteBufCodecs.INT, ManaData::getGlyphBonus,
            ByteBufCodecs.INT, ManaData::getBookTier,
            (mana, max, reserved, glyph, bookTier) -> {
                var instance = new ManaData();
                instance.setMana(mana);
                instance.setMaxMana(max);
                instance.setReservedMana(reserved);
                instance.setGlyphBonus(glyph);
                instance.setBookTier(bookTier);
                return instance;
            }
    );

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
