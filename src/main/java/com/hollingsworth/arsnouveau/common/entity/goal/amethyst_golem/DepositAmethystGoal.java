package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.function.Supplier;

public class DepositAmethystGoal extends Goal {
    public AmethystGolem golem;
    public Supplier<Boolean> canUse;

    int usingTicks;
    boolean isDone;

    public DepositAmethystGoal(AmethystGolem golem, Supplier<Boolean> canUse) {
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public boolean canContinueToUse() {
        return golem.getHome() != null && !isDone;
    }

    @Override
    public void tick() {
        super.tick();
        usingTicks--;
        if (usingTicks <= 0) {
            isDone = true;
            deposit();
            return;
        }
        if (golem.getHome() == null)
            return;

        if (BlockUtil.distanceFrom(golem.blockPosition(), golem.getHome()) <= 2) {
            isDone = true;
            deposit();
        }
        golem.getNavigation().tryMoveToBlockPos(golem.getHome(), 1);
    }

    public void deposit() {
        IItemHandler iItemHandler = golem.level.getCapability(Capabilities.ItemHandler.BLOCK, golem.getHome(), null);
        if (iItemHandler != null) {
            ItemStack oldStack = new ItemStack(golem.getMainHandItem().getItem(), golem.getMainHandItem().getCount());

            ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, golem.getMainHandItem(), false);
            if (left.equals(oldStack)) {
                return;
            }
            golem.setHeldStack(left);
        }
    }

    @Override
    public void start() {
        this.isDone = false;
        this.usingTicks = 80;
        golem.goalState = AmethystGolem.AmethystGolemGoalState.DEPOSIT;
    }

    @Override
    public void stop() {
        super.stop();
        golem.goalState = AmethystGolem.AmethystGolemGoalState.NONE;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (golem.getHome() == null || golem.getMainHandItem().isEmpty())
            return false;
        return canUse.get() &&  golem.level.getCapability(Capabilities.ItemHandler.BLOCK, golem.getHome(), null) != null;
    }
}
