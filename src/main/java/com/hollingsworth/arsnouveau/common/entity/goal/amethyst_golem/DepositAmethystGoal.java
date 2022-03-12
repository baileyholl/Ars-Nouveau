package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Supplier;

public class DepositAmethystGoal extends Goal {
    public AmethystGolem golem;
    public Supplier<Boolean> canUse;

    int usingTicks;
    boolean isDone;

    public DepositAmethystGoal(AmethystGolem golem, Supplier<Boolean> canUse){
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public boolean canContinueToUse() {
        return golem.getHome() != null && !isDone;
    }

    @Override
    public void tick() {
        super.tick();
        usingTicks--;
        if(usingTicks <= 0){
            isDone = true;
            deposit();
            return;
        }
        if(golem.getHome() == null)
            return;

        if(BlockUtil.distanceFrom(golem.blockPosition(), golem.getHome()) <= 2){
            isDone = true;
            deposit();
        }
        Path path = golem.getNavigation().createPath(golem.getHome(), 2);
        if(path != null){
            golem.getNavigation().moveTo(path, 1.3f);
        }
    }

    public void deposit(){
        BlockEntity tileEntity = golem.getLevel().getBlockEntity(golem.getHome());
        if(tileEntity == null)
            return;
        IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (iItemHandler != null) {
            ItemStack oldStack = new ItemStack(golem.getHeldStack().getItem(), golem.getHeldStack().getCount());

            ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, golem.getHeldStack(), false);
            if (left.equals(oldStack)) {
                return;
            }
            golem.setHeldStack(left);
        }
    }

    @Override
    public void start() {
        this.isDone = false;
        this.usingTicks = 80;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if(golem.getHome() == null || golem.getHeldStack().isEmpty())
            return false;
        BlockEntity entity = golem.getLevel().getBlockEntity(golem.getHome());
        return canUse.get() && entity != null && entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
    }
}
