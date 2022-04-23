package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItem extends Goal {
    private EntityCarbuncle entityCarbuncle;
    boolean itemStuck;
    int timeFinding;
    int stuckTicks;
    List<ItemEntity> destList = new ArrayList<>();
    ItemEntity dest;

    private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (itemEntity) -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && entityCarbuncle.isValidItem(itemEntity.getItem());
    private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = (itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.GOLD_NUGGET);

    @Override
    public void stop() {
        super.stop();
        itemStuck = false;
        timeFinding = 0;
        destList = new ArrayList<>();
        dest = null;
        stuckTicks = 0;
    }

    public FindItem(EntityCarbuncle entityCarbuncle) {
        this.entityCarbuncle = entityCarbuncle;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public List<ItemEntity> nearbyItems(){
        return entityCarbuncle.level.getEntitiesOfClass(ItemEntity.class, entityCarbuncle.getAABB(), entityCarbuncle.isTamed() ? TRUSTED_TARGET_SELECTOR : NONTAMED_TARGET_SELECTOR);
    }

    @Override
    public boolean canContinueToUse() {
        return timeFinding <= 20 * 15 && !itemStuck && !entityCarbuncle.isStuck && entityCarbuncle.getHeldStack().isEmpty();
    }
    @Override
    public boolean canUse() {
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        itemStuck = false;
        destList = new ArrayList<>();
        if (itemstack.isEmpty() && !list.isEmpty()) {
            for(ItemEntity entity : list){
                if(!entityCarbuncle.isValidItem(entity.getItem()))
                    continue;
                destList.add(entity);
            }
        }
        if(destList.isEmpty()) {
            return false;
        }
        Collections.shuffle(destList);
        for(ItemEntity e : destList){
            Path path = entityCarbuncle.minecraftNavigator.createPath(new BlockPos(e.position()),1);
            if(path != null && path.canReach()){
                this.dest = e;
                break;
            }
        }

        return dest != null && !entityCarbuncle.isStuck && entityCarbuncle.getHeldStack().isEmpty() && !nearbyItems().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        timeFinding = 0;
        itemStuck = false;
        stuckTicks = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if(dest == null || dest.getItem().isEmpty() || dest.removed) {
            itemStuck = true;
            return;
        }
        timeFinding++;
        entityCarbuncle.minecraftNavigator.stop();
        Path path = entityCarbuncle.minecraftNavigator.createPath(new BlockPos(dest.position()),1);
        if(path == null || !path.canReach()){
            stuckTicks++;
            if(stuckTicks > 20 * 5) { // Give up after 5 seconds of being unpathable, in case we fall or jump into the air
                itemStuck = true;
            }
            return;
        }
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        if (!itemstack.isEmpty()) {
            itemStuck = true;
            return;
        }
        entityCarbuncle.getNavigation().moveTo(dest, 1.4d);
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }
}
