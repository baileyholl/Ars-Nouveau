package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class PerkInstance {

    private PerkSlot slot;
    private IPerk perk;

    public PerkInstance(CompoundTag tag) {
        ResourceLocation perkId = ResourceLocation.tryParse(tag.getString("perkId"));
        perk = PerkRegistry.PERK_TYPES.getOptional(perkId).orElse(StarbunclePerk.INSTANCE);
        slot = PerkSlot.PERK_SLOTS.getOrDefault(ResourceLocation.tryParse(tag.getString("slotId")), PerkSlot.ONE);
    }

    public PerkInstance(PerkSlot slot, IPerk perk) {
        this.slot = slot;
        this.perk = perk;
    }

    public PerkSlot getSlot() {
        return slot;
    }

    public IPerk getPerk() {
        return perk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkInstance that = (PerkInstance) o;
        return Objects.equals(slot, that.slot) && Objects.equals(perk, that.perk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, perk);
    }
}
