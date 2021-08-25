package com.hollingsworth.arsnouveau.api.familiar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IFamiliar {

    UUID getOwnerID();

    void setOwnerID(UUID uuid);

    default Entity getThisEntity(){
        return (Entity) this;
    }

    default @Nullable Entity getOwnerServerside(){
        return ((ServerWorld) getThisEntity().level).getEntity(getOwnerID());
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

    default boolean wantsToAttack(LivingEntity ownerLastHurt, LivingEntity owner) {
        return true;
    }
}
