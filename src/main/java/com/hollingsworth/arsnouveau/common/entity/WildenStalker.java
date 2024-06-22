package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.entity.goal.stalker.DiveAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.FlyHelper;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.StartFlightGoal;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.AnimationState;

public class WildenStalker extends Monster implements GeoEntity {
    int leapCooldown;
    public Vec3 orbitOffset = Vec3.ZERO;
    public BlockPos orbitPosition = BlockPos.ZERO;

    public static final EntityDataAccessor<Boolean> isFlying = SynchedEntityData.defineId(WildenStalker.class, EntityDataSerializers.BOOLEAN);
    public int timeFlying;

    public WildenStalker(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
        moveControl = new FlyHelper(this);
    }

    public WildenStalker(Level worldIn) {
        this(ModEntities.WILDEN_STALKER.get(), worldIn);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new StartFlightGoal(this));
        this.goalSelector.addGoal(1, new DiveAttackGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.3f));

//        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 1.3D, true, WildenStalker.Animations.ATTACK.ordinal(), () -> !isFlying()));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (Config.STALKER_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (leapCooldown > 0)
                leapCooldown--;

            if (this.isFlying() && this.onGround())
                this.setFlying(false);

            if (this.isFlying()) {
                timeFlying++;
            } else
                timeFlying = 0;
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (!level.isClientSide && entityIn instanceof LivingEntity && level.getDifficulty() == Difficulty.HARD)
            ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
        return super.doHurtTarget(entityIn);
    }

    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return 0;
    }

    public int getLeapCooldown() {
        return leapCooldown;
    }

    public void setLeapCooldown(int leapCooldown) {
        this.leapCooldown = leapCooldown;
    }

    @Override
    public int getExperienceReward() {
        return 8;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    private PlayState flyPredicate(software.bernie.geckolib.animation.AnimationState event) {
        if(isFlying()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("fly"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private PlayState groundPredicate(software.bernie.geckolib.animation.AnimationState e) {
        if(isFlying()){
            return PlayState.STOP;
        }else if(e.isMoving()){
            e.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    AnimationController<WildenStalker> flyController;
    AnimationController<WildenStalker> groundController;
    AnimationController<WildenStalker> idleController;
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        flyController = new AnimationController<>(this, "flyController", 1, this::flyPredicate);
        animatableManager.add(flyController);
        groundController = new AnimationController<>(this, "groundController", 1, this::groundPredicate);
        animatableManager.add(groundController);
        idleController = new AnimationController<>(this, "idleController", 1, this::idlePredicate);

        animatableManager.add(idleController);
    }

    private <T extends GeoAnimatable> PlayState idlePredicate(AnimationState<T> tAnimationState) {
        if(tAnimationState.isMoving() || isFlying()){
            return PlayState.STOP;
        }
        tAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        return PlayState.CONTINUE;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (!this.isFlying())
            super.checkFallDamage(y, onGroundIn, state, pos);
    }


    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isFlying()) {
            super.travel(travelVector);
            return;
        }
        // Copy of FlyingEntity
        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
        } else {
            BlockPos ground = BlockPos.containing(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.onGround()) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround()) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
        }

        this.calculateEntityAnimation( false);
    }

    public static AttributeSupplier.Builder getModdedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.7D)
                .add(Attributes.ATTACK_DAMAGE, 2.5D);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(isFlying, false);
    }

    public boolean isFlying() {
        return this.entityData.get(isFlying);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(isFlying, flying);
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        setFlying(pCompound.getBoolean("isFlying"));
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        pCompound.putBoolean("isFlying", isFlying());
        return super.save(pCompound);
    }

    public enum Animations {
        ATTACK,
        DIVE,
        FLY
    }
}
