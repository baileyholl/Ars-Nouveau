package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;


public class TakeItemState extends TravelToPosState {
    public TakeItemState(Starbuncle starbuncle, StarbyTransportBehavior behavior, BlockPos target) {
        super(starbuncle, behavior, target, new DecideStarbyActionState(starbuncle, behavior));
    }

    @Override
    public StarbyState onDestinationReached() {
        IItemHandler iItemHandler = behavior.getItemCapFromTile(targetPos, behavior.FROM_DIRECTION_MAP.get(targetPos.hashCode()));
        if (iItemHandler == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoItemHandler", "No item handler at " + targetPos.toString()));
            return nextState;
        }

        giveStarbyStack(starbuncle, iItemHandler);
        if (starbuncle.getHeldStack().isEmpty()) {
            starbuncle.addGoalDebug(this, new DebugEvent("TakeFromChest", "No items to take? Cancelling goal."));
            return nextState;
        }

        starbuncle.addGoalDebug(this, new DebugEvent("SetHeld", "Taking " + starbuncle.getHeldStack().getHoverName().getString() + " from " + targetPos.toString()));
        starbuncle.level.playSound(null, starbuncle.getX(), starbuncle.getY(), starbuncle.getZ(),
                SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);

        OpenChestEvent event = new OpenChestEvent((ServerLevel) level, targetPos, 20);
        event.open();
        EventQueue.getServerInstance().addEvent(event);

        for (Entity entity : starbuncle.getIndirectPassengers()) {
            if (!(entity instanceof Starbuncle passenger) || !passenger.getHeldStack().isEmpty()) {
                break;
            }
            giveStarbyStack(passenger, iItemHandler);
            if (passenger.getHeldStack().isEmpty()) {
                break;
            }
            starbuncle.addGoalDebug(this, new DebugEvent("SetHeldPassenger", "Taking " + passenger.getHeldStack().getHoverName().getString() + " from " + targetPos.toString()));
        }


        return nextState;
    }

    public void giveStarbyStack(Starbuncle starbuncle, IItemHandler iItemHandler) {
        for (int j = 0; j < iItemHandler.getSlots() && starbuncle.getHeldStack().isEmpty(); j++) {
            ItemStack stack = iItemHandler.getStackInSlot(j);
            if (!stack.isEmpty()) {
                int count = behavior.getMaxTake(iItemHandler.getStackInSlot(j));
                if (count <= 0)
                    continue;
                starbuncle.setHeldStack(iItemHandler.extractItem(j, Math.min(count, stack.getMaxStackSize()), false));
            }
        }
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.isPositionValidTake(pos);
    }
}
