package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.WildenSummon;
import com.hollingsworth.arsnouveau.setup.Config;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class WildenHunter extends Monster implements IAnimatable, IAnimationListener {

    public static final EntityDataAccessor<String> ANIM_STATE = SynchedEntityData.defineId(WildenHunter.class, EntityDataSerializers.STRING);
    AnimationFactory manager = GeckoLibUtil.createFactory(this);

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
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, Animations.IDLE.name());
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    public int getExperienceReward() {
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
        return SoundEvents.WOLF_GROWL;
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
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("howl_master").addAnimation("idle"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private PlayState attackPredicate(AnimationEvent<?> event) {
        return PlayState.CONTINUE;
    }

    AnimationController<WildenHunter> controller;

    AnimationController<WildenHunter> runController;
    AnimationController<WildenHunter> idleController;

    @Override
    public void registerControllers(AnimationData animationData) {
        controller = new AnimationController<>(this, "attackController", 1, this::attackPredicate);
        runController = new AnimationController<>(this, "runController", 1, this::runPredicate);
        idleController = new AnimationController<>(this, "idleController", 1, this::idlePredicate);
        animationData.addAnimationController(controller);
        animationData.addAnimationController(runController);
        animationData.addAnimationController(idleController);
    }

    private <T extends IAnimatable> PlayState runPredicate(AnimationEvent<T> tAnimationEvent) {
        if(this.getEntityData().get(ANIM_STATE).equals(Animations.HOWL.name())){
            return PlayState.STOP;
        }
        if(tAnimationEvent.isMoving()){
            tAnimationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends IAnimatable> PlayState idlePredicate(AnimationEvent<T> tAnimationEvent) {
        if(this.getEntityData().get(ANIM_STATE).equals(Animations.HOWL.name())){
            return PlayState.STOP;
        }
        if(tAnimationEvent.isMoving()){
            return PlayState.STOP;
        }
        tAnimationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }
    @Override
    public AnimationFactory getFactory() {
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
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        summonCooldown = pCompound.getInt("summonCooldown");
    }
}
