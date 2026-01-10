package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.MageBlock;
import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalLeapGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalMeleeGoal;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class AnimBlockSummon extends TamableAnimal implements GeoEntity, ISummon, IDispellable, IEntityWithComplexSpawn {

    public BlockState blockState;
    public CompoundTag head_data = new CompoundTag();
    public int color;
    private int ticksLeft;
    public static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(AnimBlockSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> CAN_WALK = SynchedEntityData.defineId(AnimBlockSummon.class, EntityDataSerializers.BOOLEAN);
    public boolean dropItem = true;
    public boolean hasConverted = false;

    public AnimBlockSummon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AnimBlockSummon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel, CompoundTag pHeadData) {
        super(pEntityType, pLevel);
        this.head_data = pHeadData;
    }


    public AnimBlockSummon(Level pLevel, BlockState state, CompoundTag head_data) {
        this(ModEntities.ANIMATED_BLOCK.get(), pLevel);
        this.blockState = state;
        this.head_data = head_data;
    }

    @Override
    public double getAttributeValue(Holder<Attribute> pAttribute) {
        if (pAttribute.is(Attributes.ATTACK_DAMAGE)) {
            return super.getAttributeValue(pAttribute) + getStateDamageBonus();
        }
        return super.getAttributeValue(pAttribute);
    }

    public float getStateDamageBonus() {
        float destroySpeed = 1.0f;
        try {
            destroySpeed = this.blockState.getDestroySpeed(level, blockPosition());
        } catch (Exception e) {
            // Passing unexpected values here, catch any errors
        }

        return destroySpeed;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.ANIMATED_BLOCK.get();
    }

    public @Nullable SummonBehavior behavior;

    @Override
    public SummonBehavior getCurrentBehavior() {
        return behavior != null ? behavior : ISummon.super.getCurrentBehavior();
    }

    @Override
    public void setCurrentBehavior(SummonBehavior behavior) {
        this.behavior = behavior;
        reloadGoals();
    }

    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;
        this.goalSelector.availableGoals.clear();
        this.targetSelector.availableGoals.clear();
        registerGoals();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new ConditionalLeapGoal(this, 0.4F, () -> entityData.get(CAN_WALK)));
        this.goalSelector.addGoal(5, new ConditionalMeleeGoal(this, 1.0D, true, () -> entityData.get(CAN_WALK)));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        if (getCurrentBehavior() != SummonBehavior.PASSIVE) {
            this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
            this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, AnimBlockSummon.class)).setAlertOthers(AnimBlockSummon.class));
            if (getCurrentBehavior() == SummonBehavior.AGGRESSIVE) {
                // Additional aggressive behaviors can be added here
                this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, true, e -> !this.isAlliedTo(e)));
            }
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        if (getOwner() != null && pEntity.isAlliedTo(getOwner())) return false;
        boolean result = super.doHurtTarget(pEntity);
        if (result) ticksLeft -= 20 * 20;
        return result;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        if (getOwnerUUID() != null) {
            if (pTarget.getUUID().equals(getOwnerUUID()))
                return false;
            if (pTarget instanceof ISummon summon) {
                return super.canAttack(pTarget) && !getOwnerUUID().equals(summon.getOwnerUUID());
            }
        }
        return super.canAttack(pTarget);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            ticksLeft--;
            this.entityData.set(AGE, this.entityData.get(AGE) + 1);
            if (this.entityData.get(AGE) > 20) {
                this.entityData.set(CAN_WALK, true);
            }
            if (ticksLeft <= 0 && !isDeadOrDying()) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                returnToFallingBlock(blockState);
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, true);
            }
        }
    }

    public void returnToFallingBlock(BlockState blockState) {
        if (hasConverted || blockState == null)
            return;
        hasConverted = true;
        EnchantedFallingBlock fallingBlock = new EnchantedFallingBlock(level, blockPosition(), blockState, null);
        fallingBlock.setOwner(this.getOwner());
        fallingBlock.setDeltaMovement(this.getDeltaMovement());
        fallingBlock.dropItem = this.dropItem;
        if (blockState.getBlock() instanceof MageBlock) {
            fallingBlock.dropItem = false;
        }

        level.addFreshEntity(fallingBlock);
    }

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(AnimBlockSummon.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(AnimBlockSummon.class, EntityDataSerializers.INT);

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
        pBuilder.define(COLOR, ParticleColor.defaultParticleColor().getColor());
        pBuilder.define(AGE, 0);
        pBuilder.define(CAN_WALK, false);
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        returnToFallingBlock(getBlockState());
        onSummonDeath(level, damageSource, false);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.dead ? null : super.getOwner();
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canMate(@NotNull Animal pOtherAnimal) {
        return false;
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    protected int getBaseExperienceReward() {
        return 0;
    }

    @Override
    public int getTicksLeft() {
        return ticksLeft;
    }

    @Override
    public void setTicksLeft(int ticks) {
        this.ticksLeft = ticks;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.getEntityData().get(OWNER_UUID).isEmpty() ? this.getUUID() : this.getEntityData().get(OWNER_UUID).get();
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        String spawnAnim = "spawn";
        data.add(new AnimationController<>(this, spawnAnim, 1, (e) -> {
            if (!entityData.get(CAN_WALK)) {
                e.getController().setAnimation(RawAnimation.begin().thenPlay(spawnAnim));
                return PlayState.CONTINUE;
            }
            e.getController().forceAnimationReset();
            return PlayState.STOP;
        }));

        data.add(new AnimationController<>(this, "run", 1, (e) -> {
            if (e.isMoving() && entityData.get(CAN_WALK)) {
                e.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }

            return PlayState.STOP;
        }));
    }

    final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public BlockState getBlockState() {
        return blockState != null ? blockState : BlockRegistry.MAGE_BLOCK.get().defaultBlockState();
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity p_352287_) {
        return new ClientboundAddEntityPacket(this, p_352287_, Block.getId(this.getBlockState()));
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        this.blockState = Block.stateById(pPacket.getData());
        double d0 = pPacket.getX();
        double d1 = pPacket.getY();
        double d2 = pPacket.getZ();
        this.setPos(d0, d1, d2);
    }

    public void setColor(int color) {
        this.color = color;
        this.getEntityData().set(COLOR, color);
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        pCompound.putInt("color", color);
        return super.save(pCompound);
    }

    @Override
    public void load(@NotNull CompoundTag pCompound) {
        super.load(pCompound);
        this.getEntityData().set(COLOR, pCompound.getInt("color"));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.ticksLeft = pCompound.getInt("left");
        this.color = pCompound.getInt("color");
        this.blockState = Block.stateById(pCompound.getInt("blockState"));
        this.getEntityData().set(AGE, pCompound.getInt("ticksAlive"));
        this.getEntityData().set(CAN_WALK, pCompound.getBoolean("canWalk"));
        this.dropItem = !pCompound.contains("dropItem") || pCompound.getBoolean("dropItem");
        head_data = pCompound.getCompound("head_data");
        setCurrentBehavior(SummonBehavior.fromId(pCompound.getInt("summonBehavior")));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("head_data", head_data);
        pCompound.putInt("left", ticksLeft);
        pCompound.putInt("color", color);
        pCompound.putInt("blockState", Block.getId(blockState));
        pCompound.putInt("ticksAlive", this.getEntityData().get(AGE));
        pCompound.putBoolean("canWalk", this.getEntityData().get(CAN_WALK));
        pCompound.putBoolean("dropItem", this.dropItem);
        pCompound.putInt("summonBehavior", this.getCurrentBehavior().ordinal());
    }

    public int getColor() {
        if (color == 0) {
            color = entityData.get(COLOR);
        }
        return color;
    }

    /**
     * When dispel hits a target
     *
     * @param caster The entity that cast this spell. This can be NULL in the case of runes and machines that cast spells.
     * @return Returns true if dispel was successful.
     */
    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.setTicksLeft(0);
        return true;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(head_data);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        head_data = additionalData.readNbt();
    }
}
