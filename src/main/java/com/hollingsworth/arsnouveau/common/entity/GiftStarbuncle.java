package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.goal.UntamedFindItemGoal;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GiftStarbuncle extends PathfinderMob implements GeoEntity {
    int tamingTime;
    public static final EntityDataAccessor<Boolean> BEING_TAMED = SynchedEntityData.defineId(GiftStarbuncle.class, EntityDataSerializers.BOOLEAN);

    public GiftStarbuncle(EntityType<GiftStarbuncle> type, Level level){
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getMainHandItem().isEmpty() && !level.isClientSide) {
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                if (itementity.isAlive() && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay()) {
                    this.pickUpItem(itementity);
                    if (!getMainHandItem().isEmpty())
                        break;
                }
            }
        }
        if(!isTaming())
            return;
        tamingTime++;

        if (tamingTime > 60 && !level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.STARBUNCLE_SHARD.get(), 1 + level.random.nextInt(2));
            ItemStack gift = new ItemStack(ItemsRegistry.STARBY_GIFY.get(), 1);
            level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), gift));
            level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));

            level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
            ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.POOF_MOB.get(), (ServerLevel) this.level, this.getOnPos(), 10);
            this.remove(RemovalReason.DISCARDED);
        } else if (tamingTime == 60 && level.isClientSide) {
            for (int i = 0; i < 10; i++) {
                double d0 = getX();
                double d1 = getY() + 0.1;
                double d2 = getZ();
                level.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (level.random.nextFloat() * 1 - 0.5) / 3, (level.random.nextFloat() * 1 - 0.5) / 3, (level.random.nextFloat() * 1 - 0.5) / 3);
            }
        }
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25d);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if(!this.getMainHandItem().isEmpty())
            return;
        if(!this.isTaming() && itemEntity.getItem().is(Tags.Items.NUGGETS_GOLD)){
            setItemInHand(InteractionHand.MAIN_HAND, itemEntity.getItem().split(1));
            setTaming(true);
        }
    }

    public boolean isTaming(){
        return this.entityData.get(BEING_TAMED);
    }

    public void setTaming(boolean taming){
        this.entityData.set(BEING_TAMED, taming);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new UntamedFindItemGoal(this, () -> !this.entityData.get(BEING_TAMED), i -> i.getItem().is(Tags.Items.NUGGETS_GOLD)));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 16.0F, 2.0D, 1.2D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEING_TAMED, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "walkController", 1, this::animationPredicate));
        animatableManager.add(new AnimationController<>(this, "danceController", 1, this::dancePredicate));
    }

    private PlayState animationPredicate(AnimationState<?> event) {
        if (event.isMoving() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }

    private PlayState dancePredicate(AnimationState<?> event) {
        if (this.entityData.get(BEING_TAMED)) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("dance"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
