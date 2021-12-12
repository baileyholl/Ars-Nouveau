package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.PathResult;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItem extends Goal {
    private Starbuncle starbuncle;
    boolean itemStuck;
    int timeFinding;
    List<BlockPos> destList = new ArrayList<>();

    private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (itemEntity) -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && starbuncle.isValidItem(itemEntity.getItem());
    private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = (itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.GOLD_NUGGET);

    @Override
    public void stop() {
        super.stop();
        itemStuck = false;
        timeFinding = 0;
        destList = new ArrayList<>();
    }

    public FindItem(Starbuncle starbuncle) {
        this.starbuncle = starbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }


    public Predicate<ItemEntity> getFinderItems() {
        return starbuncle.isTamed() ? TRUSTED_TARGET_SELECTOR : NONTAMED_TARGET_SELECTOR;
    }

    public List<ItemEntity> nearbyItems(){
       return starbuncle.level.getEntitiesOfClass(ItemEntity.class, starbuncle.getAABB(), getFinderItems());
    }

    @Override
    public boolean canContinueToUse() {
        return timeFinding <= 20 * 30 && !itemStuck && !starbuncle.isStuck && starbuncle.getHeldStack().isEmpty();
    }

    @Override
    public boolean canUse() {
        return !starbuncle.isStuck && starbuncle.getHeldStack().isEmpty() && !nearbyItems().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        timeFinding = 0;
        itemStuck = false;
        ItemStack itemstack = starbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        destList = new ArrayList<>();
        if (itemstack.isEmpty() && !list.isEmpty() && !itemStuck) {
            for(ItemEntity entity : list){
                if(!starbuncle.isValidItem(entity.getItem()))
                    continue;
                destList.add(entity.blockPosition());
            }
        }
        if(destList.isEmpty()) {
            itemStuck = true;
            return;
        }
        starbuncle.getNavigation().moveToClosestPosition(destList, 1.4d);
    }

    @Override
    public void tick() {
        super.tick();
        timeFinding++;
        ItemStack itemstack = starbuncle.getHeldStack();
        if (itemstack.isEmpty()) {
            pathToTarget();
        }
    }
    public void pathToTarget(){
        PathResult result = starbuncle.getNavigation().moveToClosestPosition(destList, 1.4d);
        if(result == null){
            itemStuck = true;
            return;
        }
        if(result.isDone() && !result.isPathReachingDestination()) {
            itemStuck = true;
        }
    }
}
