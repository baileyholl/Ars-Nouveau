package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindItemState extends StarbyState{

    boolean itemStuck;
    List<ItemEntity> destList = new ArrayList<>();
    ItemEntity dest;
    int stuckTicks;
    public FindItemState(Starbuncle starbuncle, StarbyTransportBehavior behavior) {
        super(starbuncle, behavior);
    }

    @Override
    public void onStart() {
        super.onStart();

        List<ItemEntity> list = nearbyItems(starbuncle, behavior);
        if(list.isEmpty()){
            return;
        }
        for (ItemEntity entity : list) {
            if (behavior.getValidStorePos(entity.getItem()) == null)
                continue;
            destList.add(entity);
        }
        if (destList.isEmpty()) {
            return;
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
    }


    public static List<ItemEntity> nearbyItems(Starbuncle starbuncle, StarbyTransportBehavior behavior) {
        return starbuncle.level.getEntitiesOfClass(ItemEntity.class, starbuncle.getAABB(), itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && behavior.getValidStorePos(itemEntity.getItem()) != null);
    }

    @Override
    public @Nullable StarbyState tick() {
        if(dest == null){
            starbuncle.setBackOff(30 + starbuncle.level.random.nextInt(30));
            starbuncle.addGoalDebug(this, new DebugEvent("NotReachable", "No pathable items nearby"));
            return new DecideStarbyActionState(starbuncle, behavior);
        }

        if (behavior.isPickupDisabled())
            return new DecideStarbyActionState(starbuncle, behavior);
        if(ticksRunning > 20 * 15){
            starbuncle.addGoalDebug(this, new DebugEvent("TooLong", "Stopped finding item, time finding expired"));
            return new DecideStarbyActionState(starbuncle, behavior);
        }
        if(itemStuck || !starbuncle.getHeldStack().isEmpty()){
            return new DecideStarbyActionState(starbuncle, behavior);
        }
        if (dest == null || dest.getItem().isEmpty() || dest.isRemoved()) {
            itemStuck = true;
            starbuncle.addGoalDebug(this, new DebugEvent("ItemRemoved", "Item removed during goal"));
            return new DecideStarbyActionState(starbuncle, behavior);
        }
        starbuncle.minecraftPathNav.stop();
        Path path = starbuncle.minecraftPathNav.createPath(BlockPos.containing(dest.position()), 1, 9);
        if (path == null || !path.canReach()) {
            stuckTicks++;
            if (stuckTicks > 20 * 5) { // Give up after 5 seconds of being unpathable, in case we fall or jump into the air
                itemStuck = true;
                starbuncle.addGoalDebug(this, new DebugEvent("ItemStuck", "Item stuck for 5 seconds. Ending goal"));
            }
            return super.tick();
        }
        ItemStack itemstack = starbuncle.getHeldStack();
        if (!itemstack.isEmpty()) {
            starbuncle.addGoalDebug(this, new DebugEvent("ItemPickup", "Received item, ending."));
            return new DecideStarbyActionState(starbuncle, behavior);
        }
        starbuncle.getNavigation().moveTo(dest, 1.4d);
        starbuncle.addGoalDebug(this, new DebugEvent("PathTo", "Pathing to " + dest));
        return super.tick();
    }
}
