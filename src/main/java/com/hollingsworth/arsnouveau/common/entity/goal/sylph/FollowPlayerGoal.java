package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FollowPlayerGoal extends Goal {

    private final MobEntity entity;
    private final Predicate<PlayerEntity> followPredicate;
    private PlayerEntity followingEntity;
    private final double speedModifier;
    private final PathNavigator navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;
    private final float probability;
    public FollowPlayerGoal(MobEntity mob, double speedModifier, float stopDistance, float areaSize, float probability) {
        this.entity = mob;
        this.followPredicate = Objects::nonNull;
        this.speedModifier = speedModifier;
        this.navigation = mob.getNavigation();
        this.stopDistance = stopDistance;
        this.areaSize = areaSize;
        this.probability = probability;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigator) && !(mob.getNavigation() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }
    public FollowPlayerGoal(MobEntity mob, double speedModifier, float stopDistance, float areaSize) {
        this(mob, speedModifier, stopDistance, areaSize, 0.001f);
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        List<PlayerEntity> list = this.entity.level.getEntitiesOfClass(PlayerEntity.class, this.entity.getBoundingBox().inflate((double)this.areaSize), this.followPredicate);
        if (!list.isEmpty()) {
            for(PlayerEntity mobentity : list) {
                if (!mobentity.isInvisible()) {
                    this.followingEntity = mobentity;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.followingEntity != null && !this.navigation.isDone() && this.entity.distanceToSqr(this.followingEntity) > (double)(this.stopDistance * this.stopDistance);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.entity.getPathfindingMalus(PathNodeType.WATER);
        this.entity.setPathfindingMalus(PathNodeType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.followingEntity = null;
        this.navigation.stop();
        this.entity.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.followingEntity != null && !this.entity.isLeashed()) {
            this.entity.getLookControl().setLookAt(this.followingEntity, 10.0F, (float)this.entity.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                double d0 = this.entity.getX() - this.followingEntity.getX();
                double d1 = this.entity.getY() - this.followingEntity.getY();
                double d2 = this.entity.getZ() - this.followingEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (!(d3 <= (double)(this.stopDistance * this.stopDistance))) {
                    this.navigation.moveTo(this.followingEntity, this.speedModifier);
                } else {
                    this.navigation.stop();

                    if (d3 <= (double)this.stopDistance) {
                        double d4 = this.followingEntity.getX() - this.entity.getX();
                        double d5 = this.followingEntity.getZ() - this.entity.getZ();
                        this.navigation.moveTo(this.entity.getX() - d4, this.entity.getY(), this.entity.getZ() - d5, this.speedModifier);
                    }

                }
            }
        }
    }
}
