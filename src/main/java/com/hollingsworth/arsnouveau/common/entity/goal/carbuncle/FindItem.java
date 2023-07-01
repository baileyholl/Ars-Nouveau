package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItem extends Goal {
    protected Starbuncle starbuncle;
    boolean itemStuck;
    int timeFinding;
    int stuckTicks;
    List<ItemEntity> destList = new ArrayList<>();
    ItemEntity dest;
    public StarbyTransportBehavior behavior;
    private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && behavior.getValidStorePos(itemEntity.getItem()) != null;

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

    public FindItem(Starbuncle starbuncle, StarbyTransportBehavior transportBehavior) {
        this.starbuncle = starbuncle;
        this.behavior = transportBehavior;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public List<ItemEntity> nearbyItems() {
        return starbuncle.level.getEntitiesOfClass(ItemEntity.class, starbuncle.getAABB(), TRUSTED_TARGET_SELECTOR);
    }

    @Override
    public boolean canContinueToUse() {
        if (behavior.isPickupDisabled())
            return false;
        if(timeFinding > 20 * 15){
            starbuncle.addGoalDebug(this, new DebugEvent("TooLong", "Stopped finding item, time finding expired"));
            return false;
        }
        return !itemStuck && starbuncle.getHeldStack().isEmpty();
    }

    @Override
    public boolean canUse() {
        if (behavior.isPickupDisabled() || !starbuncle.getHeldStack().isEmpty())
            return false;
        ItemStack itemstack = starbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        itemStuck = false;
        destList = new ArrayList<>();
        if (itemstack.isEmpty() && !list.isEmpty()) {
            for (ItemEntity entity : list) {
                if (behavior.getValidStorePos(entity.getItem()) == null)
                    continue;
                destList.add(entity);
            }
        }
        if (destList.isEmpty()) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoStacks", "No storable items nearby"));
            return false;
        }
        Collections.shuffle(destList);
        for (ItemEntity e : destList) {
            Path path = starbuncle.minecraftPathNav.createPath(BlockPos.containing(e.position()), 1, 9);
            if (path != null && path.canReach()) {
                this.dest = e;
                starbuncle.addGoalDebug(this, new DebugEvent("DestSet", "Dest set to " + e));
                break;
            }
        }
        if (dest == null) {
            starbuncle.setBackOff(30 + starbuncle.level.random.nextInt(30));
            starbuncle.addGoalDebug(this, new DebugEvent("NotReachable", "No pathable items nearby"));
            return false;
        }
        if(behavior.isBedPowered()){
            starbuncle.addGoalDebug(this, new DebugEvent("BedPowered", "Bed powered, cannot pickup items"));
            return false;
        }
        return dest != null && !nearbyItems().isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        if (dest == null || dest.getItem().isEmpty() || dest.isRemoved()) {
            itemStuck = true;
            starbuncle.addGoalDebug(this, new DebugEvent("ItemRemoved", "Item removed during goal"));
            return;
        }
        timeFinding++;
        starbuncle.minecraftPathNav.stop();
        Path path = starbuncle.minecraftPathNav.createPath(BlockPos.containing(dest.position()), 1, 9);
        if (path == null || !path.canReach()) {
            stuckTicks++;
            if (stuckTicks > 20 * 5) { // Give up after 5 seconds of being unpathable, in case we fall or jump into the air
                itemStuck = true;
                starbuncle.addGoalDebug(this, new DebugEvent("ItemStuck", "Item stuck for 5 seconds. Ending goal"));
            }
            return;
        }
        ItemStack itemstack = starbuncle.getHeldStack();
        if (!itemstack.isEmpty()) {
            itemStuck = true;
            starbuncle.addGoalDebug(this, new DebugEvent("ItemPickup", "Received item, ending."));
            return;
        }
        starbuncle.getNavigation().moveTo(dest, 1.4d);
        starbuncle.addGoalDebug(this, new DebugEvent("PathTo", "Pathing to " + dest));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }
}
