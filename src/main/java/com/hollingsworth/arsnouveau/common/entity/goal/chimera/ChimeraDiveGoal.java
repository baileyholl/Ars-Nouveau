package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;

import java.util.EnumSet;

public class ChimeraDiveGoal extends Goal {
    EntityChimera boss;
    boolean finished;
    int ticksFlying;
    boolean isDiving;
    BlockPos divePos;
    boolean startedFlying;
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
        startedFlying = false;
        startPos = boss.blockPosition();
        hoverPos = startPos.above(8);
    }


    @Override
    public void tick() {
        super.tick();

        if(!startedFlying){
            startedFlying = true;
            boss.setFlying(true);
            Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.FLYING.ordinal()));
        }
        if(startedFlying && ticksFlying < 35){
            boss.moveTo(hoverPos.getX(), hoverPos.getY(), hoverPos.getZ());
            if(boss.getTarget() != null)
                this.boss.getLookControl().setLookAt(boss.getTarget(), 30.0F, 30.0F);
        }
        ticksFlying++;
        if(ticksFlying > 35){
            isDiving = true;

            if(divePos == null){
                if(boss.getTarget() != null)
                    divePos = boss.getTarget().blockPosition();
                Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.DIVE_BOMB.ordinal()));
            }
            if(divePos != null)
                boss.moveTo(divePos.getX() + 0.5, divePos.getY(), divePos.getZ());
        }
        System.out.println(boss.level.getBlockState(boss.blockPosition().below()).isAir());
        if((isDiving && (!boss.level.getBlockState(boss.blockPosition().below()).isAir() || boss.isOnGround()))) {
            finished = true;
            makeExplosion();
            boss.setFlying(false);
            boss.diveCooldown = 100;
        }
    }

    public void makeExplosion(){
        boss.level.explode(boss, boss.getX() + 0.5, boss.getY() + 0.5, boss.getZ() + 0.5, 0.1f, Explosion.Mode.NONE);
        Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.HOWL.ordinal()));
    }

    @Override
    public boolean canContinueToUse() {
        return !finished;
    }

    @Override
    public boolean canUse() {
        return boss.canDive() && boss.getTarget() != null;
    }
}
