package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class StoreItemGoal<T extends StarbyTransportBehavior> extends GoToPosGoal<T> {

    public StoreItemGoal(Starbuncle starbuncle, T behavior) {
        super(starbuncle, behavior, () -> !starbuncle.getHeldStack().isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
    }

    @Override
    public BlockPos getDestination() {
        return behavior.getValidStorePos(starbuncle.getHeldStack());
    }

    @Override
    public boolean onDestinationReached() {
        this.starbuncle.getNavigation().stop();
        Level world = starbuncle.level;
        BlockEntity tileEntity = world.getBlockEntity(this.targetPos);
        if (tileEntity == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("missing_tile", "store pos broken " + targetPos.toString()));
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            return true;
        }

        IItemHandler iItemHandler = behavior.getItemCapFromTile(this.targetPos, behavior.TO_DIRECTION_MAP.get(targetPos.hashCode()));
        if (iItemHandler == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoItemHandler", "No item handler at " + targetPos.toString()));
            return true;
        }

        ItemStack oldStack = new ItemStack(starbuncle.getHeldStack().getItem(), starbuncle.getHeldStack().getCount());
        ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, starbuncle.getHeldStack(), false);
        if (left.equals(oldStack)) {
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            starbuncle.addGoalDebug(this, new DebugEvent("no_room", targetPos.toString()));
            return true;
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
        return true;
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.sortPrefForStack(pos, starbuncle.getHeldStack()) != ItemScroll.SortPref.INVALID;
    }
}
