package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
            if(duration % 20 ==0){
                Random random = boss.getRandom();
                SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF, world);
                wolf.setPos(getPos().getX(), getPos().getY(), getPos().getZ());
                wolf.isWildenSummon = true;
                wolf.ticksLeft = 100 + phase * 60;
                if(boss.getTarget() != null){
                    wolf.setTarget(boss.getTarget());
                }
                wolf.setAggressive(true);
                world.addFreshEntity(wolf);

                if(boss.hasHorns() && boss.level.random.nextInt(8) == 0){
                    WildenHunter hunter = new WildenHunter(ModEntities.WILDEN_HUNTER, world);
                    hunter.setPos(getPos().getX(), getPos().getY(), getPos().getZ());
                    world.addFreshEntity(hunter);
                }

                if(boss.hasSpikes() && boss.level.random.nextInt(8) == 0){
                    WildenGuardian guardian = new WildenGuardian(ModEntities.WILDEN_GUARDIAN, world);
                    guardian.setPos(getPos().getX(), getPos().getY(), getPos().getZ());
                    world.addFreshEntity(guardian);
                }

                if(boss.hasWings() && boss.level.random.nextInt(8) == 0){
                    WildenStalker stalker = new WildenStalker(ModEntities.WILDEN_STALKER, world);
                    stalker.setPos(getPos().getX(), getPos().getY(), getPos().getZ());
                    world.addFreshEntity(stalker);
                }
            }
        }else{
            ParticleUtil.spawnRitualAreaEffect(pos, world, world.random, ParticleUtil.defaultParticleColor(), 1 + phase *2);
        }
    }

    public BlockPos getPos(){
        return new BlockPos(pos.getX() + ParticleUtil.inRange(-2.5, 2.5), pos.getY(), pos.getZ() + ParticleUtil.inRange(-2.5, 2.5));
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
