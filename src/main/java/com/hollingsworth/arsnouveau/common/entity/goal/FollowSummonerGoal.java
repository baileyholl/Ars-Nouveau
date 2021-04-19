package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.api.IFollowingSummon;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;

public class FollowSummonerGoal extends Goal {
    protected final IFollowingSummon summon;
    protected final IWorldReader world;
    private final double followSpeed;
    private final PathNavigator navigator;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;

    public FollowSummonerGoal(IFollowingSummon mobEntity, LivingEntity owner, double followSpeedIn, float minDistIn, float maxDistIn) {
        this.summon = mobEntity;
        this.world = mobEntity.getWorld();
        this.followSpeed = followSpeedIn;
        this.navigator = mobEntity.getPathNav();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mobEntity.getPathNav() instanceof GroundPathNavigator) && !(mobEntity.getPathNav() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean canUse() {
        LivingEntity livingentity = summon.getSummoner();
        if (livingentity == null) {
            return false;
        } else if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).isSpectator()) {
            return false;
        } else if (this.summon instanceof TameableEntity && ((TameableEntity) this.summon).isOrderedToSit()) {
            return false;
        } else if (this.summon.getSelfEntity().distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {

        boolean flag = true;
        if(this.summon instanceof TameableEntity)
            flag = !((TameableEntity) this.summon).isOrderedToSit();

        if(this.summon.getSummoner() == null)
            return false;

        return !this.navigator.isDone() && this.summon.getSelfEntity().distanceToSqr(this.summon.getSummoner()) > (double)(this.maxDist * this.maxDist) && flag;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {

        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.summon.getSelfEntity().getPathfindingMalus(PathNodeType.WATER);
        this.summon.getSelfEntity().setPathfindingMalus(PathNodeType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {

        this.navigator.stop();
        this.summon.getSelfEntity().setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
    }


    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {

        if(this.summon.getSummoner() == null) {

            return;
        }
        this.summon.getSelfEntity().getLookControl().setLookAt(this.summon.getSummoner(), 10.0F, (float)this.summon.getSelfEntity().getMaxHeadXRot());
        if(this.summon instanceof TameableEntity && ((TameableEntity) this.summon).isOrderedToSit())
            return;

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;

            if (!this.navigator.moveTo(this.summon.getSummoner(), this.followSpeed)) {

                if (!(this.summon.getSelfEntity().distanceToSqr(this.summon.getSummoner()) < 144.0D)) {
                    int i = MathHelper.floor(this.summon.getSummoner().getX()) - 2;
                    int j = MathHelper.floor(this.summon.getSummoner().getZ()) - 2;
                    int k = MathHelper.floor(this.summon.getSummoner().getBoundingBox().minY);

                    for(int l = 0; l <= 4; ++l) {
                        for(int i1 = 0; i1 <= 4; ++i1) {
                            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.canTeleportToBlock(new BlockPos(i + l, k - 1, j + i1))) {
                                this.summon.getSelfEntity().moveTo((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.summon.getSelfEntity().yRot, this.summon.getSelfEntity().xRot);
                                this.navigator.stop();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canTeleportToBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return blockstate.isValidSpawn(this.world, pos, this.summon.getSelfEntity().getType()) && this.world.isEmptyBlock(pos.above()) && this.world.isEmptyBlock(pos.above(2));
    }
}
