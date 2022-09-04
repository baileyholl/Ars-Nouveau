package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArmorPerkHolder extends StackPerkHolder {

    private String color;
    private int tier;
    private List<List<PerkSlot>> slotsForTier;

    public ArmorPerkHolder(ItemStack stack, List<List<PerkSlot>> slotsForTier) {
        super(stack);
        CompoundTag tag = getItemTag(stack);
        this.slotsForTier = slotsForTier;
        if (tag == null)
            return;
        color = tag.getString("color");
        tier = tag.getInt("tier");
    }

    public String getColor() {
        return color == null ? DyeColor.PURPLE.getName() : color;
    }

    public void setColor(String color) {
        this.color = color;
        writeItem();
    }

    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
        writeItem();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (color != null)
            tag.putString("color", color);
        tag.putInt("tier", tier);
    }

    @Override
    public List<PerkSlot> getSlotsForTier() {
        List<PerkSlot> slots = new ArrayList<>(slotsForTier.get(tier));
        slots.sort(Comparator.comparingInt((a) -> -a.value));
        return slots;
    }
}
