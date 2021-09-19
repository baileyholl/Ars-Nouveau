package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.PathResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItem extends Goal {
    private EntityCarbuncle entityCarbuncle;

    Entity pathingEntity;
    boolean itemStuck;

    int timeFinding;
    List<BlockPos> destList = new ArrayList<>();
    private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (itemEntity) -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && entityCarbuncle.isValidItem(itemEntity.getItem());

    private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = (itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.GOLD_NUGGET);

    @Override
    public void stop() {
        super.stop();
        itemStuck = false;
        timeFinding = 0;
        destList = new ArrayList<>();
    }

    public FindItem(EntityCarbuncle entityCarbuncle) {
        this.entityCarbuncle = entityCarbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }


    public Predicate<ItemEntity> getFinderItems() {
        return entityCarbuncle.isTamed() ? TRUSTED_TARGET_SELECTOR : NONTAMED_TARGET_SELECTOR;
    }

    public List<ItemEntity> nearbyItems(){
       return entityCarbuncle.level.getLoadedEntitiesOfClass(ItemEntity.class, entityCarbuncle.getAABB(), getFinderItems());
    }

    @Override
    public boolean canContinueToUse() {
        return timeFinding <= 20 * 30 && !itemStuck && !entityCarbuncle.isStuck && !(pathingEntity == null || pathingEntity.removed || ((ItemEntity)pathingEntity).getItem().isEmpty()) && entityCarbuncle.getHeldStack().isEmpty();
    }

    @Override
    public boolean canUse() {
        return !entityCarbuncle.isStuck && entityCarbuncle.getHeldStack().isEmpty() && !nearbyItems().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        timeFinding = 0;
        itemStuck = false;
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        destList = new ArrayList<>();
        if (itemstack.isEmpty() && !list.isEmpty() && !itemStuck) {
            for(ItemEntity entity : list){
                if(!entityCarbuncle.isValidItem(entity.getItem()))
                    continue;
                destList.add(entity.blockPosition());
            }
        }
        if(destList.isEmpty()) {
            itemStuck = true;
            return;
        }
        entityCarbuncle.getNavigation().moveToClosestPosition(destList, 1.2f);
    }

    @Override
    public void tick() {
        super.tick();
        timeFinding++;
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        if (itemstack.isEmpty()) {
            pathToTarget();
        }
    }
    public void pathToTarget(){
        PathResult result = entityCarbuncle.getNavigation().moveToClosestPosition(destList, 1.2f);
        if(result.isDone() && !result.isPathReachingDestination()) {
            itemStuck = true;
        }
    }
}
