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
    public static final DataParameter<Boolean> isLaser = EntityDataManager.createKey(WildenGuardian.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> IS_ARMORED = EntityDataManager.createKey(WildenGuardian.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(WildenGuardian.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CLIENT_TIME = EntityDataManager.createKey(WildenGuardian.class, DataSerializers.VARINT);
    protected WildenGuardian(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 0.8d, true, WildenGuardian.Animations.ATTACK.ordinal(), () -> !this.dataManager.get(isLaser)));
        this.goalSelector.addGoal(4, new LaserAttackGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        if(Config.GUARDIAN_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (TARGET_ENTITY.equals(key)) {
            setClientAttackTime(0);
            this.targetedEntity = null;
        }

    }

    public void setClientAttackTime(int i){
        this.dataManager.set(CLIENT_TIME, i);
    }

    public int getClientAttackTime(){
        return this.dataManager.get(CLIENT_TIME);
    }

    public boolean getIsLaser(){
        return this.dataManager.get(isLaser);
    }

    public void setLaser(boolean isLasering){
        this.dataManager.set(isLaser, isLasering);
    }


    public boolean isArmored(){
        return this.dataManager.get(IS_ARMORED);
    }

    public void setArmored(boolean isArmored){
        this.dataManager.set(IS_ARMORED, isArmored);
    }


    public void setTargetedEntity(int entityId) {
        this.dataManager.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        return this.dataManager.get(TARGET_ENTITY) != 0;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return super.attackEntityAsMob(entityIn);
    }

    @Nullable
    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        } else if (this.world.isRemote) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            } else {
                Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY));
                if (entity instanceof LivingEntity) {
                    this.targetedEntity = (LivingEntity)entity;
                    return this.targetedEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.getAttackTarget();
        }
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if(!world.isRemote && armorCooldown == 0){
            setArmored(true);
            armorCooldown = 500;
            armorTimeRemaining = 250;
        }
        if(!world.isRemote && isArmored()){
            damageAmount *= 0.25;

            if(damageSrc.getTrueSource() != null ){
                damageSrc.getTrueSource().attackEntityFrom(DamageSource.causeThornsDamage(this), 3.0f);

            }

        }
        super.damageEntity(damageSrc, damageAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote){
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
                this.getLookController().setLookPositionWithEntity(livingentity, 90.0F, 90.0F);
                this.getLookController().tick();
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
    protected void registerData() {
        super.registerData();
        this.dataManager.register(isLaser, false);
        this.dataManager.register(TARGET_ENTITY, 0);
        this.dataManager.register(CLIENT_TIME, 0);
        this.dataManager.register(IS_ARMORED, false);
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
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        armorCooldown = compound.getInt("armorCooldown");
        armorTimeRemaining = compound.getInt("armorTimeRemaining");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("armorCooldown", armorCooldown);
        compound.putInt("armorTimeRemaining", armorTimeRemaining);
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    public static AttributeModifierMap.MutableAttribute getAttributes(){
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 25D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.5D)
                .createMutableAttribute(Attributes.ARMOR, 2.0D);
    }



    enum Animations{
        ATTACK

    }
}
