package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;

import java.util.function.Supplier;

public class GrowClusterGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    BlockPos pathPos;
    int usingTicks;

    boolean isDone;
    public GrowClusterGoal(AmethystGolem golem, Supplier<Boolean> canUse){
        this.golem = golem;
        this.canUse = canUse;
    }


    @Override
    public void tick() {
        super.tick();
        usingTicks--;
        if(pathPos != null){
            Path path = golem.getNavigation().createPath(pathPos, 1);
            if(path != null){
                golem.getNavigation().moveTo(path, 1.3f);
            }

            if(BlockUtil.distanceFrom(golem.blockPosition(), pathPos) <= 2){
                golem.setImbueing(true);
                golem.setImbuePos(pathPos);
            }
        }
        if(usingTicks <= 0){
            growCluster();
        }

    }

    @Override
    public void start() {
        usingTicks = 120;
        isDone = false;
        for(BlockPos p : golem.buddingBlocks){
            Path path = golem.getNavigation().createPath(p, 2);
            if(path != null && path.canReach()) {
                pathPos = p;
                break;
            }
        }
    }

    public void growCluster(){
        int numGrown = 0;
        for(BlockPos p : golem.buddingBlocks){
            if(numGrown > 3)
                break;
            if(golem.level.getBlockState(p).getBlock() == Blocks.BUDDING_AMETHYST){
                golem.level.getBlockState(p).randomTick((ServerLevel) golem.level, p, golem.getRandom());
                numGrown++;
            }
        }
        isDone = true;
        golem.growCooldown = 20 * 15;
        golem.setImbueing(false);
    }

    @Override
    public void stop() {
        golem.setImbueing(false);
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !isDone;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && golem.growCooldown <= 0 && !golem.buddingBlocks.isEmpty();
    }
}
