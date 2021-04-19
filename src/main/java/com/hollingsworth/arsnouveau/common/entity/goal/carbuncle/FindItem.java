package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.Path;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class FindItem extends Goal {
    private EntityCarbuncle entityCarbuncle;

    Entity pathingEntity;
    boolean itemStuck;

    int timeFinding;
    private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (itemEntity) -> {
        return !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && entityCarbuncle.isValidItem(itemEntity.getItem());
    };

    private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = (itemEntity -> {
        return !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.GOLD_NUGGET;
    });

    @Override
    public void stop() {
        super.stop();
        itemStuck = false;
        timeFinding = 0;
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
        //System.out.println(nearbyItems());
        return !entityCarbuncle.isStuck && entityCarbuncle.getHeldStack().isEmpty() && !nearbyItems().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        timeFinding = 0;

        itemStuck = false;
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        if (itemstack.isEmpty() && !list.isEmpty() && !itemStuck) {
            for(ItemEntity entity : list){
                if(!entityCarbuncle.isValidItem(entity.getItem()))
                    continue;
                Path path = entityCarbuncle.getNavigation().createPath(entity, 0);
                if(path != null && path.canReach()) {

                    this.pathingEntity = entity;
                    pathToTarget(pathingEntity, 1.2f);

                    entityCarbuncle.getEntityData().set(EntityCarbuncle.HOP, true);
                    break;
                }
            }
        }

        if(pathingEntity == null)
            itemStuck = true;

    }

    @Override
    public void tick() {
        super.tick();
        timeFinding += 1;
        if(pathingEntity == null || pathingEntity.removed)
            return;

        ItemStack itemstack = entityCarbuncle.getHeldStack();
        if (itemstack.isEmpty()) {
            pathToTarget(pathingEntity, 1.2f);
            entityCarbuncle.getEntityData().set(EntityCarbuncle.HOP, true);
        }
    }
    public void pathToTarget(Entity entity, double speed){
        Path path = entityCarbuncle.getNavigation().createPath(entity, 0);
        if(path != null && path.canReach()) {
            entityCarbuncle.getNavigation().moveTo(path, speed);
          //  entityCarbuncle.setMotion(entityCarbuncle.getMotion().add(ParticleUtil.inRange(-0.1, 0.1),0,ParticleUtil.inRange(-0.1, 0.1)));
        }
        if(path != null && !path.canReach()) {
            itemStuck = true;
        }
    }
}
