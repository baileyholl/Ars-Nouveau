package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.util.ArrayUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class GetUnstuckGoal extends CheckStuckGoal {

    int numUnstucks;
    BlockPos targetPos;
    public Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH};
    Supplier<Boolean> isStuck;
    MobEntity entity;
    boolean isStuckTrying;
    Function<Boolean, Void> setUnstuck;
    public GetUnstuckGoal(MobEntity entity, Supplier<Boolean> isStuck, Function<Boolean, Void> setUnstuck){
        super(entity::blockPosition, 4, null);
        this.entity = entity;
        this.isStuck = isStuck;
        this.setStuck = this::setStuckTrying;
        this.setUnstuck = setUnstuck;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public Void setStuckTrying(boolean isStuck){
        this.isStuckTrying = isStuck;
        return null;
    }

    @Override
    public void start() {
        resetStuckCheck();
        ArrayUtil.shuffleArray(directions);
        numUnstucks = 0;
        targetPos = getNextTarget();
        isStuckTrying = false;
        entity.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPos != null && isStuck.get();
    }

    @Override
    public boolean canUse() {
        return isStuck.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (targetPos == null)
            return;

        if(BlockUtil.distanceFrom(entity.blockPosition(), targetPos) > 0.5){
            entity.getNavigation().moveTo(getPath(targetPos), 1.2D);
        }else{
            setUnstuck.apply(false);
            targetPos = null;
            return;
        }
        if (isStuckTrying) {
            targetPos = getNextTarget();
            resetStuckCheck();
        }
    }

    public BlockPos getNextTarget(){
        numUnstucks++;
        if(numUnstucks >= directions.length)
            return null;
        Direction direction = directions[numUnstucks];
        if(entity.getMotionDirection() == direction){
            return getNextTarget();
        }
        for(int i = 3; i > 1; i--){
            BlockPos posToMove = entity.blockPosition().relative(direction, i);
            Path path = getPath(posToMove);
            if(path != null && path.canReach()){
                return posToMove;
            }else if(getPath(posToMove.below()) != null && getPath(posToMove.below()).canReach()){
                return posToMove.below();
            }else if(getPath(posToMove.below(2)) != null && getPath(posToMove.below(2)).canReach()){
                return posToMove.below(2);
            }else if(getPath(posToMove.above()) != null && getPath(posToMove.above()).canReach()){
                return posToMove.above();
            }else if(getPath(posToMove.above(2)) != null && getPath(posToMove.above(2)).canReach()){
                return posToMove.above(2);
            }
        }

        return getNextTarget();
    }

    public Path getPath(BlockPos p){
        return entity.getNavigation().createPath(p, 0);
    }
}
