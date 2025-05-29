package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TravelToPosState extends StarbyState{
    public BlockPos targetPos;
    public int ticksRunning;
    public StarbyState nextState;

    public TravelToPosState(Starbuncle starbuncle, StarbyTransportBehavior behavior, @NotNull BlockPos targetPos, StarbyState nextState) {
        super(starbuncle, behavior);
        this.targetPos = targetPos;
        this.nextState = nextState;
    }

    @Override
    public @Nullable StarbyState tick() {
        if (this.ticksRunning % 100 == 0 && !isDestinationStillValid(targetPos)) {
            starbuncle.addDebugEvent(new DebugEvent("became_invalid", "Invalid position " + targetPos.toString()));
            return nextState;
        }

        if(BlockUtil.distanceFrom(starbuncle.position().add(0, 0.5, 0), new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5)) <= 2.5D && isDestinationStillValid(targetPos)){
            return onDestinationReached();
        }

        return setPath(targetPos);
    }

    public StarbyState setPath(BlockPos pos) {
        starbuncle.getNavigation().tryMoveToBlockPos(pos, 1.3);
        starbuncle.addGoalDebug(this, new DebugEvent("path_set", "path set to " + targetPos.toString()));
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            starbuncle.addGoalDebug(this, new DebugEvent("unreachable", targetPos.toString()));
            return nextState;
        }
        return null;
    }

    public StarbyState onDestinationReached(){
        return nextState;
    }

    public boolean isDestinationStillValid(BlockPos pos){
        return true;
    }

}
