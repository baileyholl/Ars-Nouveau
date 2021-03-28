package com.hollingsworth.arsnouveau.common.entity.goal.guardian;

import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import java.util.EnumSet;

public class LaserAttackGoal extends Goal {
    private final WildenGuardian guardian;
    private int tickCounter;


    public LaserAttackGoal(WildenGuardian guardian) {
        this.guardian = guardian;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        LivingEntity livingentity = this.guardian.getAttackTarget();
        return livingentity != null && livingentity.isAlive() && guardian.laserCooldown == 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && (this.guardian.getDistanceSq(this.guardian.getAttackTarget()) > 9.0D) && guardian.laserCooldown == 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.tickCounter = -10;
        this.guardian.getNavigator().clearPath();
        this.guardian.getLookController().setLookPositionWithEntity(this.guardian.getAttackTarget(), 90.0F, 90.0F);
        this.guardian.setLaser(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.guardian.setAttackTarget((LivingEntity)null);
        this.guardian.setLaser(false);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        LivingEntity livingentity = this.guardian.getAttackTarget();
        this.guardian.getNavigator().clearPath();
        this.guardian.getLookController().setLookPositionWithEntity(livingentity, 90.0F, 90.0F);
        if (!this.guardian.canEntityBeSeen(livingentity)) {
            this.guardian.setAttackTarget((LivingEntity)null);
        } else {
            ++this.tickCounter;
            if (this.tickCounter == 0) {
                this.guardian.setTargetedEntity(this.guardian.getAttackTarget().getEntityId());
            } else if (this.tickCounter >= this.guardian.getAttackDuration()) {
                float f = 1.0F;
                if (this.guardian.world.getDifficulty() == Difficulty.HARD) {
                    f += 2.0F;
                }

                livingentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.guardian, this.guardian), f);
                livingentity.attackEntityFrom(DamageSource.causeMobDamage(this.guardian), (float)this.guardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
                livingentity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));


                this.guardian.setAttackTarget((LivingEntity)null);
                this.guardian.laserCooldown = 100;
                this.guardian.setClientAttackTime(0);
            }

            super.tick();
        }
    }
}
