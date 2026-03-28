package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class PerkInstance {

    private PerkSlot slot;
    private IPerk perk;

    public PerkInstance(CompoundTag tag) {
        Identifier perkId = Identifier.tryParse(tag.getStringOr("perkId", ""));
        perk = PerkRegistry.getPerkMap().getOrDefault(perkId, StarbunclePerk.INSTANCE);
        slot = PerkSlot.PERK_SLOTS.getOrDefault(Identifier.tryParse(tag.getStringOr("slotId", "")), PerkSlot.ONE);
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
