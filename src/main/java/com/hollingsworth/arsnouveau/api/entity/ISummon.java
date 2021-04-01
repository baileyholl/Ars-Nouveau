package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public interface ISummon {
    // How many ticks the summon has left
    int getTicksLeft();

    void setTicksLeft(int ticks);

    default @Nullable LivingEntity getLivingEntity(){
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }

    default void onSummonDeath(World world, @Nullable DamageSource source, boolean didExpire){
        MinecraftForge.EVENT_BUS.post(new SummonEvent.Death(world,this, source, didExpire));
    }
}
