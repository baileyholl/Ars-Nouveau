package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ArmorPerkHolder extends StackPerkHolder<ArmorPerkHolder> {

    public static final Codec<ArmorPerkHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("color").forGetter(ArmorPerkHolder::getColor),
            StackPerkHolder.PERK_CODEC.listOf().fieldOf("perks").forGetter(ArmorPerkHolder::getPerks),
            Codec.INT.fieldOf("tier").forGetter(ArmorPerkHolder::getTier),
            StackPerkHolder.PERK_TAG_CODEC.fieldOf("perkTags").forGetter(ArmorPerkHolder::getPerkTags)
    ).apply(instance, ArmorPerkHolder::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorPerkHolder> STREAM_CODEC = CheatSerializer.create(CODEC);

    private String color;


    public ArmorPerkHolder(String color, List<IPerk> perks, int tier, Map<IPerk, CompoundTag> perkTags) {
        super(perks, tier, perkTags);
        this.color = color;
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
        return new ArmorPerkHolder(color, getPerks(), getTier(), Util.copyAndPut(getPerkTags(), perk, tag));
    }
}
