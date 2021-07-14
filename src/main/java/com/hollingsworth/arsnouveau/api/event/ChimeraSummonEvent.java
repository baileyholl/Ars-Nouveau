package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.common.entity.WildenBoss;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChimeraSummonEvent implements ITimedEvent {

    int duration;
    int phase;
    World world;
    BlockPos pos;
    int ownerID;
    public ChimeraSummonEvent(int duration, int phase, World world, BlockPos pos, int ownerID){
        this.duration = duration;
        this.phase = phase;
        this.world = world;
        this.pos = pos;
        this.ownerID = ownerID;
    }

    public static ChimeraSummonEvent get(CompoundNBT tag){
        return new ChimeraSummonEvent(tag.getInt("duration"),
                tag.getInt("phase"), ArsNouveau.proxy.getClientWorld(), NBTUtil.getBlockPos(tag, "loc"), -1);
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if(serverSide){
            Entity owner = world.getEntity(ownerID);
            if(!(owner instanceof WildenBoss)) {
                duration = 0;
                return;
            }
            WildenBoss boss = (WildenBoss) owner;
            if(duration % 20 ==0){
                System.out.println(phase);
                if(phase <= 1){
                    SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF, world);
                    wolf.setPos(pos.getX(), pos.getY(), pos.getZ());
                    wolf.isWildenSummon = true;
                    wolf.ticksLeft = 100;
                    if(boss.getTarget() != null){
                        wolf.setTarget(boss.getTarget());
                    }
                    wolf.setAggressive(true);
                    world.addFreshEntity(wolf);
                }
            }
        }else{
            ParticleUtil.spawnRitualAreaEffect(pos, world, world.random, ParticleUtil.defaultParticleColor(), 1 + phase *2);
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        ITimedEvent.super.serialize(tag);
        tag.putInt("duration", duration);
        tag.putInt("phase", phase);
        NBTUtil.storeBlockPos(tag, "loc", pos);
    }

    public static final String ID = "chimera";
    @Override
    public String getID() {
        return ID;
    }
}
