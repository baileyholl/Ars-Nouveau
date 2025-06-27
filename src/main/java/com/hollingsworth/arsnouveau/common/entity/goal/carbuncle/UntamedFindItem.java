package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class UntamedFindItem extends Goal {
    private Starbuncle starbuncle;
    boolean itemStuck;
    int timeFinding;
    int stuckTicks;
    List<ItemEntity> destList = new ArrayList<>();
    ItemEntity dest;


    private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().is(Tags.Items.NUGGETS_GOLD);

    @Override
    public void stop() {
        super.stop();
        itemStuck = false;
        timeFinding = 0;
        destList = new ArrayList<>();
        dest = null;
        stuckTicks = 0;
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public void start() {
        super.start();
        timeFinding = 0;
        itemStuck = false;
        stuckTicks = 0;
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.HUNTING_ITEM;
    }

    public UntamedFindItem(Starbuncle starbuncle) {
        this.starbuncle = starbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public List<ItemEntity> nearbyItems() {
        return starbuncle.level.getEntitiesOfClass(ItemEntity.class, starbuncle.getAABB(), NONTAMED_TARGET_SELECTOR);
    }

    @Override
    public boolean canContinueToUse() {
        return timeFinding <= 20 * 15 && !itemStuck && starbuncle.getHeldStack().isEmpty();
    }

    @Override
    public boolean canUse() {
        if (!starbuncle.getHeldStack().isEmpty())
            return false;
        ItemStack itemstack = starbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        itemStuck = false;
        destList = new ArrayList<>();
        if (itemstack.isEmpty() && !list.isEmpty()) {
            destList.addAll(list);
        }
        if (destList.isEmpty()) {
            return false;
        }
        Collections.shuffle(destList);
        for (ItemEntity e : destList) {
            Path path = starbuncle.minecraftPathNav.createPath(BlockPos.containing(e.position()), 1, 9);
            if (path != null && path.canReach()) {
                this.dest = e;
                break;
            }
        }
        if (dest == null) {
            starbuncle.setBackOff(30 + starbuncle.level.random.nextInt(30));
        }
        return dest != null && !nearbyItems().isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        if (dest == null || dest.getItem().isEmpty() || dest.isRemoved()) {
            itemStuck = true;
            return;
        }
        timeFinding++;
        starbuncle.minecraftPathNav.stop();
        Path path = starbuncle.minecraftPathNav.createPath(BlockPos.containing(dest.position()), 1, 9);
        if (path == null || !path.canReach()) {
            stuckTicks++;
            if (stuckTicks > 20 * 5) { // Give up after 5 seconds of being unpathable, in case we fall or jump into the air
                itemStuck = true;
            }
            return;
        }
        ItemStack itemstack = starbuncle.getHeldStack();
        if (!itemstack.isEmpty()) {
            itemStuck = true;
            return;
        }
        starbuncle.getNavigation().moveTo(dest, 1.4d);
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }
}
