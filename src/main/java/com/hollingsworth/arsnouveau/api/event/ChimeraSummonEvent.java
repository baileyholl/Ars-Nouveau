package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.*;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ChimeraSummonEvent implements ITimedEvent {

    int duration;
    int phase;
    Level world;
    BlockPos pos;
    int ownerID;

    public ChimeraSummonEvent(int duration, int phase, Level world, BlockPos pos, int ownerID) {
        this.duration = duration;
        this.phase = phase;
        this.world = world;
        this.pos = pos;
        this.ownerID = ownerID;
    }

    public static ChimeraSummonEvent get(CompoundTag tag) {
        return new ChimeraSummonEvent(tag.getInt("duration"),
                tag.getInt("phase"), ArsNouveau.proxy.getClientWorld(), NBTUtil.getBlockPos(tag, "loc"), -1);
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if (serverSide) {
            Entity owner = world.getEntity(ownerID);
            if (!(owner instanceof WildenChimera boss)) {
                duration = 0;
                return;
            }
            boolean summonedWilden = false;
            if (duration % 20 == 0) {
                RandomSource random = boss.getRandom();
                SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF.get(), world);

                wolf.setPos(getPos().getX(), getPos().getY(), getPos().getZ());
                wolf.isWildenSummon = true;
                wolf.ticksLeft = 600 + phase * 60;
                summon(wolf, getPos(), boss.getTarget());

                int randBound = 8 - boss.getPhase();
                if (boss.hasWings() && boss.level.random.nextInt(randBound) == 0) {
                    WildenStalker stalker = new WildenStalker(ModEntities.WILDEN_STALKER.get(), world);
                    summon(stalker, getPos(), boss.getTarget());
                    summonedWilden = true;
                }

                if (!summonedWilden && boss.hasHorns() && boss.level.random.nextInt(randBound) == 0) {
                    WildenHunter hunter = new WildenHunter(ModEntities.WILDEN_HUNTER.get(), world);
                    summon(hunter, getPos(), boss.getTarget());
                    summonedWilden = true;
                }

                if (!summonedWilden && boss.hasSpikes() && boss.level.random.nextInt(randBound) == 0) {
                    WildenGuardian guardian = new WildenGuardian(ModEntities.WILDEN_GUARDIAN.get(), world);
                    summon(guardian, getPos(), boss.getTarget());
                    summonedWilden = true;
                }
            }
        } else {
            ParticleUtil.spawnRitualAreaEffect(pos, world, world.random, ParticleColor.defaultParticleColor(), 1 + phase * 2);
        }
    }

    public void summon(Mob mob, BlockPos pos, @Nullable LivingEntity target) {
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.setTarget(target);
        mob.setAggressive(true);
        mob.level.addFreshEntity(mob);
    }

    public BlockPos getPos() {
        double spawnArea = 2.5 + phase * 2;
        return BlockPos.containing(pos.getX() + ParticleUtil.inRange(-spawnArea, spawnArea), pos.getY() + 2, pos.getZ() + ParticleUtil.inRange(-spawnArea, spawnArea));
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        ITimedEvent.super.serialize(tag);
        tag.putInt("duration", duration);
        tag.putInt("phase", phase);
        NBTUtil.storeBlockPos(tag, "loc", pos);
        return tag;
    }

    public static final String ID = "chimera";

    @Override
    public String getID() {
        return ID;
    }
}
