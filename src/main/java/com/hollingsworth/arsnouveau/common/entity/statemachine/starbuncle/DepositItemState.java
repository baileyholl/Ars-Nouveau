package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class DepositItemState extends TravelToPosState {
    public DepositItemState(Starbuncle starbuncle, StarbyTransportBehavior behavior, BlockPos target) {
        super(starbuncle, behavior, target, new DecideStarbyActionState(starbuncle, behavior));
    }

    @Override
    public StarbyState onDestinationReached() {
        this.starbuncle.getNavigation().stop();

        IItemHandler iItemHandler = behavior.getItemCapFromTile(targetPos, behavior.TO_DIRECTION_MAP.get(targetPos.hashCode()));
        if (iItemHandler == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoItemHandler", "No item handler at " + targetPos.toString()));
            return nextState;
        }

        boolean didDeposit = depositStack(iItemHandler);
        if (!didDeposit) {
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            starbuncle.addGoalDebug(this, new DebugEvent("no_room", targetPos.toString()));
            return nextState;
        }

        OpenChestEvent event = new OpenChestEvent((ServerLevel) starbuncle.level, targetPos, 20);
        event.open();
        EventQueue.getServerInstance().addEvent(event);

        ItemStack left = starbuncle.getHeldStack();
        starbuncle.addGoalDebug(this, new DebugEvent("stored_item", "successful at " + targetPos.toString() + "set stack to " + left.getCount() + "x " + left.getHoverName().getString()));
        boolean fetchPassengerStack = left.isEmpty();
        while(fetchPassengerStack){
            fetchPassengerStack = false;
            starbuncle.getNextItemFromPassengers();
            if(!starbuncle.getHeldStack().isEmpty()){
                fetchPassengerStack = depositStack(iItemHandler) && starbuncle.getHeldStack().isEmpty();
            }
        }
        return nextState;
    }

    public boolean depositStack(IItemHandler iItemHandler){
        ItemStack oldStack = new ItemStack(starbuncle.getHeldStack().getItem(), starbuncle.getHeldStack().getCount());
        ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, starbuncle.getHeldStack(), false);
        starbuncle.setHeldStack(left);
        return !left.equals(oldStack);
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.sortPrefForStack(pos, starbuncle.getHeldStack()) != ItemScroll.SortPref.INVALID;
    }
}
