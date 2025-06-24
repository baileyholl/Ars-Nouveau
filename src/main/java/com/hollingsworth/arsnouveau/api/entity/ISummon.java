package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * For entities summoned by spells.
 */
public interface ISummon extends OwnableEntity {
    // How many ticks the summon has left
    int getTicksLeft();

    void setTicksLeft(int ticks);

    default @Nullable LivingEntity getLivingEntity() {
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }

    void setOwnerID(UUID uuid);

    default void onSummonDeath(Level world, @Nullable DamageSource source, boolean didExpire) {
        NeoForge.EVENT_BUS.post(new SummonEvent.Death(world, this, source, didExpire));
    }

    default void writeOwner(CompoundTag tag) {
        if (getOwnerUUID() != null)
            tag.putUUID("owner", getOwnerUUID());
    }

    default @Nullable Entity readOwner(ServerLevel world, CompoundTag tag) {
        return tag.contains("owner") ? world.getEntity(tag.getUUID("owner")) : null;
    }

    /*
     * This is a workaround for summon entities that inherit from an entity that override LivingEntity.getOwner() to return a subclass that doesn't include players. Ex. Vex
     * By default it will redirect to the classic getOwner() method, so it's safer to use as getter for general purpose
     * */
    default LivingEntity getOwnerAlt() {
        return getOwner();
    }
}
