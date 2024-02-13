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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class DepositItemState extends TravelToPosState {
    public DepositItemState(Starbuncle starbuncle, StarbyTransportBehavior behavior, BlockPos target) {
        super(starbuncle, behavior, target, new DecideStarbyActionState(starbuncle, behavior));
    }

    @Override
    public StarbyState onDestinationReached() {
        this.starbuncle.getNavigation().stop();
        Level world = starbuncle.level;
        BlockEntity tileEntity = world.getBlockEntity(this.targetPos);
        if (tileEntity == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("missing_tile", "store pos broken " + targetPos.toString()));
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            return nextState;
        }

        IItemHandler iItemHandler = behavior.getItemCapFromTile(tileEntity, behavior.TO_DIRECTION_MAP.get(targetPos.hashCode()));
        if (iItemHandler == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoItemHandler", "No item handler at " + targetPos.toString()));
            return nextState;
        }

        ItemStack oldStack = new ItemStack(starbuncle.getHeldStack().getItem(), starbuncle.getHeldStack().getCount());
        ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, starbuncle.getHeldStack(), false);
        if (left.equals(oldStack)) {
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            starbuncle.addGoalDebug(this, new DebugEvent("no_room", targetPos.toString()));
            return nextState;
        }

        try {
            OpenChestEvent event = new OpenChestEvent((ServerLevel) starbuncle.level, targetPos, 20);
            event.open();
            EventQueue.getServerInstance().addEvent(event);
        } catch (Exception ignored) {
            // Potential bug with OpenJDK causing irreproducible noClassDef errors
        }

        starbuncle.setHeldStack(left);
        starbuncle.addGoalDebug(this, new DebugEvent("stored_item", "successful at " + targetPos.toString() + "set stack to " + left.getCount() + "x " + left.getHoverName().getString()));
        return nextState;
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.isValidStorePos(pos, starbuncle.getHeldStack()) != ItemScroll.SortPref.INVALID;
    }
}
