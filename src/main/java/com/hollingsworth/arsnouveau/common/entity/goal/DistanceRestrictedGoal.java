package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public abstract class DistanceRestrictedGoal extends Goal {
    public BlockPos positionFrom;
    public int maxDistance;

    public DistanceRestrictedGoal(Supplier<BlockPos> pos, int maxDistance){
        this.positionFrom = pos.get();
        this.maxDistance = maxDistance;
    }

    public boolean isInRange(BlockPos pos){
        return BlockUtil.distanceFrom(pos, positionFrom) <= maxDistance;
    }

}
