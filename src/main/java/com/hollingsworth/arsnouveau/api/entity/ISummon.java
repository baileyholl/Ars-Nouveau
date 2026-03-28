package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.world.entity.EntityReference;

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

    default void writeOwner(ValueOutput tag) {
        net.minecraft.world.entity.EntityReference<?> ref = getOwnerReference();
        if (ref != null)
            tag.store("owner", net.minecraft.core.UUIDUtil.CODEC, ref.getUUID());
    }

    default @Nullable Entity readOwner(ServerLevel world, ValueInput tag) {
        return tag.read("owner", net.minecraft.core.UUIDUtil.CODEC)
            .map(uuid -> world.getEntity(uuid))
            .orElse(null);
    }

    /** @deprecated Use {@link #writeOwner(ValueOutput)} */
    @Deprecated
    default void writeOwner(CompoundTag tag) {
        net.minecraft.world.entity.EntityReference<?> ref = getOwnerReference();
        if (ref != null)
            tag.store("owner", net.minecraft.core.UUIDUtil.CODEC, ref.getUUID());
    }

    /**
     * Compatibility bridge: OwnableEntity no longer has getOwnerUUID() in 1.21.11.
     * Derived from the EntityReference returned by getOwnerReference().
     */
    default @Nullable UUID getOwnerUUID() {
        EntityReference<?> ref = getOwnerReference();
        return ref != null ? ref.getUUID() : null;
    }

    /*
     * This is a workaround for summon entities that inherit from an entity that override LivingEntity.getOwner() to return a subclass that doesn't include players. Ex. Vex
     * By default it will redirect to the classic getOwner() method, so it's safer to use as getter for general purpose
     * */
    default LivingEntity getOwnerAlt() {
        return getOwner();
    }
}
