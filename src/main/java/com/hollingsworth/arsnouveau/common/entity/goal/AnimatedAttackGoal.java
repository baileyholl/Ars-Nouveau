package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.function.Supplier;

public class AnimatedAttackGoal extends Goal {

    protected final Mob mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval = 20;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public int timeAnimating = 0;
    public boolean arrived = false;
    public boolean done = false;
    public Supplier<Boolean> canAttack;
    int animationID;
    int animationLength;
    int attackRange;

    public AnimatedAttackGoal(Mob entity, boolean followUnseen, Supplier<Boolean> canAttack, int animationID, int animationLength, int attackRange, double speedModifier) {
        this.mob = entity;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followUnseen;
        this.canAttack = canAttack;
        this.animationID = animationID;
        this.animationLength = animationLength;
        this.attackRange = attackRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    public boolean canUse() {
        long i = this.mob.level.getGameTime();
        if (!canAttack.get())
            return false;
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (canPenalize) {
                    if (--this.ticksUntilNextPathRecalculation <= 0) {
                        this.path = this.mob.getNavigation().createPath(livingentity, 0);
                        this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                        return this.path != null;
                    } else {
                        return true;
                    }
                }
                this.path = this.mob.getNavigation().createPath(livingentity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getAttackReachSqr(livingentity) >= this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                }
            }
        }
    }

    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (!canAttack.get())
            return false;
        if (livingentity == null || done) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else if (!this.mob.isWithinRestriction(livingentity.blockPosition())) {
            return false;
        } else {
            return !(livingentity instanceof Player player) || !livingentity.isSpectator() && !player.isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
        timeAnimating = 0;
        arrived = false;
        done = false;
    }

    public void stop() {

    }

    public void arrivedTick() {
        timeAnimating++;
        if (timeAnimating >= animationLength) {
            if (this.mob.getTarget() != null)
                this.attack(this.mob.getTarget());
            this.done = true;
        }
    }

    public void look(LivingEntity entity) {
        if (entity != null)
            this.mob.getLookControl().setLookAt(entity, 30.0F, 30.0F);
    }

    public void onArrive() {
        this.arrived = true;
        Networking.sendToNearbyClient(mob.level, mob, new PacketAnimEntity(mob.getId(), animationID));
    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        look(livingentity);
        if (arrived) {
            arrivedTick();
            return;
        }

        double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
        if (BlockUtil.distanceFrom(this.mob.position, livingentity.position) <= attackRange) {
            onArrive();
        }
        if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
            this.pathedTargetX = livingentity.getX();
            this.pathedTargetY = livingentity.getY();
            this.pathedTargetZ = livingentity.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
            if (this.canPenalize) {
                this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                if (this.mob.getNavigation().getPath() != null) {
                    net.minecraft.world.level.pathfinder.Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                    if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                        failedPathFindingPenalty = 0;
                    else
                        failedPathFindingPenalty += 10;
                } else {
                    failedPathFindingPenalty += 10;
                }
            }
            if (d0 > 1024.0D) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if (d0 > 256.0D) {
                this.ticksUntilNextPathRecalculation += 5;
            }

            if (!this.mob.getNavigation().moveTo(livingentity, this.speedModifier)) {
                this.ticksUntilNextPathRecalculation += 15;
            }
        }

        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
    }

    protected void attack(LivingEntity target) {
        if (BlockUtil.distanceFrom(target.position, this.mob.position) <= attackRange) {
            this.ticksUntilNextAttack = 20;
            this.mob.doHurtTarget(target);
        }
    }

    protected double getAttackReachSqr(LivingEntity p_179512_1_) {
        return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + p_179512_1_.getBbWidth();
    }

}
