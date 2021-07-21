package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;

import java.util.EnumSet;

public class ChimeraDiveGoal extends Goal {
    EntityChimera boss;
    boolean finished;
    int ticksFlying;
    boolean isDiving;
    BlockPos divePos;
    BlockPos startPos;
    BlockPos hoverPos;
    public ChimeraDiveGoal(EntityChimera boss){
        this.boss = boss;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        finished = false;
        divePos = null;
        ticksFlying = 0;
        isDiving = false;

        startPos = boss.blockPosition();
        hoverPos = startPos.above(8);

        boss.setFlying(true);
        boss.getNavigation().setCanFloat(true);
        Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.FLYING.ordinal()));
    }


    @Override
    public void tick() {
        super.tick();
        ticksFlying++;
        if(ticksFlying < 60){
            boss.setFlying(true);
            boss.flyingNavigator.moveTo(hoverPos.getX(), hoverPos.getY(), hoverPos.getZ(), 1.0f);
            if(boss.getTarget() != null) {
                EntityChimera.faceBlock(boss.getTarget().blockPosition(), boss);
            }
        }

        if(ticksFlying > 60){
            isDiving = true;
            boss.diving = true;

            if(divePos == null){
                if(boss.getTarget() != null) {
                    divePos = boss.getTarget().blockPosition().below();
                    // Seek the ground below
                    for(int i = 1; i < 50; i++){
                        if(boss.level.getBlockState(divePos).isAir()) {
                            divePos = divePos.below();
                        }else{
                            break;
                        }
                    }
                }
                Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.DIVE_BOMB.ordinal()));
            }
            if(divePos != null) {
                boss.flyingNavigator.moveTo(divePos.getX() + 0.5, divePos.getY(), divePos.getZ(), 4f);
                boss.orbitOffset = new Vector3d(divePos.getX() + 0.5, divePos.getY(), divePos.getZ() + 0.5);
            }
        }
        if((isDiving && (boss.isOnGround() || BlockUtil.distanceFrom(boss.position, divePos) <= 1.0d) ||  BlockUtil.distanceFrom(boss.position, boss.orbitOffset) <= 1.7d)) {
            makeExplosion();
            endGoal();
        }
        if(isDiving && (boss.isInWall() || boss.horizontalCollision || boss.verticalCollision)){
            makeExplosion();
            endGoal();
        }
        if(isDiving && divePos == null && boss.getTarget() == null){
            endGoal();
        }
    }

    public void endGoal(){
        boss.getNavigation().setCanFloat(false);
        boss.getNavigation().stop();
        boss.setFlying(false);

        boss.diveCooldown = (int) (300 + ParticleUtil.inRange(-100, 100) + boss.getCooldownModifier());
        boss.getNavigation().stop();
        boss.getNavigation().setCanFloat(false);
        boss.diving = false;
        boss.setNoGravity(false);
        boss.setDeltaMovement(0,0,0);
        boss.getNavigation().moveTo(this.boss.getTarget() != null ? this.boss.getTarget() : this.boss, 0.0f);
        finished = true;

    }

    public void makeExplosion(){
        boss.level.explode(boss, boss.getX() + 0.5, boss.getY(), boss.getZ() + 0.5, 3.5f, Explosion.Mode.BREAK);
        Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.HOWL.ordinal()));
    }

    @Override
    public boolean canContinueToUse() {
        return !finished && boss.getHealth() > 1;
    }

    @Override
    public boolean canUse() {
        return boss.canDive() && boss.getTarget() != null;
    }
}
