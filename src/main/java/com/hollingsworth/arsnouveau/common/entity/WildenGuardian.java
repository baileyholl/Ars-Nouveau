package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalMeleeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalWaterAvoidingGoal;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WildenGuardian extends Monster implements GeoEntity {
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);
    public int armorCooldown;
    public int armorTimeRemaining;
    public static final EntityDataAccessor<Boolean> IS_ARMORED = SynchedEntityData.defineId(WildenGuardian.class, EntityDataSerializers.BOOLEAN);

    public WildenGuardian(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }

    public WildenGuardian(Level worldIn) {
        this(ModEntities.WILDEN_GUARDIAN.get(), worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(5, new ConditionalMeleeGoal(this, 1.2d, true, () -> !this.isArmored()));
        this.goalSelector.addGoal(8, new ConditionalWaterAvoidingGoal(this, 1.0D, () -> !this.isArmored()));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (Config.GUARDIAN_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));
    }

    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    public boolean isArmored() {
        return this.entityData.get(IS_ARMORED);
    }

    public void setArmored(boolean isArmored) {
        this.entityData.set(IS_ARMORED, isArmored);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
        if (!level.isClientSide && armorCooldown == 0) {
            setArmored(true);
            armorCooldown = 200;
            armorTimeRemaining = 100;
            this.navigation.stop();
        }
        if (!level.isClientSide && isArmored() && !damageSrc.is(DamageTypeTags.BYPASSES_ARMOR)) {
            damageAmount *= 0.75;

            if (damageSrc.getEntity() != null && BlockUtil.distanceFrom(damageSrc.getEntity().position, this.position) <= 2.0 && !damageSrc.type().msgId().equals("thorns")) {
                damageSrc.getEntity().hurt(level.damageSources().thorns(this), 3.0f);
            }

        }
        super.actuallyHurt(damageSrc, damageAmount);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.is(DamageTypes.DROWN)){
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (armorTimeRemaining > 0)
                armorTimeRemaining--;

            if (armorTimeRemaining == 0 && isArmored()) {
                setArmored(false);
                explodeSpikes();
            }

            if (armorCooldown > 0)
                armorCooldown--;
        }
        if(isArmored() && !this.level.isClientSide){
            this.getNavigation().stop();
        }
    }

    public void explodeSpikes(){
        for (int i = 0; i < 20; i++) {
            EntityChimeraProjectile entity = new EntityChimeraProjectile(level);
            entity.shootFromRotation(this, level.random.nextInt(360), level.random.nextInt(360), 0.0f, (float) (1.0F + ParticleUtil.inRange(0.0, 0.5)), 1.0F);
            entity.setPos(position.x, position.y + 1, position.z);
            level.addFreshEntity(entity);
        }
        if (this.getTarget() != null) {
            EntityChimeraProjectile abstractarrowentity = new EntityChimeraProjectile(level);
            abstractarrowentity.setPos(getX(), getY(), getZ());
            double d0 = getTarget().getX() - getX();
            double d1 = getTarget().getY(0.3333333333333333D) - abstractarrowentity.getY();
            double d2 = getTarget().getZ() - getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, 1.0f);
            this.level.addFreshEntity(abstractarrowentity);
        }
    }

    private <T extends GeoAnimatable> PlayState runPredicate(AnimationState<T> tAnimationState) {
        if(this.isArmored()){
            return PlayState.STOP;
        }
        if(tAnimationState.isMoving()){
            tAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState idlePredicate(AnimationState<T> tAnimationState) {
        if(this.isArmored()){
            return PlayState.STOP;
        }
        if(tAnimationState.isMoving()){
            return PlayState.STOP;
        }
        tAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(IS_ARMORED, false);
    }

    private PlayState defendPredicate(AnimationState<?> event) {
        if(this.isArmored()){
            event.getController().setAnimation(RawAnimation.begin().thenPlay("defending"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    AnimationController<WildenGuardian> controller;
    AnimationController<WildenGuardian> runController;
    AnimationController<WildenGuardian> idleController;
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        controller = new AnimationController<>(this, "attackController", 1, this::defendPredicate);
        runController = new AnimationController<>(this, "runController", 1, this::runPredicate);
        idleController = new AnimationController<>(this, "idleController", 1, this::idlePredicate);
        animatableManager.add(controller);
        animatableManager.add(runController);
        animatableManager.add(idleController);
    }

    public int getAttackDuration() {
        return 80;
    }

    @Override
    public boolean save(CompoundTag compound) {
        compound.putInt("armorCooldown", armorCooldown);
        compound.putInt("armorTimeRemaining", armorTimeRemaining);
        return super.save(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        armorCooldown = compound.getInt("armorCooldown");
        armorTimeRemaining = compound.getInt("armorTimeRemaining");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    public static AttributeSupplier.Builder getModdedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98f;
    }
}
