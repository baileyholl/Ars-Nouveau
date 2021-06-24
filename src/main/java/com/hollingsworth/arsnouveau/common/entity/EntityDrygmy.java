package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.sylph.FollowMobGoalBackoff;
import com.hollingsworth.arsnouveau.common.entity.goal.sylph.FollowPlayerGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class EntityDrygmy extends CreatureEntity implements IPickupResponder, IAnimatable, ITooltipProvider, IDispellable {

    public static final DataParameter<Boolean> CHANNELING = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> TAMED = EntityDataManager.defineId(EntityDrygmy.class, DataSerializers.BOOLEAN);
    public int channelCooldown;
    private boolean setBehaviors;
    BlockPos homePos;
    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }

    public EntityDrygmy(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        addGoalsAfterConstructor();
    }

    public EntityDrygmy(World world){
        super(ModEntities.ENTITY_DRYGMY, world);
        addGoalsAfterConstructor();
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
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
    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor(){
        if(this.level.isClientSide())
            return;

        for(PrioritizedGoal goal : getGoals()){
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<PrioritizedGoal> getGoals(){
        return this.entityData.get(TAMED) ? getTamedGoals() : getUntamedGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHANNELING, false);
        this.entityData.define(TAMED, false);
    }
    public boolean isTamed(){
        return this.entityData.get(TAMED);
    }

    public boolean isChanneling(){
        return this.entityData.get(CHANNELING);
    }

    public void setChanneling(boolean channeling){
        this.entityData.set(CHANNELING,channeling);
    }

    @Override
    protected void registerGoals() { }
    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
      //  list.add(new PrioritizedGoal(1, new GoBackHomeGoal(this, () -> this.crystalPos,20)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new FollowMobGoalBackoff(this, 1.0D, 3.0F, 7.0F, 0.5f)));
        list.add(new PrioritizedGoal(5, new FollowPlayerGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new PrioritizedGoal(2, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        return list;
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
        animationData.addAnimationController(new AnimationController<EntityDrygmy>(this, "walkController", 20, this::animationPredicate));
        animationData.addAnimationController(new AnimationController<EntityDrygmy>(this, "idleController", 20, this::idlePredicate));
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
        NBTUtil.storeBlockPos(tag, "home", homePos);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(NBTUtil.hasBlockPos(tag, "home"))
            this.homePos = NBTUtil.getBlockPos(tag, "home");
        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        if(!setBehaviors){
            tryResetGoals();
            setBehaviors = true;
        }
    }

    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals(){
        this.goalSelector.availableGoals = new LinkedHashSet<>();
        this.addGoalsAfterConstructor();
    }
}
