package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class AnimBlockSummon extends TamableAnimal implements IAnimatable, ISummon, IDispellable {

    public BlockState blockState;
    public int color;
    private int ticksLeft;

    protected AnimBlockSummon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AnimBlockSummon(Level pLevel, BlockState state){
        super(ModEntities.ANIMATED_BLOCK.get(), pLevel);
        this.blockState = state;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ANIMATED_BLOCK.get();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, AnimBlockSummon.class)).setAlertOthers(AnimBlockSummon.class));
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if (getOwner() != null && pEntity.isAlliedTo(getOwner())) return false;
        boolean result = super.doHurtTarget(pEntity);
        if (result) ticksLeft -= 20*20;
        return result;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        if (getOwnerID() != null) {
            if (pTarget.getUUID().equals(getOwnerID()))
                return false;
            if (pTarget instanceof ISummon summon) {
                return super.canAttack(pTarget) && !getOwnerID().equals(summon.getOwnerID());
            }
        }
        return super.canAttack(pTarget);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            ticksLeft--;
            if (ticksLeft <= 0) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                returnToFallingBlock(blockState);
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, true);
            }
        }
    }

    public void returnToFallingBlock(BlockState blockState) {
        EnchantedFallingBlock fallingBlock = new EnchantedFallingBlock(level, blockPosition(), blockState);
        fallingBlock.setOwner(this.getOwner());
        fallingBlock.setDeltaMovement(this.getDeltaMovement());
        if (blockState.getBlock() == BlockRegistry.MAGE_BLOCK) {
            fallingBlock.setColor(ParticleColor.fromInt(color));
            fallingBlock.dropItem = false;
        }
        level.addFreshEntity(fallingBlock);
    }

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(AnimBlockSummon.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(AnimBlockSummon.class, EntityDataSerializers.INT);

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
        this.entityData.define(COLOR, ParticleColor.defaultParticleColor().getColor());
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        returnToFallingBlock(getBlockState());
        onSummonDeath(level, cause, false);
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
    public boolean canMate(Animal pOtherAnimal) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public int getExperienceReward() {
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
    public UUID getOwnerID() {
        return this.getEntityData().get(OWNER_UUID).isEmpty() ? this.getUUID() : this.getEntityData().get(OWNER_UUID).get();
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walk", 20, (e) -> {
            if (e.isMoving()) {
                e.getController().setAnimation(new AnimationBuilder().addAnimation("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
    }

    public BlockState getBlockState() {
        return blockState != null ? blockState : BlockRegistry.MAGE_BLOCK.defaultBlockState();
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        this.blockState = Block.stateById(pPacket.getData());
        double d0 = pPacket.getX();
        double d1 = pPacket.getY();
        double d2 = pPacket.getZ();
        this.setPos(d0, d1, d2);
    }

    public void setColor(int color){
        this.color = color;
        this.getEntityData().set(COLOR, color);
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        pCompound.putInt("color", color);
        return super.save(pCompound);
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        this.getEntityData().set(COLOR, pCompound.getInt("color"));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.ticksLeft = pCompound.getInt("left");
        this.color = pCompound.getInt("color");
        this.blockState = Block.stateById(pCompound.getInt("blockState"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("left", ticksLeft);
        pCompound.putInt("color", color);
        pCompound.putInt("blockState", Block.getId(blockState));
    }

    public int getColor() {
        if (color == 0){
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
}
