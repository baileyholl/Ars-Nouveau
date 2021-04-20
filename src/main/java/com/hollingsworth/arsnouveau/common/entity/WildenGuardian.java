package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.guardian.LaserAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenMeleeAttack;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
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

import javax.annotation.Nullable;

public class WildenGuardian extends MonsterEntity implements IAnimatable, IAnimationListener {
    AnimationFactory manager = new AnimationFactory(this);
    public int laserCooldown;
    public int armorCooldown;
    public int armorTimeRemaining;
    public Vector3d orbitOffset = Vector3d.ZERO;
    public BlockPos orbitPosition = BlockPos.ZERO;
    private LivingEntity targetedEntity;
    public static final DataParameter<Boolean> isLaser = EntityDataManager.defineId(WildenGuardian.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> IS_ARMORED = EntityDataManager.defineId(WildenGuardian.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.defineId(WildenGuardian.class, DataSerializers.INT);
    private static final DataParameter<Integer> CLIENT_TIME = EntityDataManager.defineId(WildenGuardian.class, DataSerializers.INT);
    protected WildenGuardian(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 0.8d, true, WildenGuardian.Animations.ATTACK.ordinal(), () -> !this.entityData.get(isLaser)));
        this.goalSelector.addGoal(4, new LaserAttackGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        if(Config.GUARDIAN_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }
    public void onSyncedDataUpdated(DataParameter<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            setClientAttackTime(0);
            this.targetedEntity = null;
        }

    }

    public void setClientAttackTime(int i){
        this.entityData.set(CLIENT_TIME, i);
    }

    public int getClientAttackTime(){
        return this.entityData.get(CLIENT_TIME);
    }

    public boolean getIsLaser(){
        return this.entityData.get(isLaser);
    }

    public void setLaser(boolean isLasering){
        this.entityData.set(isLaser, isLasering);
    }


    public boolean isArmored(){
        return this.entityData.get(IS_ARMORED);
    }

    public void setArmored(boolean isArmored){
        this.entityData.set(IS_ARMORED, isArmored);
    }


    public void setTargetedEntity(int entityId) {
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        return this.entityData.get(TARGET_ENTITY) != 0;
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        return super.doHurtTarget(entityIn);
    }

    @Nullable
    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(TARGET_ENTITY));
                if (entity instanceof LivingEntity) {
                    this.targetedEntity = (LivingEntity)entity;
                    return this.targetedEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    @Override
    protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
        if(!level.isClientSide && armorCooldown == 0){
            setArmored(true);
            armorCooldown = 500;
            armorTimeRemaining = 250;
        }
        if(!level.isClientSide && isArmored()){
            damageAmount *= 0.25;

            if(damageSrc.getEntity() != null ){
                damageSrc.getEntity().hurt(DamageSource.thorns(this), 3.0f);

            }

        }
        super.actuallyHurt(damageSrc, damageAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){
            if(laserCooldown > 0)
                laserCooldown--;
            if(armorTimeRemaining > 0)
                armorTimeRemaining--;

            if(armorTimeRemaining == 0 && isArmored())
                setArmored(false);

            if(armorCooldown > 0)
                armorCooldown--;
        }
        if (this.hasTargetedEntity() && getIsLaser()) {
            if (this.getClientAttackTime() < this.getAttackDuration()) {
                this.setClientAttackTime(this.getClientAttackTime() + 1);
            }

            LivingEntity livingentity = this.getTargetedEntity();
            if (livingentity != null) {
                this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
                this.getLookControl().tick();
            }
        }
    }

    @Override
    public void startAnimation(int arg) {
        try{
            if(arg == WildenHunter.Animations.ATTACK.ordinal()){
                AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");

                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("attack") || controller.getCurrentAnimation().animationName.equals("attack2") ||
                        controller.getCurrentAnimation().animationName.equals("howl"))) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack").addAnimation("idle"));
            }

            if(arg == WildenHunter.Animations.RAM.ordinal()){
                AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");
                if(controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("attack2")) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack2").addAnimation("idle"));
            }

            if(arg == WildenHunter.Animations.HOWL.ordinal()){
                AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("howl").addAnimation("idle"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(isLaser, false);
        this.entityData.define(TARGET_ENTITY, 0);
        this.entityData.define(CLIENT_TIME, 0);
        this.entityData.define(IS_ARMORED, false);
    }

    private <E extends Entity> PlayState attackPredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "attackController", 1, this::attackPredicate));
    }

    public int getAttackDuration() {
        return 80;
    }

    public float getAttackAnimationScale(float p_175477_1_) {
        return ((float)this.getClientAttackTime() + p_175477_1_) / (float)this.getAttackDuration();
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        armorCooldown = compound.getInt("armorCooldown");
        armorTimeRemaining = compound.getInt("armorTimeRemaining");
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("armorCooldown", armorCooldown);
        compound.putInt("armorTimeRemaining", armorTimeRemaining);
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    public static AttributeModifierMap.MutableAttribute getModdedAttributes(){
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.ARMOR, 2.0D);
    }



    enum Animations{
        ATTACK

    }
}
