package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.DiveAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.FlyHelper;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.LeapGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class WildenStalker extends CreatureEntity implements IAnimatable, IAnimationListener {
    int leapCooldown;
    public Vector3d orbitOffset = Vector3d.ZERO;
    public BlockPos orbitPosition = BlockPos.ZERO;

    public static final DataParameter<Boolean> isFlying = EntityDataManager.createKey(WildenStalker.class, DataSerializers.BOOLEAN);

    protected WildenStalker(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        moveController = new FlyHelper(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new LeapGoal(this));
        goalSelector.addGoal(1, new DiveAttackGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, true));
        this.goalSelector.addGoal(8, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if(leapCooldown > 0)
            leapCooldown--;

        if(this.isFlying() && this.isOnGround())
            this.setFlying(false);
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
    protected int getExperiencePoints(PlayerEntity player) {
        return 5;
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
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("flyController");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("flying").addAnimation("dive", true));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private <E extends Entity> PlayState flyPredicate(AnimationEvent event) {
        return isFlying() ? PlayState.CONTINUE : PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "flyController", 1, this::flyPredicate));
    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if(!this.isFlying())
            super.updateFallState(y, onGroundIn, state, pos);
    }


    @Override
    public void travel(Vector3d travelVector) {
        if(!this.isFlying()) {
            super.travel(travelVector);
            return;
        }
        // Copy of FlyingEntity
        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale((double)0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.5D));
        } else {
            BlockPos ground = new BlockPos(this.getPosX(), this.getPosY() - 1.0D, this.getPosZ());
            float f = 0.91F;
            if (this.onGround) {
                f = this.world.getBlockState(ground).getSlipperiness(this.world, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround) {
                f = this.world.getBlockState(ground).getSlipperiness(this.world, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale((double)f));
        }

        this.func_233629_a_(this, false);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(isFlying, false);
    }

    public boolean isFlying() {
        return this.dataManager.get(isFlying);
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(isFlying, flying);
    }

    public enum Animations{
        ATTACK,
        DIVE,

    }
}
