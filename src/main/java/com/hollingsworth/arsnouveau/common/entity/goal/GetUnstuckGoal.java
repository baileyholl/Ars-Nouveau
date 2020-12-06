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

public class GetUnstuckGoal extends CheckStuckGoal {

    int numUnstucks;
    BlockPos targetPos;
    public Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH};
    Supplier<Boolean> isStuck;
    MobEntity entity;
    boolean isStuckTrying;
    Function<Boolean, Void> setUnstuck;
    public GetUnstuckGoal(MobEntity entity, Supplier<Boolean> isStuck, Function<Boolean, Void> setUnstuck){
        super(entity::getPosition, 4, null);
        this.entity = entity;
        this.isStuck = isStuck;
        this.setStuck = this::setStuckTrying;
        this.setUnstuck = setUnstuck;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public Void setStuckTrying(boolean isStuck){
        this.isStuckTrying = isStuck;
        return null;
    }

    @Override
    public void startExecuting() {
        resetStuckCheck();
        ArrayUtil.shuffleArray(directions);
        numUnstucks = 0;
        targetPos = getNextTarget();
        isStuckTrying = false;
        entity.getNavigator().clearPath();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetPos != null && isStuck.get();
    }

    @Override
    public boolean shouldExecute() {
        return isStuck.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (targetPos == null)
            return;

        if(BlockUtil.distanceFrom(entity.getPosition(), targetPos) > 0.5){
            entity.getNavigator().setPath(getPath(targetPos), 1.2D);
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
        if(entity.getAdjustedHorizontalFacing() == direction){
            return getNextTarget();
        }
        for(int i = 3; i > 1; i--){
            BlockPos posToMove = entity.getPosition().offset(direction, i);
            Path path = getPath(posToMove);
            if(path != null && path.reachesTarget()){
                return posToMove;
            }else if(getPath(posToMove.down()) != null && getPath(posToMove.down()).reachesTarget()){
                return posToMove.down();
            }else if(getPath(posToMove.down(2)) != null && getPath(posToMove.down(2)).reachesTarget()){
                return posToMove.down(2);
            }else if(getPath(posToMove.up()) != null && getPath(posToMove.up()).reachesTarget()){
                return posToMove.up();
            }else if(getPath(posToMove.up(2)) != null && getPath(posToMove.up(2)).reachesTarget()){
                return posToMove.up(2);
            }
        }

        return getNextTarget();
    }

    public Path getPath(BlockPos p){
        return entity.getNavigator().getPathToPos(p, 0);
    }
}
