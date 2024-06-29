package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ArmorPerkHolder extends StackPerkHolder<ArmorPerkHolder> {

    public static final Codec<ArmorPerkHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("color").forGetter(ArmorPerkHolder::getColor),
            PerkSlot.TIERED_LIST_CODEC.fieldOf("slotsForTier").forGetter(i -> i.slotsForTier),
            StackPerkHolder.PERK_CODEC.listOf().fieldOf("perks").forGetter(ArmorPerkHolder::getPerks),
            Codec.INT.fieldOf("tier").forGetter(ArmorPerkHolder::getTier),
            StackPerkHolder.PERK_TAG_CODEC.fieldOf("perkTags").forGetter(ArmorPerkHolder::getPerkTags)
    ).apply(instance, ArmorPerkHolder::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorPerkHolder> STREAM_CODEC = CheatSerializer.create(CODEC);

    private String color;
    private List<List<PerkSlot>> slotsForTier;

    public ArmorPerkHolder(String color, List<List<PerkSlot>> slotsForTier, List<IPerk> perks, int tier, Map<IPerk, CompoundTag> perkTags) {
        super(perks, tier, perkTags);
        this.color = color;
        this.slotsForTier = List.copyOf(slotsForTier);
    }

    public String getColor() {
        return color == null ? DyeColor.PURPLE.getName() : color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public ArmorPerkHolder setPerks(List<IPerk> perks) {
        return new ArmorPerkHolder(color, slotsForTier, perks, getTier(), getPerkTags());
    }

    @Override
    public List<PerkSlot> getSlotsForTier() {
        List<PerkSlot> slots = new ArrayList<>(slotsForTier.get(getTier()));
        slots.sort(Comparator.comparingInt((a) -> -a.value()));
        return slots;
    }

    @Override
    public ArmorPerkHolder setTier(int tier) {
        return new ArmorPerkHolder(color, slotsForTier, getPerks(), tier, getPerkTags());
    }

    @Override
    public ArmorPerkHolder setTagForPerk(IPerk perk, CompoundTag tag) {
        return new ArmorPerkHolder(color, slotsForTier, getPerks(), getTier(), Util.copyAndPut(getPerkTags(), perk, tag));
    }
}
