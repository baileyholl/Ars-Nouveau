package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebugger;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebuggerProvider;
import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalLookAtMob;
import com.hollingsworth.arsnouveau.common.entity.goal.LookAtTarget;
import com.hollingsworth.arsnouveau.common.entity.goal.UntamedFindItemGoal;
import com.hollingsworth.arsnouveau.common.entity.statemachine.SimpleStateMachine;
import com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos.DecideCrabActionState;
import com.hollingsworth.arsnouveau.common.items.data.ICharmSerializable;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class Alakarkinos extends PathfinderMob implements GeoEntity, IDispellable, ITooltipProvider, IWandable, IDebuggerProvider, IAnimationListener, ICharmSerializable {

    public boolean tamed;
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Boolean> HAS_HAT = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BLOWING_BUBBLES = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Optional<BlockPos>> BLOWING_AT = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public int findBlockCooldown;

    public boolean partyCrab = false;
    public BlockPos jukeboxPos = null;
    public BlockPos hatPos = null;
    public Vec3 lookAt = null;
    public static final EntityDataAccessor<Boolean> NEED_SOURCE = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.BOOLEAN);

    boolean beingTamed = false;

    public SimpleStateMachine stateMachine = new SimpleStateMachine(new DecideCrabActionState(this));
    int ticksBlowingAnim;
    int tamedTicks;
    public Alakarkinos(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        reloadGoals();
    }

    public Alakarkinos(Level pLevel, BlockPos pos, boolean tamed) {
        this(ModEntities.ALAKARKINOS_TYPE.get(), pLevel);
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.tamed = tamed;
        reloadGoals();
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean hasSound) {
        super.setRecordPlayingNearby(pos, hasSound);
        this.partyCrab = hasSound;
        this.jukeboxPos = pos;
    }

    @Override
    public void tick() {
        super.tick();
        if (!tamed && !beingTamed && level.getGameTime() % 40 == 0) {
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                pickUpItem(itementity);
            }
        }
        if (!tamed && this.getMainHandItem().is(ItemTags.DECORATED_POT_SHERDS)) {
            tamedTicks++;
        }
        if(tamedTicks > 60 && !level.isClientSide && !isRemoved() && !isDeadOrDying()){
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            ItemStack stack = new ItemStack(ItemsRegistry.ALAKARKINOS_SHARD);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            this.remove(RemovalReason.DISCARDED);
        }
        if (!level.isClientSide) {
            stateMachine.tick();
            SummonUtil.healOverTime(this);
            if (findBlockCooldown > 0) {
                findBlockCooldown--;
            }
        } else {

            if (blowingBubbles()) {
                ticksBlowingAnim++;
                if(ticksBlowingAnim < 10){
                    return;
                }
                var optPos = this.entityData.get(BLOWING_AT);
                if (optPos.isEmpty()) {
                    return;
                }
                BlockPos to = optPos.get();
                Vec3 towards = new Vec3(to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5);
                // Spawn particles from this mob to the target
                float randScale = 0.2f;
                Vec3 from = new Vec3(this.getX() + ParticleUtil.inRange(-randScale, randScale), this.getY() + 0.75 + ParticleUtil.inRange(-randScale, randScale), this.getZ() + ParticleUtil.inRange(-randScale, randScale));
                Vec3 dir = towards.subtract(from).normalize();
                Vec3 pos = from.add(dir.scale(0.5));
                Vec3 motion = dir.scale(0.2);
                level.addAlwaysVisibleParticle(
                        ModParticles.BUBBLE_TYPE.get(),
                        pos.x, pos.y, pos.z,motion.x, motion.y * 0.05, motion.z
                );
                if (getRandom().nextInt(20) == 0) {
                    level.playLocalSound(
                            getX(),
                            getY(),
                            getZ(),
                            SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
                            SoundSource.NEUTRAL,
                            0.8F + getRandom().nextFloat() * 0.2F,
                            0.9F + getRandom().nextFloat() * 0.15F,
                            false
                    );
                }
            }else{
                ticksBlowingAnim = 0;
            }
        }
    }
    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if (!tamed && !beingTamed && itemEntity.getItem().is(ItemTags.DECORATED_POT_SHERDS)) {
            beingTamed = true;
            ItemStack stack = itemEntity.getItem().copy();
            stack.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, stack);
            itemEntity.getItem().shrink(1);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (!level.isClientSide && tamed) {
            ItemStack stack = new ItemStack(ItemsRegistry.ALAKARKINOS_CHARM);
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
    }

    @Override
    public void fromCharmData(PersistentFamiliarData data) {
        setCustomName(data.name());
    }

    public String getColor(){
        return "red";
    }


    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;
        this.goalSelector.availableGoals.clear();
        for (WrappedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<WrappedGoal> getGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        if (tamed) {
            list.add(new WrappedGoal(4, new ConditionalLookAtMob(this, Player.class, 3.0F, 0.02F, () -> this.lookAt == null)));
            list.add(new WrappedGoal(4, new ConditionalLookAtMob(this, Mob.class, 8.0F,  () -> this.lookAt == null)));
            list.add(new WrappedGoal(4, new LookAtTarget(this, 8.0f, () -> this.lookAt)));
        }else{
            list.add(new WrappedGoal(1, new UntamedFindItemGoal(this, () -> this.getMainHandItem().isEmpty(), (e) -> e.getItem().is(ItemTags.DECORATED_POT_SHERDS))));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F)));
            list.add(new WrappedGoal(0, new FloatGoal(this)));
        }
        return list;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALAKARKINOS_TYPE.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HOME, Optional.empty());
        pBuilder.define(HAS_HAT, true);
        pBuilder.define(BLOWING_BUBBLES, false);
        pBuilder.define(BLOWING_AT, Optional.empty());
        pBuilder.define(NEED_SOURCE, false);
    }

    public void setNeedSource(boolean needSource) {
        this.entityData.set(NEED_SOURCE, needSource);
    }

    public boolean needSource() {
        return this.entityData.get(NEED_SOURCE);
    }

    public boolean hasHat() {
        return this.entityData.get(HAS_HAT);
    }

    public void setHat(boolean hasHat) {
        this.entityData.set(HAS_HAT, hasHat);
    }

    public void setBlowingBubbles(boolean blowingBubbles) {
        this.entityData.set(BLOWING_BUBBLES, blowingBubbles);
    }

    public boolean blowingBubbles() {
        return this.entityData.get(BLOWING_BUBBLES);
    }

    public @Nullable BlockPos getHome() {
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(this.tamed && this.needSource()){
            tooltip.add(Component.translatable("ars_nouveau.wixie.need_mana"));
        }
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (playerEntity.level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, null) != null) {
            this.entityData.set(HOME, Optional.ofNullable(storedPos));
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.alakarkinos.set_home"));
        }
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        if (this.isRemoved())
            return false;
        if(!level.isClientSide && tamed){
            ItemStack stack = new ItemStack(ItemsRegistry.ALAKARKINOS_CHARM);
            stack.set(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, createCharmData());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    @Override
    public IDebugger getDebugger() {
        return null;
    }

    AnimationController placeHat;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walkController", 1, (event) -> {
            if (blowingBubbles()) {
                return PlayState.STOP;
            }
            if (event.isMoving() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "idleController", 1, (event) -> {
            if (blowingBubbles()) {
                return PlayState.STOP;
            }
            if (!event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "danceController", 1, (event) -> {
            if (blowingBubbles()) {
                return PlayState.STOP;
            }
            if (this.getMainHandItem().is(ItemTags.DECORATED_POT_SHERDS) || (this.partyCrab && this.jukeboxPos != null && BlockUtil.distanceFrom(position, jukeboxPos) <= 8)) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("dance"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "blowBubbles", 1, (event) -> {
            if (blowingBubbles()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("bubble_blow"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        placeHat = new AnimationController<>(this, "placeHatController", 1, (event) -> PlayState.CONTINUE);
        controllers.add(placeHat);
    }


    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        if (this.entityData.get(HOME).isPresent()) {
            pCompound.putLong("home", this.entityData.get(HOME).get().asLong());
        }
        pCompound.putInt("findBlockCooldown", findBlockCooldown);
        if (this.hatPos != null) {
            pCompound.putLong("hatPos", this.hatPos.asLong());
        }
        pCompound.putBoolean("needSource", this.needSource());
        pCompound.putBoolean("tamed", this.tamed);
        pCompound.putBoolean("beingTamed", this.beingTamed);
        pCompound.putBoolean("hasHat", this.hasHat());
        return super.save(pCompound);
    }

    boolean setBehaviors = false;

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("home")) {
            this.entityData.set(HOME, Optional.of(BlockPos.of(pCompound.getLong("home"))));
        }
        findBlockCooldown = pCompound.getInt("findBlockCooldown");
        if (pCompound.contains("hatPos")) {
            this.hatPos = BlockPos.of(pCompound.getLong("hatPos"));
        }
        this.tamed = pCompound.getBoolean("tamed");
        this.setNeedSource(pCompound.getBoolean("needSource"));
        this.beingTamed = pCompound.getBoolean("beingTamed");
        if (!setBehaviors) {
            this.goalSelector.availableGoals = new LinkedHashSet<>();
            this.reloadGoals();
            setBehaviors = true;
        }
        if(pCompound.contains("hasHat")){
            this.setHat(pCompound.getBoolean("hasHat"));
        }
    }

    public enum Animations {
        PLACE_HAT,
        BUBBLE_BLOW,
        PICKUP_HAT
    }

    @Override
    public void startAnimation(int arg) {
        if (arg == Animations.PLACE_HAT.ordinal()) {
            if (placeHat == null) {
                return;
            }
            placeHat.forceAnimationReset();
            placeHat.setAnimation(RawAnimation.begin().thenPlay("place_hat"));
        } else if (arg == Animations.BUBBLE_BLOW.ordinal()) {
            if (placeHat == null) {
                return;
            }
            placeHat.forceAnimationReset();
            placeHat.setAnimation(RawAnimation.begin().thenPlay("bubble_blow"));
        }else if(arg == Animations.PICKUP_HAT.ordinal()){
            if (placeHat == null) {
                return;
            }
            placeHat.forceAnimationReset();
            placeHat.setAnimation(RawAnimation.begin().thenPlay("pickup_hat"));
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }
}
