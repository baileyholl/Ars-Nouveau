package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class ChimeraLeapRamGoal extends Goal {
    WildenChimera boss;

    int timeCharging;
    boolean finished;
    boolean startedCharge;
    boolean isCharging;
    boolean hasHit;

    public ChimeraLeapRamGoal(WildenChimera boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }


    @Override
    public void start() {
        super.start();
        timeCharging = 0;
        finished = false;
        startedCharge = false;
        isCharging = false;
        hasHit = false;
        boss.isRamGoal = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.boss.getTarget() == null) {
            endRam();
        }
        if (!startedCharge) {
            boss.setRamPrep(true);
            startedCharge = true;
        }
        timeCharging++;
        if (timeCharging <= 20 && !isCharging) {
            LivingEntity livingentity = this.boss.getTarget();
            if (livingentity != null)
                this.boss.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            this.boss.getNavigation().stop();
        }

        if (timeCharging > 20 && !isCharging) {
            boss.setRamPrep(false);
            Vec3 vec3 = this.boss.getDeltaMovement();
            Vec3 vec31 = new Vec3(boss.getTarget().getX() - this.boss.getX(), 0.0D, this.boss.getTarget().getZ() - this.boss.getZ());
            if (vec31.lengthSqr() > 1.0E-7D) {
                vec31 = vec31.normalize().scale(0.4D).add(vec3.scale(0.2D));
            }

            this.boss.setDeltaMovement(vec31.x * 6f, 1.2d, vec31.z * 6f);
            boss.hasImpulse = true;
            isCharging = true;
        }
        if (isCharging) {
            if (boss.getNavigation() == null || boss.getTarget() == null) {
                attack();
                return;
            }
            breakBlocks();
            if(boss.onGround()) {
                Path path = boss.getNavigation().createPath(this.boss.getTarget().blockPosition().above(), 1);
                if (path == null) {
                    return;
                }
                boss.getNavigation().moveTo(path, 2.0f);
            }
            attack();
        }

        if (boss != null && boss.getTarget() != null && hasHit && BlockUtil.distanceFrom(boss.position, boss.getTarget().position) <= 3f) {
            endRam();
        }
    }

    public void breakBlocks() {
        if (!net.neoforged.neoforge.event.EventHooks.canEntityGrief(this.boss.level, this.boss)) {
            return;
        }
        Direction facing = boss.getDirection();
        BlockPos facingPos = boss.blockPosition().above().relative(facing);
        for (int i = 0; i < 3; i++) {
            facingPos = facingPos.above(i);
            destroyBlock(facingPos.above());
            destroyBlock(facingPos.east());
            destroyBlock(facingPos.west());
            destroyBlock(facingPos.south());
            destroyBlock(facingPos.north());
        }
    }


    public void destroyBlock(BlockPos pos) {
        if (SpellUtil.isCorrectHarvestLevel(4, boss.level.getBlockState(pos))) {
            boss.level.destroyBlock(pos, true);
        }
    }


    public void endRam() {
        finished = true;
        if (boss.level.random.nextInt(3) != 0) {
            boss.ramCooldown = (int) (400 + ParticleUtil.inRange(-100, 100 + boss.getCooldownModifier()));
        }
        boss.isRamGoal = false;
        attack();
        boss.setRamming(false);
        boss.setRamPrep(false);
    }

    protected void attack() {
        List<LivingEntity> nearbyEntities = boss.level.getEntitiesOfClass(LivingEntity.class, new AABB(boss.blockPosition()).inflate(3, 3, 3));
        for (LivingEntity enemy : nearbyEntities) {
            if (enemy.equals(boss))
                continue;
            this.boss.doHurtTarget(enemy);
            enemy.knockback(3.0f, Mth.sin(boss.yRot * ((float) Math.PI / 180F)), -Mth.cos(boss.yRot * ((float) Math.PI / 180F)));
            hasHit = true;
        }
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !finished && !boss.getPhaseSwapping();
    }

    @Override
    public boolean canUse() {
        return boss.getTarget() != null && boss.ramCooldown <= 0 && boss.canRam(true);
    }
}
