package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public abstract class Perk implements IPerk {
    private Identifier id;

    public Perk(Identifier key) {
        this.id = key;
    }

    public Identifier getRegistryName() {
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
