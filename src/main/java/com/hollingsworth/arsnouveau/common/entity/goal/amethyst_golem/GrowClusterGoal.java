package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class GrowClusterGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    BlockPos targetCluster;
    int usingTicks;
    public GrowClusterGoal(AmethystGolem golem, Supplier<Boolean> canUse){
        this.golem = golem;
        this.canUse = canUse;
    }


    @Override
    public void tick() {
        super.tick();
        usingTicks--;
    }

    @Override
    public void start() {
        usingTicks = 0;
        for(BlockPos p : golem.harvestables){
            if(golem.level.getBlockState(p).getBlock() == Blocks.BUDDING_AMETHYST){


            }
        }
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && usingTicks > 0;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && golem.growCooldown <= 0 && false;
    }
}
