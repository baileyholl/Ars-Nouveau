package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class TakeItemGoal<T extends StarbyTransportBehavior> extends GoToPosGoal<T> {

    public TakeItemGoal(Starbuncle starbuncle, T behavior) {
        super(starbuncle, behavior, () -> starbuncle.getHeldStack().isEmpty());
    }

    @Override
    public boolean canUse() {
        boolean superCan = super.canUse();
        if(starbuncle.hasPassenger((e) -> true)){
            System.out.println(" im the riding one");
        }
        if(!superCan || behavior.FROM_LIST.isEmpty())
            return false;
        if(getDestination() == null){
            starbuncle.addGoalDebug(this, new DebugEvent("NoTakeDestination", "No valid take destination"));
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            return false;
        }
        if(behavior.isBedPowered()){
            starbuncle.addGoalDebug(this, new DebugEvent("BedPowered", "Bed Powered, cannot take items"));
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !behavior.isBedPowered();
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.TAKING_ITEM;
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.isPositionValidTake(pos);
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getValidTakePos();
    }

    @Override
    public boolean onDestinationReached() {
        Level world = starbuncle.level;
        BlockEntity tileEntity = world.getBlockEntity(targetPos);
        if (tileEntity == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("TakePosBroken", "Take Tile Broken" ));
            return true;
        }
        IItemHandler iItemHandler = behavior.getItemCapFromTile(tileEntity, behavior.FROM_DIRECTION_MAP.get(targetPos.hashCode()));
        if (iItemHandler == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoItemHandler", "No item handler at " + targetPos.toString()));
            return true;
        }
        for (int j = 0; j < iItemHandler.getSlots() && starbuncle.getHeldStack().isEmpty(); j++) {
            ItemStack stack = iItemHandler.getStackInSlot(j);
            if (!stack.isEmpty()) {
                int count = behavior.getMaxTake(iItemHandler.getStackInSlot(j));
                if (count <= 0)
                    continue;
                starbuncle.setHeldStack(iItemHandler.extractItem(j, Math.min(count, stack.getMaxStackSize()), false));
                starbuncle.addGoalDebug(this, new DebugEvent("SetHeld", "Taking " + count + "x " + starbuncle.getHeldStack().getHoverName().getString() + " from " + targetPos.toString()));
                starbuncle.level.playSound(null, starbuncle.getX(), starbuncle.getY(), starbuncle.getZ(),
                        SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);

                if (world instanceof ServerLevel serverLevel) {
                    try {
                        OpenChestEvent event = new OpenChestEvent(serverLevel, targetPos, 20);
                        event.open();
                        EventQueue.getServerInstance().addEvent(event);
                    } catch (Exception ignored) {
                        // Potential bug with OpenJDK causing irreproducible noClassDef errors
                    }
                }
            }
        }
        if(starbuncle.getHeldStack().isEmpty()) {
            starbuncle.addGoalDebug(this, new DebugEvent("TakeFromChest", "No items to take? Cancelling goal."));
        }
        return true;
    }
}
