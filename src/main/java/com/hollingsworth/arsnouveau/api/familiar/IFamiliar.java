package com.hollingsworth.arsnouveau.api.familiar;

import net.minecraft.entity.Entity;

import java.util.UUID;

public interface IFamiliar {

    UUID getOwnerID();

    void setOwnerID(UUID uuid);

    default Entity getThisEntity(){
        return (Entity) this;
    }

    /**
     * Called if another familiar is summoned in the world, not including this one.
     * Used for maintaining the familiar limit in the world.
     */
    default void onFamiliarSpawned(UUID summoner){
        if(summoner.equals(getOwnerID())){
            this.getThisEntity().remove();
        }
    }
}
