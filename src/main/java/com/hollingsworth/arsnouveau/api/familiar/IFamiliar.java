package com.hollingsworth.arsnouveau.api.familiar;

import net.minecraft.entity.Entity;

import java.util.UUID;

public interface IFamiliar {

    UUID getOwnerID();

    void setOwnerID(UUID uuid);

    default Entity getThisEntity(){
        return (Entity) this;
    }
}
