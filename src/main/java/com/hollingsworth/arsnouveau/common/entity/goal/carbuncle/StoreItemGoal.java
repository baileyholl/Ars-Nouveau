package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.EnumSet;

public class StoreItemGoal extends ExtendedRangeGoal {

    private final Starbuncle starbuncle;
    BlockPos storePos;
    boolean unreachable;

    public StoreItemGoal(Starbuncle starbuncle) {
        super(25);
        this.starbuncle = starbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
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
        storePos = starbuncle.getValidStorePos(starbuncle.getHeldStack());
        if (storePos != null && !starbuncle.getHeldStack().isEmpty()) {
            starbuncle.getNavigation().tryMoveToBlockPos(storePos, 1.3);
            startDistance = BlockUtil.distanceFrom(starbuncle.position, storePos);
        }
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
    }

    @Override
    public void tick() {
        super.tick();
        // Retry the valid position
        if (this.ticksRunning % 100 == 0 && starbuncle.isValidStorePos(storePos, starbuncle.getHeldStack()) != ItemScroll.SortPref.INVALID) {
            storePos = null;
            return;
        }

        if (!starbuncle.getHeldStack().isEmpty() && storePos != null && BlockUtil.distanceFrom(starbuncle.position(), storePos) <= 2D + this.extendedRange) {
            this.starbuncle.getNavigation().stop();
            Level world = starbuncle.level;
            BlockEntity tileEntity = world.getBlockEntity(storePos);
            if (tileEntity == null)
                return;

            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if (iItemHandler != null) {
                ItemStack oldStack = new ItemStack(starbuncle.getHeldStack().getItem(), starbuncle.getHeldStack().getCount());

                ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, starbuncle.getHeldStack(), false);
                if (left.equals(oldStack)) {
                    return;
                }
                if (world instanceof ServerLevel serverLevel) {
                    // Potential bug with OpenJDK causing irreproducible noClassDef errors
                    try {
                        OpenChestEvent event = new OpenChestEvent(serverLevel, storePos, 20);
                        event.open();
                        EventQueue.getServerInstance().addEvent(event);
                    } catch (Throwable ignored) {
                    }
                }
                starbuncle.setHeldStack(left);
                starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
                return;
            }
        }

        if (storePos != null && !starbuncle.getHeldStack().isEmpty()) {
            setPath(storePos.getX(), storePos.getY(), storePos.getZ(), 1.3D);
        }

    }

    public void setPath(double x, double y, double z, double speedIn) {
        starbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !unreachable && starbuncle.isTamed() && starbuncle.getHeldStack() != null && !starbuncle.getHeldStack().isEmpty() && starbuncle.getBackOff() == 0 && storePos != null;
    }

    @Override
    public boolean canUse() {
        return starbuncle.isTamed() && starbuncle.getHeldStack() != null && !starbuncle.getHeldStack().isEmpty() && starbuncle.getBackOff() == 0;
    }
}
