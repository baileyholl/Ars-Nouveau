package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.DiveAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.FlyHelper;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.LeapGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenMeleeAttack;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.core.BlockPos;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class WildenStalker extends Monster implements IAnimatable, IAnimationListener {
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
        this.goalSelector.addGoal(1, new LeapGoal(this));
        this.goalSelector.addGoal(1, new DiveAttackGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 1.3D, true, WildenStalker.Animations.ATTACK.ordinal(), () -> !isFlying()));
        this.goalSelector.addGoal(8, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if(Config.STALKER_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){
            if(leapCooldown > 0)
                leapCooldown--;

            if(this.isFlying() && this.isOnGround())
                this.setFlying(false);

            if(this.isFlying()) {
                timeFlying++;
            }else
                timeFlying = 0;
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if(!level.isClientSide && entityIn instanceof LivingEntity && level.getDifficulty() == Difficulty.HARD)
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

    @Override
    public void startAnimation(int arg) {
        try{
            if(arg == Animations.DIVE.ordinal()){
                flyController.markNeedsReload();
                flyController.setAnimation(new AnimationBuilder().addAnimation("dive", true));
            }

            if(arg == Animations.FLY.ordinal()){
                flyController.markNeedsReload();
                flyController.setAnimation(new AnimationBuilder().addAnimation("flying", true));
            }

            if(arg == Animations.ATTACK.ordinal()){
                if(groundController.getCurrentAnimation() != null && (groundController.getCurrentAnimation().animationName.equals("attack"))){
                    return;
                }
                groundController.markNeedsReload();
                groundController.setAnimation(new AnimationBuilder().addAnimation("attack", false).addAnimation("idle"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private <E extends Entity> PlayState flyPredicate(AnimationEvent event) {
        return isFlying() ? PlayState.CONTINUE : PlayState.STOP;
    }

    private<E extends Entity> PlayState groundPredicate(AnimationEvent e){
        return isFlying() ? PlayState.STOP : PlayState.CONTINUE;
    }

    AnimationController flyController;
    AnimationController groundController;
    @Override
    public void registerControllers(AnimationData animationData) {
        flyController = new AnimationController(this, "flyController", 1, this::flyPredicate);
        animationData.addAnimationController(flyController);
        groundController = new AnimationController(this, "groundController", 1, this::groundPredicate);
        animationData.addAnimationController(groundController);
    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if(!this.isFlying())
            super.checkFallDamage(y, onGroundIn, state, pos);
    }


    @Override
    public void travel(Vec3 travelVector) {
        if(!this.isFlying()) {
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
            BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
        }

        this.calculateEntityAnimation(this, false);
    }

    public static AttributeSupplier.Builder getModdedAttributes(){
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

    public enum Animations{
        ATTACK,
        DIVE,
        FLY
    }
}
