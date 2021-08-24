package com.hollingsworth.arsnouveau.common.entity.goal.familiar;

import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class FamiliarFollowGoal extends FamiliarBaseGoal{


    private LivingEntity theOwner;

    private final float maxDist;

    private final float minDist;

    double moveSpeed;

    public FamiliarFollowGoal(FamiliarEntity entityRobit, double moveSpeed, float min, float max) {
        super(entityRobit);
        this.moveSpeed = moveSpeed;
        minDist = min;
        maxDist = max;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity player = entity.getOwner();
        if (player == null || player.isSpectator()) {
            return false;
        } else if (entity.level.dimension() != player.level.dimension()) {
            return false;
        }else if (entity.distanceToSqr(player) < (minDist * minDist)) {
            return false;
        }
        theOwner = player;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        boolean stillRunning = !entity.getNavigation().isDone() && entity.distanceToSqr(theOwner) > (maxDist * maxDist) && theOwner.level.dimension() == entity.level.dimension();
        if(!stillRunning)
            entity.getNavigation().stop();
        return stillRunning;
    }

    @Override
    public void stop() {
        theOwner = null;
        super.stop();
    }

    @Override
    public void tick() {
        entity.getLookControl().setLookAt(theOwner, 6, entity.getMaxHeadXRot() / 10F);

        if (!entity.isPassenger()) {
            if (entity.distanceToSqr(theOwner) >= 144.0 && entity.canTeleport()) {
                BlockPos targetPos = theOwner.blockPosition();
                teleportTo(entity, targetPos.getX(), targetPos.getY() , targetPos.getZ());
            } else {
                entity.getNavigation().moveTo(theOwner, moveSpeed);
            }
        }

    }

    private int randomize(int min, int max) {
        return entity.getRandom().nextInt(max - min + 1) + min;
    }

    private void teleportTo(Entity target, int x, int y, int z) {
        entity.setPos(x,y + 0.5,z);
        entity.getNavigation().stop();
    }
}
