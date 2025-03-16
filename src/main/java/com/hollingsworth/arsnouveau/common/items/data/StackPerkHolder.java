package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Serializes a set of perks from an itemstack.
 */
public abstract class StackPerkHolder<T> implements IPerkHolder<T> {

    public static final Codec<IPerk> PERK_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("perk").forGetter(IPerk::getRegistryName)
    ).apply(instance, (name) -> PerkRegistry.getPerkMap().getOrDefault(name, StarbunclePerk.INSTANCE)));

    private List<IPerk> perks;
    private int tier;
    private PerkMap perkTags;

    public StackPerkHolder(List<IPerk> perks, int tier, Map<IPerk, CompoundTag> perkTags) {
        this(perks, tier, new PerkMap(Map.copyOf(perkTags)));
    }

    public StackPerkHolder(List<IPerk> perks, int tier, PerkMap perkTags) {
        this.perks = List.copyOf(perks);
        this.tier = tier;
        this.perkTags = perkTags;
    }

    @Override
    public List<IPerk> getPerks() {
        return perks;
    }

    public int getTier() {
        return this.tier;
    }

    public PerkMap getPerkTags() {
        return this.perkTags;
    }

    @Override
    public @Nullable CompoundTag getTagForPerk(IPerk perk) {
        return this.perkTags.map().getOrDefault(perk, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackPerkHolder<?> that = (StackPerkHolder<?>) o;
        return tier == that.tier && Objects.equals(perks, that.perks) && Objects.equals(perkTags, that.perkTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perks, tier, perkTags);
    }
}
