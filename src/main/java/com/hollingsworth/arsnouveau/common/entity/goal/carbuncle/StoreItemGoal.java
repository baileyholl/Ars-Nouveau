package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.EnumSet;

public class StoreItemGoal extends ExtendedRangeGoal {

    private final Starbuncle starbuncle;
    BlockPos storePos;
    boolean unreachable;
    StarbyTransportBehavior behavior;

    public StoreItemGoal(Starbuncle starbuncle, StarbyTransportBehavior transportBehavior) {
        super(25);
        this.starbuncle = starbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.behavior = transportBehavior;
    }

    @Override
    public void stop() {
        super.stop();
        storePos = null;
        unreachable = false;
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public void start() {
        super.start();
        storePos = behavior.getValidStorePos(starbuncle.getHeldStack());
        if (storePos == null) {
            starbuncle.setBackOff(60 + starbuncle.level.random.nextInt(60));
            return;
        }
        if (!starbuncle.getHeldStack().isEmpty()) {
            starbuncle.getNavigation().tryMoveToBlockPos(storePos, 1.3);
            startDistance = BlockUtil.distanceFrom(starbuncle.position, storePos);
        }
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
        starbuncle.addGoalDebug(this, new DebugEvent("StoreItemGoal", "Started storing item " + starbuncle.getHeldStack().getCount() + "x " + starbuncle.getHeldStack().getHoverName().getString() + " at " + storePos.toString()));
    }

    @Override
    public void tick() {
        super.tick();
        // Retry the valid position
        if (this.ticksRunning % 100 == 0 && behavior.isValidStorePos(storePos, starbuncle.getHeldStack()) != ItemScroll.SortPref.INVALID) {
            starbuncle.addDebugEvent(new DebugEvent("became_invalid", "Invalid store position " + storePos.toString()));
            storePos = null;
            return;
        }

        if (!starbuncle.getHeldStack().isEmpty() && storePos != null && BlockUtil.distanceFrom(starbuncle.position(), storePos) <= 2D + this.extendedRange) {
            this.starbuncle.getNavigation().stop();
            Level world = starbuncle.level;
            BlockEntity tileEntity = world.getBlockEntity(storePos);
            if (tileEntity == null) {
                starbuncle.addGoalDebug(this, new DebugEvent("missing_tile", "store pos broken " + storePos.toString()));
                starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
                return;
            }

            IItemHandler iItemHandler = tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (iItemHandler != null) {
                ItemStack oldStack = new ItemStack(starbuncle.getHeldStack().getItem(), starbuncle.getHeldStack().getCount());

                ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, starbuncle.getHeldStack(), false);
                if (left.equals(oldStack)) {
                    starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
                    starbuncle.addGoalDebug(this, new DebugEvent("no_room", storePos.toString()));
                    return;
                }
                if (world instanceof ServerLevel serverLevel) {
                    try {
                        OpenChestEvent event = new OpenChestEvent(serverLevel, storePos, 20);
                        event.open();
                        EventQueue.getServerInstance().addEvent(event);
                    } catch (Exception ignored) {
                        // Potential bug with OpenJDK causing irreproducible noClassDef errors
                    }
                }
                starbuncle.setHeldStack(left);
                starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
                starbuncle.addGoalDebug(this, new DebugEvent("stored_item", "successful at " + storePos.toString() + "set stack to " + left.getCount() + "x " + left.getHoverName().getString()));
                return;
            }
        }

        if (storePos != null && !starbuncle.getHeldStack().isEmpty()) {
            setPath(storePos.getX(), storePos.getY(), storePos.getZ(), 1.3D);
            starbuncle.addGoalDebug(this, new DebugEvent("path_set", "path set to " + storePos.toString()));
        }

    }

    public void setPath(double x, double y, double z, double speedIn) {
        starbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
            starbuncle.addGoalDebug(this, new DebugEvent("unreachable", storePos.toString()));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !unreachable && !starbuncle.getHeldStack().isEmpty() && starbuncle.getBackOff() == 0 && storePos != null;
    }

    @Override
    public boolean canUse() {
        return !starbuncle.getHeldStack().isEmpty() && starbuncle.getBackOff() == 0;
    }
}
