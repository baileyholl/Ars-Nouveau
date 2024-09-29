package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider.SHARD_TAG;

public class PickupAmethystGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    ItemEntity targetEntity;
    int usingTicks;
    boolean isDone;

    public PickupAmethystGoal(AmethystGolem golem, Supplier<Boolean> canUse) {
        this.golem = golem;
        this.canUse = canUse;
    }


    @Override
    public boolean canContinueToUse() {
        return targetEntity != null && !isDone;
    }

    @Override
    public void tick() {
        super.tick();

        usingTicks--;
        if (usingTicks <= 0) {
            isDone = true;
            collectStacks();
            return;
        }

        if (targetEntity == null || targetEntity.isRemoved() || !targetEntity.getItem().is(SHARD_TAG)) {
            isDone = true;
            return;
        }
        golem.getNavigation().tryMoveToBlockPos(targetEntity.blockPosition(), 1.0f);

        if (BlockUtil.distanceFrom(golem.blockPosition(), targetEntity.blockPosition()) <= 1.5) {
            collectStacks();
            isDone = true;
            golem.pickupCooldown = 60 + golem.getRandom().nextInt(10);
        }
    }

    public void collectStacks() {
        if (golem.getHome() == null) return;
        for (ItemEntity i : golem.level.getEntitiesOfClass(ItemEntity.class, new AABB(golem.getHome()).inflate(10), i -> i.getItem().is(SHARD_TAG))) {
            if (!golem.getMainHandItem().isEmpty() && i.getItem().getItem() != golem.getMainHandItem().getItem())
                continue;
            int maxTake = golem.getMainHandItem().getMaxStackSize() - golem.getMainHandItem().getCount();
            if (golem.getMainHandItem().isEmpty()) {
                golem.setHeldStack(i.getItem().copy());
                i.getItem().setCount(0);
                continue;
            }

            int toTake = Math.min(i.getItem().getCount(), maxTake);
            i.getItem().shrink(toTake);
            golem.getMainHandItem().grow(toTake);

        }
    }

    @Override
    public void stop() {
        isDone = false;
        usingTicks = 80;
        golem.goalState = AmethystGolem.AmethystGolemGoalState.NONE;
        golem.pickupCooldown = 60 + golem.getRandom().nextInt(10);
    }

    @Override
    public void start() {
        this.isDone = false;
        this.usingTicks = 80;
        for (ItemEntity entity : golem.level.getEntitiesOfClass(ItemEntity.class, new AABB(golem.getHome()).inflate(10), entity -> entity.getItem().is(SHARD_TAG))) {
            golem.getNavigation().tryMoveToBlockPos(entity.blockPosition(), 1f);
            targetEntity = entity;
            break;
        }
        if (targetEntity == null)
            isDone = true;
        golem.goalState = AmethystGolem.AmethystGolemGoalState.PICKUP;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (golem.getHome() == null)
            return false;
        return canUse.get() && golem.pickupCooldown <= 0 && golem.level.getCapability(Capabilities.ItemHandler.BLOCK, golem.getHome(), null) != null;
    }
}
