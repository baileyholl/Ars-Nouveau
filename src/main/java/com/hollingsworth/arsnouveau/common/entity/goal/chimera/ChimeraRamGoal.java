package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;
import java.util.List;

public class ChimeraRamGoal extends Goal {
    EntityChimera boss;
    int timeCharging;
    BlockPos chargePos;
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
        chargePos = null;
        finished = false;
        startedCharge = false;
        isCharging = false;
        hasHit = true;
    }

    @Override
    public void tick() {
        super.tick();

        if(this.boss.getTarget() == null) {
            endRam();
        }
        chargePos = boss.getTarget().blockPosition();
        Path path = boss.getNavigation().createPath(chargePos.getX() + 0.5, chargePos.getY(), chargePos.getZ() + 0.5, 1);
        if(path == null || !path.canReach()) {
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
            chargePos = boss.getTarget().blockPosition();
        }
        if(isCharging && chargePos != null) {
            boss.getNavigation().moveTo( path, 2.0f);
            attack();
        }
        if(chargePos != null)
            System.out.println(BlockUtil.distanceFrom(boss.position, chargePos) );

        if(hasHit && chargePos != null && BlockUtil.distanceFrom(boss.position, chargePos) <= 2.0f){
            endRam();
            chargePos = null;
        }

        if(chargePos != null && BlockUtil.distanceFrom(boss.position, chargePos) <= 1.0f){
            endRam();
        }

        if(timeCharging >= 90) {
            endRam();
            attack();
        }
    }

    public void endRam(){
        finished = true;
        if(boss.level.random.nextInt(3) != 0) {
            boss.ramCooldown = 300;
        }
        Networking.sendToNearby(boss.level, boss, new PacketAnimEntity(boss.getId(), EntityChimera.Animations.ATTACK.ordinal()));
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
        return super.canContinueToUse() && !finished;
    }

    @Override
    public boolean canUse() {
        return boss.getTarget() != null && boss.ramCooldown <= 0 && boss.canRam();
    }
}
