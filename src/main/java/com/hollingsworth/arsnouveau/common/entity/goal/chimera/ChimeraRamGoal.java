package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;
import java.util.List;

public class ChimeraRamGoal extends Goal {
    EntityChimera boss;
    int timeCharging;

    boolean finished;
    boolean startedCharge;
    boolean isCharging;
    boolean hasHit;

    public ChimeraRamGoal(EntityChimera boss){
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
        timeCharging = 0;
        finished = false;
        startedCharge = false;
        isCharging = false;
        hasHit = false;
        boss.isRamming = true;
    }

    @Override
    public void tick() {
        super.tick();

        if(timeCharging >= 105) {
            endRam();
        }
        if(this.boss.getTarget() == null) {
            endRam();
        }
        if(!startedCharge){
            Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.CHARGE.ordinal()));
            startedCharge = true;
        }
        timeCharging++;


        if(timeCharging <= 25 && !isCharging){
            LivingEntity livingentity = this.boss.getTarget();

            this.boss.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            this.boss.getNavigation().stop();
        }

        if(timeCharging > 25 ){
            isCharging = true;
        }
        if(isCharging) {
            if(boss.getNavigation() == null || boss.getTarget() == null) {
                attack();
                return;
            }
            breakBlocks();
            Path path = boss.getNavigation().createPath(this.boss.getTarget().blockPosition().above(),  1);
            if(path == null) {
                return;
            }
            breakBlocks();
            boss.getNavigation().moveTo(path, 2.0f);
            attack();
        }

        if(hasHit && BlockUtil.distanceFrom(boss.position, boss.getTarget().position) <= 2.0f){
            endRam();
        }
    }

    public void breakBlocks(){
        Direction facing = boss.getDirection();
        BlockPos facingPos = boss.blockPosition().above().relative(facing);
        for(int i = 0; i < 2; i++){
            facingPos = facingPos.above(i);
            boss.level.destroyBlock(facingPos.above(), true);
            boss.level.destroyBlock(facingPos.east(), true);
            boss.level.destroyBlock(facingPos.west(), true);
            boss.level.destroyBlock(facingPos.south(), true);
            boss.level.destroyBlock(facingPos.north(), true);
        }


    }

    @Override
    public void stop() {
        super.stop();
        boss.isRamming = false;
    }

    public void endRam(){
        finished = true;
        if(boss.level.random.nextInt(3) != 0) {
            boss.ramCooldown = (int) (400 + ParticleUtil.inRange(-100, 100 + boss.getCooldownModifier()));
        }
        boss.isRamming = false;
        Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.ATTACK.ordinal()));
        attack();
    }

    protected void attack() {
        List<LivingEntity> nearbyEntities = boss.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(boss.blockPosition()).inflate(1, 1, 1));
        for(LivingEntity enemy: nearbyEntities){
            if(enemy.equals(boss))
                continue;
            this.boss.doHurtTarget(enemy);
            enemy.knockback(3.0f, (double) MathHelper.sin(boss.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(boss.yRot * ((float)Math.PI / 180F))));
            hasHit = true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !finished;
    }

    @Override
    public boolean canUse() {
        return boss.getTarget() != null && boss.ramCooldown <= 0 && boss.canRam();
    }
}
