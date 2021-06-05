package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;

public class DrygmyEntity extends CreatureEntity implements IPickupResponder, IAnimatable, ITooltipProvider, IDispellable {

    public static final DataParameter<Boolean> CHANNELING = EntityDataManager.defineId(DrygmyEntity.class, DataSerializers.BOOLEAN);

    public int channelCooldown;


    protected DrygmyEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }


    private PlayState animationPredicate(AnimationEvent event) {
      //  System.out.println(this.getSpeed());
        if(isChanneling()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("channel"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHANNELING, false);
    }

    public boolean isChanneling(){
        return this.entityData.get(CHANNELING);
    }

    public void setChanneling(boolean channeling){
        this.entityData.set(CHANNELING,channeling);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtGoal(this, MobEntity.class, 3.0F, 0.01F));
    }

    @Override
    public List<String> getTooltip() {
        return null;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        return false;
    }

    @Override
    public ItemStack onPickup(ItemStack stack) {
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<DrygmyEntity>(this, "walkController", 20, this::animationPredicate));
        animationData.addAnimationController(new AnimationController<DrygmyEntity>(this, "idleController", 20, this::idlePredicate));
    }

    private PlayState idlePredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);

    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
    }
}
