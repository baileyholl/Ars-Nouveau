package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * For entities summoned by spells.
 */
public interface ISummon {
    // How many ticks the summon has left
    int getTicksLeft();

    void setTicksLeft(int ticks);

    default @Nullable LivingEntity getLivingEntity() {
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }

    @Nullable
    UUID getOwnerID();

    default @Nullable Entity getOwner(ServerLevel world) {
        return getOwnerID() != null ? world.getEntity(getOwnerID()) : null;
    }

    void setOwnerID(UUID uuid);

    default void onSummonDeath(Level world, @Nullable DamageSource source, boolean didExpire) {
        MinecraftForge.EVENT_BUS.post(new SummonEvent.Death(world, this, source, didExpire));
    }

    default void writeOwner(CompoundTag tag) {
        if (getOwnerID() != null)
            tag.putUUID("owner", getOwnerID());
    }

    default @Nullable Entity readOwner(ServerLevel world, CompoundTag tag) {
        return tag.contains("owner") ? world.getEntity(tag.getUUID("owner")) : null;
    }
}
