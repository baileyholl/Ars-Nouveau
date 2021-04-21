package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ISummon {
    // How many ticks the summon has left
    int getTicksLeft();

    void setTicksLeft(int ticks);

    default @Nullable LivingEntity getLivingEntity(){
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }

    @Nullable UUID getOwnerID();

    default @Nullable Entity getOwner(ServerWorld world){
        return getOwnerID() != null ? world.getEntity(getOwnerID()) : null;
    }

    void setOwnerID(UUID uuid);

    default void onSummonDeath(World world, @Nullable DamageSource source, boolean didExpire){
        MinecraftForge.EVENT_BUS.post(new SummonEvent.Death(world,this, source, didExpire));
    }

    default void writeOwner(CompoundNBT tag){
        if(getOwnerID() != null)
            tag.putUUID("owner", getOwnerID());
    }

    default @Nullable Entity readOwner(ServerWorld world, CompoundNBT tag){
        return tag.contains("owner") ? world.getEntity(tag.getUUID("owner")) : null;
    }
}
