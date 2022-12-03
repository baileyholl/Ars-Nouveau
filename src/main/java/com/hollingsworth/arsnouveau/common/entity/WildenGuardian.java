package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.guardian.LaserAttackGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenMeleeAttack;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class WildenGuardian extends Monster implements IAnimatable, IAnimationListener {
    AnimationFactory manager = GeckoLibUtil.createFactory(this);
    public int laserCooldown;
    public int armorCooldown;
    public int armorTimeRemaining;
    public Vec3 orbitOffset = Vec3.ZERO;
    public BlockPos orbitPosition = BlockPos.ZERO;
    private LivingEntity targetedEntity;
    public static final EntityDataAccessor<Boolean> isLaser = SynchedEntityData.defineId(WildenGuardian.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_ARMORED = SynchedEntityData.defineId(WildenGuardian.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(WildenGuardian.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CLIENT_TIME = SynchedEntityData.defineId(WildenGuardian.class, EntityDataSerializers.INT);

    public WildenGuardian(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }

    public WildenGuardian(Level worldIn) {
        this(ModEntities.WILDEN_GUARDIAN.get(), worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 0.8d, true, WildenGuardian.Animations.ATTACK.ordinal(), () -> !this.entityData.get(isLaser)));
        this.goalSelector.addGoal(4, new LaserAttackGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        if (Config.GUARDIAN_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            setClientAttackTime(0);
            this.targetedEntity = null;
        }

    }

    public void setClientAttackTime(int i) {
        this.entityData.set(CLIENT_TIME, i);
    }

    public int getClientAttackTime() {
        return this.entityData.get(CLIENT_TIME);
    }

    public boolean getIsLaser() {
        return this.entityData.get(isLaser);
    }

    public void setLaser(boolean isLasering) {
        this.entityData.set(isLaser, isLasering);
    }


    public boolean isArmored() {
        return this.entityData.get(IS_ARMORED);
    }

    public void setArmored(boolean isArmored) {
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
                    this.targetedEntity = (LivingEntity) entity;
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
        if (!level.isClientSide && armorCooldown == 0) {
            setArmored(true);
            armorCooldown = 200;
            armorTimeRemaining = 100;
        }
        if (!level.isClientSide && isArmored() && !damageSrc.isBypassArmor()) {
            damageAmount *= 0.75;

            if (damageSrc.getEntity() != null && BlockUtil.distanceFrom(damageSrc.getEntity().position, this.position) <= 2.0 && !damageSrc.msgId.equals("thorns")) {
                damageSrc.getEntity().hurt(DamageSource.thorns(this), 3.0f);
            }

        }
        super.actuallyHurt(damageSrc, damageAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (laserCooldown > 0)
                laserCooldown--;
            if (armorTimeRemaining > 0)
                armorTimeRemaining--;

            if (armorTimeRemaining == 0 && isArmored())
                setArmored(false);

            if (armorCooldown > 0)
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
        try {
            AnimationController<?> controller = attackController;
            if (attackController == null)
                return;
            if (arg == WildenHunter.Animations.ATTACK.ordinal()) {

                if (controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("attack") || controller.getCurrentAnimation().animationName.equals("attack2") ||
                        controller.getCurrentAnimation().animationName.equals("howl"))) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack").addAnimation("idle"));
            }

            if (arg == WildenHunter.Animations.RAM.ordinal()) {
                if (controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("attack2")) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack2").addAnimation("idle"));
            }

            if (arg == WildenHunter.Animations.HOWL.ordinal()) {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("howl").addAnimation("idle"));
            }

        } catch (Exception e) {
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

    private PlayState attackPredicate(AnimationEvent<?> event) {
        return PlayState.CONTINUE;
    }

    AnimationController<WildenGuardian> attackController;

    @Override
    public void registerControllers(AnimationData animationData) {
        attackController = new AnimationController<>(this, "attackController", 1, this::attackPredicate);
        animationData.addAnimationController(attackController);
    }

    public int getAttackDuration() {
        return 80;
    }

    public float getAttackAnimationScale(float p_175477_1_) {
        return ((float) this.getClientAttackTime() + p_175477_1_) / (float) this.getAttackDuration();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        armorCooldown = compound.getInt("armorCooldown");
        armorTimeRemaining = compound.getInt("armorTimeRemaining");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("armorCooldown", armorCooldown);
        compound.putInt("armorTimeRemaining", armorTimeRemaining);
    }

    @Override
    public AnimationFactory getFactory() {
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


    enum Animations {
        ATTACK

    }
}
