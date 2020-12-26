package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.CheckStuckGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.Path;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItem extends CheckStuckGoal {
    private EntityCarbuncle entityCarbuncle;
    int travelTime;
    Entity pathingEntity;

    private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (itemEntity) -> {
        return !itemEntity.cannotPickup() && itemEntity.isAlive() && TakeItemGoal.isValidItem(entityCarbuncle, itemEntity.getItem());
    };

    private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = (itemEntity -> {
        return !itemEntity.cannotPickup() && itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.GOLD_NUGGET;
    });

    public FindItem(EntityCarbuncle entityCarbuncle) {
        super(entityCarbuncle::getPosition, 4, entityCarbuncle::setStuck);
        this.entityCarbuncle = entityCarbuncle;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public Predicate<ItemEntity> getFinderItems() {
        return entityCarbuncle.isTamed() ? TRUSTED_TARGET_SELECTOR : NONTAMED_TARGET_SELECTOR;
    }

    public List<ItemEntity> nearbyItems(){
       return entityCarbuncle.world.getEntitiesWithinAABB(ItemEntity.class, entityCarbuncle.getBoundingBox().grow(8.0D, 6, 8.0D), getFinderItems());
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !entityCarbuncle.isStuck && !(pathingEntity == null || pathingEntity.removed) && travelTime < 15 * 20;
    }

    @Override
    public boolean shouldExecute() {
        if(entityCarbuncle.world.rand.nextDouble() > 0.05)
            return false;
        return !entityCarbuncle.isStuck && !nearbyItems().isEmpty() && entityCarbuncle.getHeldStack().isEmpty();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        List<ItemEntity> list = nearbyItems();
        if (itemstack.isEmpty() && !list.isEmpty()) {
            for(ItemEntity entity : list){
                if(!TakeItemGoal.isValidItem(entityCarbuncle, entity.getItem()))
                    continue;
                Path path = entityCarbuncle.getNavigator().getPathToEntity(entity, 0);
                if(path != null && path.reachesTarget()) {
                    this.pathingEntity = entity;
                    pathToTarget(pathingEntity, 1.2f);
                    entityCarbuncle.getDataManager().set(EntityCarbuncle.HOP, true);
                }
            }
        }
        travelTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
        travelTime++;
        if(pathingEntity == null || pathingEntity.removed)
            return;
        ItemStack itemstack = entityCarbuncle.getHeldStack();
        if (itemstack.isEmpty()) {
            pathToTarget(pathingEntity, 1.2f);
            entityCarbuncle.getDataManager().set(EntityCarbuncle.HOP, true);
        }
    }
    public void pathToTarget(Entity entity, double speed){
        Path path = entityCarbuncle.getNavigator().getPathToEntity(entity, 0);
        if(path != null && path.reachesTarget()) {
            entityCarbuncle.getNavigator().setPath(path, speed);
          //  entityCarbuncle.setMotion(entityCarbuncle.getMotion().add(ParticleUtil.inRange(-0.1, 0.1),0,ParticleUtil.inRange(-0.1, 0.1)));
        }
    }
}
