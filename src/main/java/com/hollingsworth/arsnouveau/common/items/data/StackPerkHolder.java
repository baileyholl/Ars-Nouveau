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

/**
 * Serializes a set of perks from an itemstack.
 */
public abstract class StackPerkHolder<T> implements IPerkHolder<T> {
    
    public static Codec<IPerk> PERK_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("perk").forGetter(IPerk::getRegistryName)
    ).apply(instance, (name) -> PerkRegistry.getPerkMap().getOrDefault(name, StarbunclePerk.INSTANCE)));

    public static Codec<Map<IPerk, CompoundTag>> PERK_TAG_CODEC = Codec.unboundedMap(PERK_CODEC, CompoundTag.CODEC);

    private List<IPerk> perks;
    private int tier;
    private Map<IPerk, CompoundTag> perkTags;

    public StackPerkHolder(List<IPerk> perks, int tier, Map<IPerk, CompoundTag> perkTags) {
        this.perks = List.copyOf(perks);
        this.tier = tier;
        this.perkTags = Map.copyOf(perkTags);
    }

    @Override
    public List<IPerk> getPerks() {
        return perks;
    }

    public int getTier() {
        return this.tier;
    }

    protected Map<IPerk, CompoundTag> getPerkTags() {
        return this.perkTags;
    }

    @Override
    public @Nullable CompoundTag getTagForPerk(IPerk perk) {
        return this.perkTags.getOrDefault(perk, null);
    }
}
