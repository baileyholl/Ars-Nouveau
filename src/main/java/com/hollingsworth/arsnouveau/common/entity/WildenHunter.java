package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.WildenSummon;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public class WildenHunter extends Monster implements GeoEntity, IAnimationListener {

    public static final EntityDataAccessor<String> ANIM_STATE = SynchedEntityData.defineId(WildenHunter.class, EntityDataSerializers.STRING);
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    public WildenHunter(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }

    public WildenHunter(Level worldIn) {
        this(ModEntities.WILDEN_HUNTER.get(), worldIn);
    }

    public int ramCooldown = 0;
    public int summonCooldown = 0;

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new WildenSummon(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 2D, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (Config.HUNTER_ATTACK_ANIMALS.get())
            // 1.21.11: Explicit type arg needed for NearestAttackableTargetGoal with lambda predicate
            // 1.21.11: TargetingConditions.Selector.test takes (LivingEntity, ServerLevel)
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Animal>(this, Animal.class, 10, true, false, (entity, serverLevel) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ANIM_STATE, Animations.IDLE.name());
    }

    // 1.21.11: Wolf sounds moved from SoundEvents static fields to WolfSoundVariant system.
    // Access default (CLASSIC) variant via SoundEvents.WOLF_SOUNDS map.
    private static net.minecraft.world.entity.animal.wolf.WolfSoundVariant getWolfVariant() {
        return SoundEvents.WOLF_SOUNDS.get(net.minecraft.world.entity.animal.wolf.WolfSoundVariants.SoundSet.CLASSIC);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        var v = getWolfVariant();
        return v != null ? v.hurtSound().value() : null;
    }

    protected SoundEvent getDeathSound() {
        var v = getWolfVariant();
        return v != null ? v.deathSound().value() : null;
    }

    @Override
    public int getBaseExperienceReward(ServerLevel pLevel) {
        return 5;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        super.playSound(soundIn, volume, pitch - 0.5f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        var v = getWolfVariant();
        return v != null ? v.growlSound().value() : null;
    }

    public static AttributeSupplier.Builder getModdedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide())
            return;
        if (ramCooldown > 0)
            ramCooldown--;
        if (summonCooldown > 0)
            summonCooldown--;
    }

    @Override
    public void startAnimation(int arg) {
        try {
            if (controller == null)
                return;
            if (arg == Animations.HOWL.ordinal()) {
                // GeckoLib 5: forceAnimationReset() → reset()
                controller.reset();
                controller.setAnimation(RawAnimation.begin().thenPlay("howl_master").thenPlay("idle"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private PlayState attackPredicate(AnimationTest<WildenHunter> event) {
        return PlayState.CONTINUE;
    }

    AnimationController<WildenHunter> controller;

    AnimationController<WildenHunter> runController;
    AnimationController<WildenHunter> idleController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        // GeckoLib 5: AnimationController constructor no longer takes entity as first arg
        controller = new AnimationController<WildenHunter>("attackController", 1, this::attackPredicate);
        runController = new AnimationController<WildenHunter>("runController", 1, this::runPredicate);
        idleController = new AnimationController<WildenHunter>("idleController", 1, this::idlePredicate);
        animatableManager.add(controller);
        animatableManager.add(runController);
        animatableManager.add(idleController);
    }

    private PlayState runPredicate(AnimationTest<WildenHunter> tAnimationState) {
        if (this.getEntityData().get(ANIM_STATE).equals(Animations.HOWL.name())) {
            return PlayState.STOP;
        }
        if (tAnimationState.isMoving()) {
            // GeckoLib 5: getController() → controller() (record accessor)
            tAnimationState.controller().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private PlayState idlePredicate(AnimationTest<WildenHunter> tAnimationState) {
        if (this.getEntityData().get(ANIM_STATE).equals(Animations.HOWL.name())) {
            return PlayState.STOP;
        }
        if (tAnimationState.isMoving()) {
            return PlayState.STOP;
        }
        tAnimationState.controller().setAnimation(RawAnimation.begin().thenPlay("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    public enum Animations {
        ATTACK,
        RAM,
        HOWL,
        IDLE
    }

    // 1.21.11: save(CompoundTag)/load(CompoundTag) removed; use addAdditionalSaveData/readAdditionalSaveData
    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("summonCooldown", summonCooldown);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput pCompound) {
        super.readAdditionalSaveData(pCompound);
        summonCooldown = pCompound.getIntOr("summonCooldown", 0);
    }
}
