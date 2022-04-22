package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.entity.ai.goal.Goal;

public class ExtendedRangeGoal extends Goal {

    public int ticksRunning;
    public int ticksPerDistance;
    public double startDistance;
    public double extendedRange;

    public ExtendedRangeGoal(int ticksPerDistance){
        this.ticksPerDistance = ticksPerDistance;
    }

    @Override
    public void tick() {
        super.tick();
        if(startDistance == 0) {
            extendedRange = 0.0;
            return;
        }
        ticksRunning++;
        if(ticksRunning > startDistance * ticksPerDistance){
            extendedRange = 0.5 + ((ticksRunning - startDistance * ticksPerDistance) / ticksPerDistance) * 0.5;

        }
    }

    public void reset(){
        ticksRunning = 0;
        extendedRange = 0;
        startDistance = 0;
    }

    @Override
    public void start() {
        super.start();
        reset();
    }

    @Override
    public void stop() {
        super.stop();
        reset();
    }


    @Override
    public boolean canUse() {
        return true;
    }
}
