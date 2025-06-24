package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GoToBedState extends StarbyState {
    public boolean unreachable;
    public BlockPos bedPos;
    public StarbyState nextState;

    public GoToBedState(Starbuncle starbuncle, StarbyTransportBehavior behavior, StarbyState nextState) {
        super(starbuncle, behavior);
        this.nextState = nextState;
        bedPos = starbuncle.data.bedPos;
    }

    @Override
    public @Nullable StarbyState tick() {
        super.tick();
        if (ticksRunning >= 20 * 15) {
            starbuncle.addGoalDebug(this, new DebugEvent("BedTimeout", "Took too long to find bed"));
            return nextState;
        }

        BlockPos bedPos = starbuncle.data.bedPos;
        if (bedPos == null)
            return nextState;

        // Time defer these checks otherwise we will destroy TPS with blockstate lookups.
        if (starbuncle.level.getGameTime() % 10 == 0) {
            var bedValid = behavior.isBedValid(bedPos);
            var isOnBed = behavior.isOnBed();
            if (!bedValid || isOnBed || !behavior.canGoToBed()) {
                return nextState;
            }
        }
        setPath(bedPos.getX(), bedPos.getY() + 1.0, bedPos.getZ(), 1.3);
        starbuncle.addGoalDebug(this, new DebugEvent("PathToBed", "Pathing to " + bedPos.getX() + " " + bedPos.getY() + " " + bedPos.getZ()));
        return null;
    }

    public void setPath(double x, double y, double z, double speedIn) {
        starbuncle.getNavigation().tryMoveToBlockPos(BlockPos.containing(x, y, z), 1.3);
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            starbuncle.addGoalDebug(this, new DebugEvent("BedUnreachable", "Unreachable"));
            unreachable = true;
        }
    }

}
