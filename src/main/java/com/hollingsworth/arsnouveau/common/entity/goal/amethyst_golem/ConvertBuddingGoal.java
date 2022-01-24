package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.Supplier;

public class ConvertBuddingGoal extends Goal {


    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    BlockPos targetCluster;

    public ConvertBuddingGoal(AmethystGolem golem, Supplier<Boolean> canUse){
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public boolean canUse() {
        return canUse.get();
    }
}
