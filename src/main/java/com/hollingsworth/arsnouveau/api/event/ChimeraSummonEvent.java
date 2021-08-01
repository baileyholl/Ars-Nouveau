package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

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
            if(!(owner instanceof EntityChimera)) {
                duration = 0;
                return;
            }
            EntityChimera boss = (EntityChimera) owner;
            boolean summonedWilden = false;
            if(duration % 20 ==0){
                Random random = boss.getRandom();
                SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF, world);

                wolf.setPos(getPos().getX(), getPos().getY(), getPos().getZ());
                wolf.isWildenSummon = true;
                wolf.ticksLeft = 100 + phase * 60;
                summon(wolf, getPos(), boss.getTarget());

                int randBound = 10 - boss.getPhase();
                if(!summonedWilden && boss.hasWings() && boss.level.random.nextInt(randBound) == 0){
                    WildenStalker stalker = new WildenStalker(ModEntities.WILDEN_STALKER, world);
                    summon(stalker, getPos(), boss.getTarget());
                    summonedWilden = true;
                }

                if(boss.hasHorns() && boss.level.random.nextInt(randBound) == 0){
                    WildenHunter hunter = new WildenHunter(ModEntities.WILDEN_HUNTER, world);
                    summon(hunter, getPos(), boss.getTarget());
                    summonedWilden = true;
                }

                if(!summonedWilden && boss.hasSpikes() && boss.level.random.nextInt(randBound) == 0){
                    WildenGuardian guardian = new WildenGuardian(ModEntities.WILDEN_GUARDIAN, world);
                    summon(guardian, getPos(), boss.getTarget());
                    summonedWilden = true;
                }
            }
        }else{
            ParticleUtil.spawnRitualAreaEffect(pos, world, world.random, ParticleUtil.defaultParticleColor(), 1 + phase * 2);
        }
    }

    public void summon(MobEntity mob, BlockPos pos, @Nullable LivingEntity target){
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.setTarget(target);
        mob.setAggressive(true);
        mob.level.addFreshEntity(mob);
    }

    public BlockPos getPos(){
        double spawnArea = 2.5 + phase *2;
        return new BlockPos(pos.getX() + ParticleUtil.inRange(-spawnArea, spawnArea), pos.getY() + 2, pos.getZ() + ParticleUtil.inRange(-spawnArea, spawnArea));
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
