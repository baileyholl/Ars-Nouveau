package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.WildenSummon;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class WildenHunter extends AbstractWilden implements IAnimationListener {

    public static final EntityDataAccessor<String> ANIM_STATE = SynchedEntityData.defineId(WildenHunter.class, EntityDataSerializers.STRING);
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    public WildenHunter(EntityType<? extends AbstractWilden> type, Level worldIn) {
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
        this.goalSelector.addGoal(3, new WildenSummon(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        if (Config.HUNTER_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, Animations.IDLE.name());
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }


    @Override
    public void playSound(@NotNull SoundEvent soundIn, float volume, float pitch) {
        super.playSound(soundIn, volume, pitch - 0.5f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    public static AttributeSupplier.Builder getModdedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
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
                controller.forceAnimationReset();
                controller.setAnimation(RawAnimation.begin().thenPlay("howl_master").thenPlay("idle"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private PlayState attackPredicate(AnimationState<?> event) {
        return PlayState.CONTINUE;
    }

    AnimationController<WildenHunter> controller;

    AnimationController<WildenHunter> runController;
    AnimationController<WildenHunter> idleController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        controller = new AnimationController<>(this, "attackController", 1, this::attackPredicate);
        runController = new AnimationController<>(this, "runController", 1, this::runPredicate);
        idleController = new AnimationController<>(this, "idleController", 1, this::idlePredicate);
        animatableManager.add(controller);
        animatableManager.add(runController);
        animatableManager.add(idleController);
    }

    private <T extends GeoAnimatable> PlayState runPredicate(AnimationState<T> tAnimationState) {
        if (this.getEntityData().get(ANIM_STATE).equals(Animations.HOWL.name())) {
            return PlayState.STOP;
        }
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState idlePredicate(AnimationState<T> tAnimationState) {
        if (this.getEntityData().get(ANIM_STATE).equals(Animations.HOWL.name())) {
            return PlayState.STOP;
        }
        if (tAnimationState.isMoving()) {
            return PlayState.STOP;
        }
        tAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
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

    @Override
    public boolean save(CompoundTag pCompound) {
        pCompound.putInt("summonCooldown", summonCooldown);
        return super.save(pCompound);
    }

    @Override
    public void load(@NotNull CompoundTag pCompound) {
        super.load(pCompound);
        summonCooldown = pCompound.getInt("summonCooldown");
    }
}
