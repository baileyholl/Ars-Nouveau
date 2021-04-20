package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CheckStuckGoal extends Goal {


    int ticksSinceLastProgress;
    public int countNoProgress;
    public int numTries;
    BlockPos lastProgressPos;
    Supplier<BlockPos> lastPos;
    Function<Boolean, Void> setStuck;

    public CheckStuckGoal(Supplier<BlockPos> lastPos, int numTries, Function<Boolean, Void> setStuck){
        this.lastPos = lastPos;
        this.numTries = numTries;
        this.setStuck = setStuck;
    }

    public void resetStuckCheck(){
        this.countNoProgress = 0;
        this.ticksSinceLastProgress = 0;
        this.lastProgressPos = null;
    }

    @Override
    public void start() {
        resetStuckCheck();
    }

    @Override
    public void tick() {
        ticksSinceLastProgress++;
        if(lastProgressPos == null)
            lastProgressPos = lastPos.get();

        if(ticksSinceLastProgress % 20 == 0){
            if(lastPos.get().equals(lastProgressPos)){
                countNoProgress++;
            }else{
                lastProgressPos = lastPos.get();
                countNoProgress = 0;
                setStuck.apply(false);
            }
        }
        if(countNoProgress >= numTries)
            setStuck.apply(true);
    }
}
