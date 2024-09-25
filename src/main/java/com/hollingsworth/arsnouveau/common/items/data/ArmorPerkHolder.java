package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ArmorPerkHolder extends StackPerkHolder<ArmorPerkHolder> {

    public static final Codec<ArmorPerkHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("color").forGetter(ArmorPerkHolder::getColor),
            StackPerkHolder.PERK_CODEC.listOf().fieldOf("perks").forGetter(ArmorPerkHolder::getPerks),
            Codec.INT.fieldOf("tier").forGetter(ArmorPerkHolder::getTier),
            PerkMap.CODEC.fieldOf("perkTags").forGetter(ArmorPerkHolder::getPerkTags)
    ).apply(instance, ArmorPerkHolder::new));

    private String color;

    public ArmorPerkHolder(String color, List<IPerk> perks, int tier, Map<IPerk, CompoundTag> perkTags) {
        super(perks, tier, perkTags);
        this.color = color;
    }

    public ArmorPerkHolder(String color, List<IPerk> perks, int tier, PerkMap perkTags) {
        super(perks, tier, perkTags);
        this.color = color;
    }

    public ArmorPerkHolder() {
        this("", new ArrayList<>(), 0, new HashMap<>());
    }

    public String getColor() {
        return color == null ? DyeColor.PURPLE.getName() : color;
    }

    public ArmorPerkHolder setColor(String color) {
        return new ArmorPerkHolder(color, getPerks(), getTier(), getPerkTags());
    }

    @Override
    public ArmorPerkHolder setPerks(List<IPerk> perks) {
        return new ArmorPerkHolder(color, perks, getTier(), getPerkTags());
    }

    @Override
    public List<PerkSlot> getSlotsForTier(ItemStack stack) {
        List<List<PerkSlot>> slotsForTier = PerkRegistry.getPerkProvider(stack.getItem());
        if (slotsForTier == null) {
            return Collections.emptyList();
        }
        List<PerkSlot> slots = new ArrayList<>(slotsForTier.get(getTier()));
        slots.sort(Comparator.comparingInt((a) -> -a.value()));
        return slots;
    }

    @Override
    public ArmorPerkHolder setTier(int tier) {
        return new ArmorPerkHolder(color, getPerks(), tier, getPerkTags());
    }

    @Override
    public ArmorPerkHolder setTagForPerk(IPerk perk, CompoundTag tag) {
        return new ArmorPerkHolder(color, getPerks(), getTier(), getPerkTags().put(perk, tag));
    }

    public ArmorPerkHolder setPerkTags(Map<IPerk, CompoundTag> perkTags) {
        return new ArmorPerkHolder(color, getPerks(), getTier(), perkTags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ArmorPerkHolder that = (ArmorPerkHolder) o;
        return Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color);
    }
}
