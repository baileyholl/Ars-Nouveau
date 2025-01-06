package com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FollowPlayerGoal extends Goal {

    private final Mob entity;
    private final Predicate<Player> followPredicate;
    private Player followingEntity;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;
    private final float probability;

    public FollowPlayerGoal(Mob mob, double speedModifier, float stopDistance, float areaSize, float probability) {
        this.entity = mob;
        this.followPredicate = Objects::nonNull;
        this.speedModifier = speedModifier;
        this.navigation = mob.getNavigation();
        this.stopDistance = stopDistance;
        this.areaSize = areaSize;
        this.probability = probability;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    public FollowPlayerGoal(Mob mob, double speedModifier, float stopDistance, float areaSize) {
        this(mob, speedModifier, stopDistance, areaSize, 0.001f);
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        List<Player> list = this.entity.level.getEntitiesOfClass(Player.class, this.entity.getBoundingBox().inflate(this.areaSize), this.followPredicate);
        if (!list.isEmpty()) {
            for (Player mobentity : list) {
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
        return this.followingEntity != null && !this.navigation.isDone() && this.entity.distanceToSqr(this.followingEntity) > (double) (this.stopDistance * this.stopDistance);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.entity.getPathfindingMalus(PathType.WATER);
        this.entity.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.followingEntity = null;
        this.navigation.stop();
        this.entity.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.followingEntity != null && !this.entity.isLeashed()) {
            this.entity.getLookControl().setLookAt(this.followingEntity, 10.0F, (float) this.entity.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                double d0 = this.entity.getX() - this.followingEntity.getX();
                double d1 = this.entity.getY() - this.followingEntity.getY();
                double d2 = this.entity.getZ() - this.followingEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (!(d3 <= (double) (this.stopDistance * this.stopDistance))) {
                    this.navigation.moveTo(this.followingEntity, this.speedModifier);
                } else {
                    this.navigation.stop();

                    if (d3 <= (double) this.stopDistance) {
                        double d4 = this.followingEntity.getX() - this.entity.getX();
                        double d5 = this.followingEntity.getZ() - this.entity.getZ();
                        this.navigation.moveTo(this.entity.getX() - d4, this.entity.getY(), this.entity.getZ() - d5, this.speedModifier);
                    }

                }
            }
        }
    }
}
