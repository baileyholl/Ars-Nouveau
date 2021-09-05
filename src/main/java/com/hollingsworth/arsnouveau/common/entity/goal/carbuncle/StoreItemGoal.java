package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.EnumSet;

public class StoreItemGoal extends ExtendedRangeGoal {

    private final EntityCarbuncle entityCarbuncle;
    BlockPos storePos;
    boolean unreachable;

    public StoreItemGoal(EntityCarbuncle entityCarbuncle) {
        super(25);
        this.entityCarbuncle = entityCarbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void stop() {
        super.stop();
        storePos = null;
        unreachable = false;
    }

    @Override
    public void start() {
        super.start();
        storePos = entityCarbuncle.getValidStorePos(entityCarbuncle.getHeldStack());
        if (storePos!= null && !entityCarbuncle.getHeldStack().isEmpty()) {
            Path path = entityCarbuncle.getNavigation().createPath(storePos, 1);
            entityCarbuncle.getNavigation().moveTo(path, 1.2D);
            startDistance = BlockUtil.distanceFrom(entityCarbuncle.position, storePos);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!entityCarbuncle.getHeldStack().isEmpty() && storePos != null && BlockUtil.distanceFrom(entityCarbuncle.position(), storePos) <= 2D + this.extendedRange) {
            this.entityCarbuncle.getNavigation().stop();
            World world = entityCarbuncle.level;
            TileEntity tileEntity = world.getBlockEntity(storePos);
            if(tileEntity == null)
                return;

            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if (iItemHandler != null) {
                ItemStack oldStack = new ItemStack(entityCarbuncle.getHeldStack().getItem(), entityCarbuncle.getHeldStack().getCount());

                ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, entityCarbuncle.getHeldStack(), false);
                if (left.equals(oldStack)) {
                    return;
                }
                if (world instanceof ServerWorld) {
                    OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), storePos, 20);
                    event.open();
                    EventQueue.getServerInstance().addEvent(event);
                }
                entityCarbuncle.setHeldStack(left);
                entityCarbuncle.backOff = 5;
                return;
            }
        }

        if (storePos != null && !entityCarbuncle.getHeldStack().isEmpty()) {
            setPath(storePos.getX(), storePos.getY(), storePos.getZ(), 1.3D);
        }

    }

    public void setPath(double x, double y, double z, double speedIn){
        entityCarbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if(entityCarbuncle.getNavigation().getPath() != null && !entityCarbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !unreachable && entityCarbuncle.isTamed() && entityCarbuncle.getHeldStack() != null && !entityCarbuncle.getHeldStack().isEmpty() && entityCarbuncle.backOff == 0 && storePos != null;
    }

    @Override
    public boolean canUse() {
        return entityCarbuncle.isTamed() && entityCarbuncle.getHeldStack() != null && !entityCarbuncle.getHeldStack().isEmpty() && entityCarbuncle.backOff == 0;
    }
}
