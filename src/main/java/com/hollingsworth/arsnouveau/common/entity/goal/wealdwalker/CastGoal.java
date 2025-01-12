package com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;
import java.util.function.Supplier;

public class CastGoal<T extends Mob & RangedAttackMob> extends Goal {
    private final T mob;
    private final double speedModifier;

    private final float attackRadiusSqr;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    boolean hasAnimated;
    int animatedTicks;
    int delayTicks;
    int animId;
    boolean done;
    Supplier<Boolean> canUse;

    public CastGoal(T entity, double speed, float attackRange, Supplier<Boolean> canUse, int animId, int delayTicks) {
        this.mob = entity;
        this.speedModifier = speed;
        this.attackRadiusSqr = attackRange * attackRange;
        this.canUse = canUse;
        this.animId = animId;
        this.delayTicks = delayTicks;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        return this.canUse.get() && this.mob.getTarget() != null;
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && !this.done;
    }

    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        animatedTicks = 0;
        done = false;
        hasAnimated = false;
    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null)
            return;


        double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        boolean canSeeEnemy = this.mob.getSensing().hasLineOfSight(livingentity);
        if (canSeeEnemy != this.seeTime > 0) {
            this.seeTime = 0;
        }

        if (canSeeEnemy) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
            this.mob.getNavigation().stop();
            ++this.strafingTime;
        } else {
            this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                this.strafingClockwise = !this.strafingClockwise;
            }

            if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (d0 > (double) (this.attackRadiusSqr * 0.75F)) {
                this.strafingBackwards = false;
            } else if (d0 < (double) (this.attackRadiusSqr * 0.25F)) {
                this.strafingBackwards = true;
            }

            this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            this.mob.lookAt(livingentity, 30.0F, 30.0F);
        } else {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
        }
        if (this.seeTime >= 40 && !this.hasAnimated) {
            this.hasAnimated = true;
            Networking.sendToNearbyClient(mob.level, mob, new PacketAnimEntity(mob.getId(), animId));
        }
        if (this.hasAnimated) {
            animatedTicks++;
            if (animatedTicks >= delayTicks) {
                mob.performRangedAttack(mob.getTarget(), 1);
                this.done = true;
            }
        }
    }
}
