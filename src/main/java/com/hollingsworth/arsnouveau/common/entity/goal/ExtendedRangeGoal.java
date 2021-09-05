package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.entity.ai.goal.Goal;

public class ExtendedRangeGoal extends Goal {

    int ticksRunning;
    final int ticksPerDistance;
    public double startDistance;
    public double extendedRange;

    public ExtendedRangeGoal(int ticksPerDistance){
        this.ticksPerDistance = ticksPerDistance;
    }

    @Override
    public void tick() {
        if(startDistance == 0) {
            extendedRange = 0.0;
            return;
        }
        ticksRunning++;
        if(ticksRunning > startDistance * ticksPerDistance){
            extendedRange = 0.5 + ((ticksRunning - startDistance * ticksPerDistance) / ticksPerDistance) * 0.5;

        }
    }

    public void resetExtendedRange(){
        ticksRunning = 0;
        extendedRange = 0;
        startDistance = 0;
    }


    @Override
    public void start() {
        resetExtendedRange();
    }

    @Override
    public void stop() {
        resetExtendedRange();
    }


    @Override
    public boolean canUse() {
        return true;
    }
}
