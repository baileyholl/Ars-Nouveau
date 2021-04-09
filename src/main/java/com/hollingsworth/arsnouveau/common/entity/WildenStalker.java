package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.DiveAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.FlyHelper;
import com.hollingsworth.arsnouveau.common.entity.goal.stalker.LeapGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenMeleeAttack;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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

public class WildenStalker extends MonsterEntity implements IAnimatable, IAnimationListener {
    int leapCooldown;
    public Vector3d orbitOffset = Vector3d.ZERO;
    public BlockPos orbitPosition = BlockPos.ZERO;

    public static final DataParameter<Boolean> isFlying = EntityDataManager.createKey(WildenStalker.class, DataSerializers.BOOLEAN);
    public int timeFlying;
    protected WildenStalker(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
        moveController = new FlyHelper(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LeapGoal(this));
        this.goalSelector.addGoal(1, new DiveAttackGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 1.3D, true, WildenStalker.Animations.ATTACK.ordinal(), () -> !isFlying()));
        this.goalSelector.addGoal(8, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        if(Config.STALKER_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote){
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
    public boolean attackEntityAsMob(Entity entityIn) {
        if(!world.isRemote && entityIn instanceof LivingEntity)
            ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.WEAKNESS, 60, 1));
        return super.attackEntityAsMob(entityIn);
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
                controller.setAnimation(new AnimationBuilder().addAnimation("dive", true));
            }

            if(arg == Animations.FLY.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("flyController");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("flying", true));
            }

            if(arg == Animations.ATTACK.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("groundController");
                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("attack"))){
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack", false).addAnimation("idle"));
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

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "flyController", 1, this::flyPredicate));
        animationData.addAnimationController(new AnimationController(this, "groundController", 1, this::groundPredicate));
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

    public static AttributeModifierMap.MutableAttribute getAttributes(){
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 15D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 0.7D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D);
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
        FLY
    }
}
