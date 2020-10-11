package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public class GoBackHomeGoal extends DistanceRestrictedGoal{
    MobEntity entity;

    public GoBackHomeGoal(MobEntity entity, Supplier<BlockPos> pos, int maxDistance) {
        super(pos, maxDistance);
        this.entity = entity;
    }

    @Override
    public void tick() {
        if(BlockUtil.distanceFrom(entity.getPosition(), this.positionFrom) > 5){
            entity.getNavigator().tryMoveToXYZ(this.positionFrom.getX(), this.positionFrom.getY(), this.positionFrom.getZ(), 1.5);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return BlockUtil.distanceFrom(entity.getPosition(), this.positionFrom) > 5;
    }

    @Override
    public boolean shouldExecute() {
        return !this.isInRange(entity.getPosition());
    }
}
