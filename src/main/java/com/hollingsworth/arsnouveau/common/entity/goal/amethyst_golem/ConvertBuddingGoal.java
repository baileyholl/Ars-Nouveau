package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.util.ANEventBus;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class ConvertBuddingGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    BlockPos targetCluster;
    int usingTicks;
    boolean isDone;

    public ConvertBuddingGoal(AmethystGolem golem, Supplier<Boolean> canUse) {
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public boolean canContinueToUse() {
        return targetCluster != null && !isDone;
    }

    @Override
    public void tick() {
        super.tick();
        usingTicks--;
        golem.getNavigation().tryMoveToBlockPos(targetCluster, 1.0f);
        if (usingTicks <= 0) {
            isDone = true;
            convert();
            return;
        }
        if (BlockUtil.distanceFrom(golem.blockPosition(), targetCluster) <= 2) {
            golem.setImbuePos(targetCluster);
            golem.setImbueing(true);
            usingTicks = Math.min(usingTicks, 40);
        }
    }

    public void convert() {
        if (targetCluster != null && golem.level instanceof ServerLevel level) {
            BlockState targetState = level.getBlockState(targetCluster);
            Optional<BuddingConversionRecipe> recipe = golem.recipes.stream().filter(r -> r.matches(targetState)).findFirst();
            recipe.ifPresent(r -> {
                if (!golem.canBreak(targetCluster)) {
                    golem.level.setBlock(targetCluster, r.result().defaultBlockState(), 3);
                    ParticleUtil.spawnTouchPacket(level, targetCluster, ParticleColor.defaultParticleColor());
                }
            });
        }
        golem.convertCooldown = 20 * 60 * 5;
        golem.setImbueing(false);
        golem.setImbuePos(BlockPos.ZERO);
    }

    @Override
    public void start() {
        this.isDone = false;
        this.usingTicks = 120;
        outerLoop:
        for (BlockPos pos : golem.amethystBlocks) {
            for (BuddingConversionRecipe recipe : golem.recipes) {
                if (recipe.matches(golem.level.getBlockState(pos))) {
                    targetCluster = pos;
                    break outerLoop;
                }
            }
        }
        golem.goalState = AmethystGolem.AmethystGolemGoalState.CONVERT;
    }

    @Override
    public void stop() {
        golem.setImbueing(false);
        golem.goalState = AmethystGolem.AmethystGolemGoalState.NONE;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && golem.convertCooldown <= 0 && !golem.amethystBlocks.isEmpty();
    }
}
