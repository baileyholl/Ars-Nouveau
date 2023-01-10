package com.hollingsworth.arsnouveau.common.entity.goal.guardian;

import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LaserAttackGoal extends Goal {
    private final WildenGuardian guardian;
    private int tickCounter;


    public LaserAttackGoal(WildenGuardian guardian) {
        this.guardian = guardian;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = this.guardian.getTarget();
        return livingentity != null && livingentity.isAlive() && guardian.laserCooldown == 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.guardian.getTarget() != null && (this.guardian.distanceToSqr(this.guardian.getTarget()) > 9.0D) && guardian.laserCooldown == 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.tickCounter = -10;
        this.guardian.getNavigation().stop();
        if(this.guardian.getTarget() == null)
            return;
        this.guardian.getLookControl().setLookAt(this.guardian.getTarget(), 90.0F, 90.0F);
        this.guardian.setLaser(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.guardian.setTarget(null);
        this.guardian.setLaser(false);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        LivingEntity livingentity = this.guardian.getTarget();
        if(livingentity == null)
            return;
        this.guardian.getNavigation().stop();
        this.guardian.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
        if (!this.guardian.hasLineOfSight(livingentity)) {
            this.guardian.setTarget(null);
        } else {
            ++this.tickCounter;
            if (this.tickCounter == 0) {
                this.guardian.setTargetedEntity(this.guardian.getTarget().getId());
            } else if (this.tickCounter >= this.guardian.getAttackDuration()) {
                float f = 1.0F;
                if (this.guardian.level.getDifficulty() == Difficulty.HARD) {
                    f += 2.0F;
                }

                livingentity.hurt(DamageSource.indirectMagic(this.guardian, this.guardian), f);
                livingentity.hurt(DamageSource.mobAttack(this.guardian), (float) this.guardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
                livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));


                this.guardian.setTarget(null);
                this.guardian.laserCooldown = 100;
                this.guardian.setClientAttackTime(0);
            }

            super.tick();
        }
    }
}
