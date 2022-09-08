package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArmorPerkHolder extends StackPerkHolder {

    private String color;
    private List<List<PerkSlot>> slotsForTier;

    public ArmorPerkHolder(ItemStack stack, List<List<PerkSlot>> slotsForTier) {
        super(stack);
        CompoundTag tag = getItemTag(stack);
        this.slotsForTier = slotsForTier;
        if (tag == null)
            return;
        color = tag.getString("color");
    }

    public String getColor() {
        return color == null ? DyeColor.PURPLE.getName() : color;
    }

    public void setColor(String color) {
        this.color = color;
        writeItem();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (color != null)
            tag.putString("color", color);
    }

    @Override
    public List<PerkSlot> getSlotsForTier() {
        List<PerkSlot> slots = new ArrayList<>(slotsForTier.get(getTier()));
        slots.sort(Comparator.comparingInt((a) -> -a.value));
        return slots;
    }
}
