package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HarvestClusterGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    int tickTime;
    boolean isDone;

    List<BlockPos> harvestableList = new ArrayList<>();

    public HarvestClusterGoal(AmethystGolem golem, Supplier<Boolean> canUse){
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public void tick() {
        super.tick();
        tickTime--;

        golem.getNavigation().stop();
        if(tickTime % 40 == 0){
            tryDropAmethyst();
        }
        if(tickTime <= 0 || harvestableList.isEmpty()) {
            isDone = true;
            golem.setStomping(false);
            golem.harvestCooldown = 20 * 60;
        }
    }

    public void tryDropAmethyst(){
        List<BlockPos> harvested = new ArrayList<>();
        for(BlockPos p : harvestableList){
            if(hasCluster(p)){
                harvested.add(p);
                harvest(p);
                break;
            }
        }
        for(BlockPos p : harvested)
            harvestableList.remove(p);
    }

    public void harvest(BlockPos p){
        for(Direction d : Direction.values()){
            if(golem.level.getBlockState(p.relative(d)).getBlock() == Blocks.AMETHYST_CLUSTER){
                golem.level.setBlock(p.relative(d), Blocks.AIR.defaultBlockState(), 3);
                golem.level.addFreshEntity(new ItemEntity(golem.level, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, new ItemStack(Items.AMETHYST_SHARD, 4)));
            }
        }
    }

    public boolean hasCluster(BlockPos p){
        for(Direction d : Direction.values()){
            if(golem.level.getBlockState(p.relative(d)).getBlock() == Blocks.AMETHYST_CLUSTER){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        golem.setStomping(true);
        golem.getNavigation().stop();
        isDone = false;
        harvestableList = new ArrayList<>(golem.buddingBlocks);
        Collections.shuffle(harvestableList);
        tickTime = 130;
        golem.goalState = AmethystGolem.AmethystGolemGoalState.HARVEST;
    }

    @Override
    public void stop() {
        golem.setStomping(false);
        golem.goalState = AmethystGolem.AmethystGolemGoalState.NONE;
    }

    @Override
    public boolean canContinueToUse() {
        return !isDone;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && !golem.buddingBlocks.isEmpty();
    }
}
