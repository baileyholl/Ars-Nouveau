package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public abstract class Perk implements IPerk {
    private ResourceLocation id;

    public Perk(ResourceLocation key) {
        this.id = key;
    }

    public ResourceLocation getRegistryName() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Perk perk = (Perk) o;
        return Objects.equals(id, perk.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
