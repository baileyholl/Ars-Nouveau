package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.entity.goal.lily.WagGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Lily extends TamableAnimal implements IAnimatable {
    private static final EntityDataAccessor<Boolean> SIT = SynchedEntityData.defineId(Lily.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WAG = SynchedEntityData.defineId(Lily.class, EntityDataSerializers.BOOLEAN);
    public int wagTicks;

    public Lily(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Lily(Level level){
        this(ModEntities.LILY.get(), level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new WagGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if (true || this.isOwnedBy(pPlayer)) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.jumping = false;
            this.navigation.stop();
            this.setTarget(null);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public void tick() {
        super.tick();
        // print the goal that is running
//        if(!level.isClientSide) {
//            goalSelector.getRunningGoals().forEach(s -> {
//                System.out.println(s.getGoal().toString());
//            });
//        }
        if(!level.isClientSide && wagTicks > 0 && isWagging()){
            wagTicks--;
            if(wagTicks <= 0){
                setWagging(false);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIT, false);
        this.entityData.define(WAG, false);
    }

    @Override
    public boolean isOrderedToSit() {
        return this.entityData.get(SIT);
    }

    @Override
    public boolean isTame() {
        return true;
    }

    @Override
    public void setOrderedToSit(boolean pOrderedToSit) {
        this.entityData.set(SIT, pOrderedToSit);
    }


    public boolean isWagging() {
        return this.entityData.get(WAG);
    }

    public void setWagging(boolean pWagging) {
        this.entityData.set(WAG, pWagging);
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(3) == 0) {
            return this.isTame() && this.getHealth() < 10.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
        } else {
            return SoundEvents.WOLF_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isFood(ItemStack pStack) {
        Item item = pStack.getItem();
        return item.isEdible() && pStack.getFoodProperties(this).isMeat();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    public boolean isLookingAtMe(Player pPlayer) {
        Vec3 vec3 = pPlayer.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(this.getX() - pPlayer.getX(), this.getEyeY() - pPlayer.getEyeY(), this.getZ() - pPlayer.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - 0.025D / d0 && pPlayer.hasLineOfSight(this);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "walk", 1, (event) -> {
            if(event.isMoving()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        data.addAnimationController(new AnimationController(this, "idle", 1, (event) -> {
            if(!event.isMoving() && !this.isWagging()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        data.addAnimationController(new AnimationController(this, "idle_wag", 1, (event) -> {
            if(!event.isMoving() && this.isWagging()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("idle_wagging"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        data.addAnimationController(new AnimationController(this, "rest", 1, (event) -> {
            if(!event.isMoving() && this.isOrderedToSit() && !this.isWagging()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("resting"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        data.addAnimationController(new AnimationController(this, "rest_wag", 1, (event) -> {
            if(!event.isMoving() && this.isOrderedToSit() && this.isWagging()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("resting_wagging"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

    }
    AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
