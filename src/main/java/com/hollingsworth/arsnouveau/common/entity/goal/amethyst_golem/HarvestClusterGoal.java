package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;
import static com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider.CLUSTER_BLOCKS;

public class HarvestClusterGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    int tickTime;
    boolean isDone;

    List<BlockPos> harvestableList = new ArrayList<>();

    public HarvestClusterGoal(AmethystGolem golem, Supplier<Boolean> canUse) {
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public void tick() {
        super.tick();
        tickTime--;

        golem.getNavigation().stop();
        if (tickTime % 40 == 0) {
            tryDropAmethyst();
        }
        if (tickTime <= 0 || harvestableList.isEmpty()) {
            isDone = true;
            golem.setStomping(false);
            golem.harvestCooldown = 20 * 60;
        }
    }

    public void tryDropAmethyst() {
        List<BlockPos> harvested = new ArrayList<>();
        for (BlockPos p : harvestableList) {
            if (hasCluster(p)) {
                harvested.add(p);
                harvest(p);
                break;
            }
        }
        for (BlockPos p : harvested)
            harvestableList.remove(p);
    }

    public void harvest(BlockPos p) {
        if (!(golem.level instanceof ServerLevel level)) return;
        for (Direction d : Direction.values()) {
            BlockState state = level.getBlockState(p.relative(d));
            if (state.is(CLUSTER_BLOCKS)) {
                ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
                state.getBlock().playerDestroy(level, ANFakePlayer.getPlayer(level), p.relative(d), state, level.getBlockEntity(p), stack);
                destroyBlockSafely(level, p.relative(d), false, ANFakePlayer.getPlayer(level));
            }
        }
    }


    public boolean hasCluster(BlockPos p) {
        for (Direction d : Direction.values()) {
            if (golem.level.getBlockState(p.relative(d)).is(CLUSTER_BLOCKS)) {
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
