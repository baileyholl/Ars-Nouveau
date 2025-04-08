package com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryCatalogTile;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TransferGoal extends Goal {
    public int time;
    public TransferTask task;
    public boolean isDone;
    public boolean reachedFrom;
    public EntityBookwyrm bookwyrm;

    public TransferGoal(EntityBookwyrm bookwyrm) {
        this.bookwyrm = bookwyrm;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        isDone = false;
        time = 0;
        reachedFrom = false;
    }

    @Override
    public boolean canContinueToUse() {
        return task != null && !isDone;
    }

    @Override
    public boolean canUse() {
        if(bookwyrm.level.getGameTime() % 2 != 0 && bookwyrm.getRandom().nextInt(10) != 0){
            return false;
        }
        this.task = bookwyrm.getTransferTask();
        return task != null;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        time++;
        if(task == null || isDone || time > 20 * 10){
            isDone = true;
            bookwyrm.setHeldStack(ItemStack.EMPTY);
            return;
        }
        if(!reachedFrom) {
            if (BlockUtil.distanceFrom(bookwyrm.position(), new Vec3(task.from.x, task.from.y(), task.from.z())) <= 1.5) {
                reachedFrom = true;
                if(task != null){
                    bookwyrm.setHeldStack(task.stack);
                    bookwyrm.level.playSound(null, bookwyrm.getX(), bookwyrm.getY(), bookwyrm.getZ(),
                            SoundEvents.ITEM_PICKUP, bookwyrm.getSoundSource(), 0.3f + (float) ParticleUtil.inRange(-0.1, 0.1), 1.0F + (float) ParticleUtil.inRange(-0.1, 0.1));
                    if (bookwyrm.level instanceof ServerLevel serverLevel) {
                        OpenChestEvent event = new OpenChestEvent(serverLevel, BlockPos.containing(task.from.subtract(0, 1, 0)), 20);
                        event.open();
                        EventQueue.getServerInstance().addEvent(event);
                    }
                    if(bookwyrm.level.getBlockEntity(task.fromPos) instanceof RepositoryCatalogTile controllerTile){
                        controllerTile.openRandomDrawer();
                    }
                }
            } else {
                bookwyrm.getNavigation().moveTo(task.from.x(), task.from.y(), task.from.z(), 1.3d);
                if(bookwyrm.getNavigation().getPath() == null){
                    isDone = true;
                }
            }
        }else{
            if (BlockUtil.distanceFrom(bookwyrm.position(), new Vec3(task.to.x(), task.to.y(), task.to.z())) <= 1.5) {
                isDone = true;
                bookwyrm.setHeldStack(ItemStack.EMPTY);
                if (bookwyrm.level instanceof ServerLevel serverLevel) {
                    OpenChestEvent event = new OpenChestEvent(serverLevel, BlockPos.containing(task.to.subtract(0, 1, 0)), 20);
                    event.open();
                    EventQueue.getServerInstance().addEvent(event);
                }
                if(bookwyrm.level.getBlockEntity(task.toPos) instanceof RepositoryCatalogTile controllerTile){
                    controllerTile.openRandomDrawer();
                }
            } else {
                bookwyrm.getNavigation().moveTo(task.to.x(), task.to.y(), task.to.z(), 1.3d);
                if(bookwyrm.getNavigation().getPath() == null){
                    isDone = true;
                }
            }
        }
    }
}
