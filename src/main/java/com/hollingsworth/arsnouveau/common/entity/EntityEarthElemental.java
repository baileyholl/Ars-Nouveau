package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.ProcessOreEvent;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketEntityAnimationSync;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

public class EntityEarthElemental extends CreatureEntity implements IAnimatedEntity {
    EntityAnimationManager manager = new EntityAnimationManager();

    EntityAnimationController<EntityEarthElemental> smeltController = new EntityAnimationController<>(this, "smeltController", 20, this::smeltPredicate);

    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.ITEMSTACK);
    public static final DataParameter<Boolean> SMELT = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.BOOLEAN);
    private <E extends Entity> boolean smeltPredicate(AnimationTestEvent<E> event) {
        return true;
    }

    protected EntityEarthElemental(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        manager.addAnimationController(smeltController);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.dataManager.get(HELD_ITEM).isEmpty() && !world.isRemote){
            for(ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.cannotPickup()) {
                    this.updateEquipmentIfNeeded(itementity);
                    Networking.sendToNearby(world, this, new PacketEntityAnimationSync(this.getEntityId(), "smeltController", "smelting"));
                    EventQueue.getInstance().addEvent(new ProcessOreEvent(this, 20 * 20));
                }
            }
        }
    }

    @Override
    public EntityAnimationManager getAnimationManager() {
        return manager;
    }
}
