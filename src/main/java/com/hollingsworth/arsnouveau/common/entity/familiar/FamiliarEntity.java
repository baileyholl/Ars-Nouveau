package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.api.familiar.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamOwnerHurtByTargetGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamOwnerHurtTargetGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamiliarFollowGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.*;

public class FamiliarEntity extends PathfinderMob implements IAnimatable, IFamiliar, IDispellable {

    public double manaReserveModifier = 0.15;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public static Set<FamiliarEntity> FAMILIAR_SET = Collections.newSetFromMap(new WeakHashMap<>());

    public boolean terminatedFamiliar;
    public String holderID;
    public PersistentFamiliarData persistentData = new PersistentFamiliarData(new CompoundTag());

    public FamiliarEntity(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        if(!level.isClientSide)
            FAMILIAR_SET.add(this);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    public double getManaReserveModifier(){
        return manaReserveModifier;
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        persistentData.name = pName;
        syncTag();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !terminatedFamiliar && (level.isClientSide || FamiliarEntity.FAMILIAR_SET.contains(this));
    }

    @Override
    public void tick() {
        super.tick();
        if(this.terminatedFamiliar){
            this.remove(RemovalReason.DISCARDED);
            FamiliarEntity.FAMILIAR_SET.remove(this);
        }
        if(level.getGameTime() % 20 == 0 && !level.isClientSide){
            if(getOwnerID() == null || ((ServerLevel)level).getEntity(getOwnerID()) == null || terminatedFamiliar){
                this.remove(RemovalReason.DISCARDED);
                this.terminatedFamiliar = true;
                FAMILIAR_SET.remove(this);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FLY_INTO_WALL || source == DamageSource.FALL)
            return false;
        if(source.getEntity() == null)
            return false;
        if(source.getEntity() == getOwner())
            return false;

        return super.hurt(source, amount);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new FamiliarFollowGoal(this, 2, 6, 4));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.targetSelector.addGoal(1, new FamOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FamOwnerHurtTargetGoal(this));
    }

    public PlayState walkPredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walkController", 1, this::walkPredicate));
    }


    public boolean canTeleport(){
        return getOwner() != null && getOwner().isOnGround();
    }

    public @Nullable LivingEntity getOwner(){
        if(level.isClientSide || getOwnerID() == null)
            return null;

        return (LivingEntity) ((ServerLevel)level).getEntity(getOwnerID());
    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    @Override
    public String getHolderID() {
        return holderID;
    }

    @Override // TODO: Make abstract
    public void setHolderID(String id) {
        this.holderID = id;
    }

    public @Nullable UUID getOwnerID() {
        return this.getEntityData().get(OWNER_UUID).orElse(null);
    }

    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.of(uuid));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d).add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    @Override
    protected boolean canRide(Entity p_184228_1_) {
        return false;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(!level.isClientSide && getOwner() != null && getOwner().equals(caster)){
            this.remove(RemovalReason.DISCARDED);
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(getOwnerID() != null)
            tag.putUUID("ownerID", getOwnerID());
        tag.putBoolean("terminated", terminatedFamiliar);
        tag.put("familiarData", getPersistentFamiliarData().toTag(new CompoundTag()));
        tag.putString("holderID", holderID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.hasUUID("ownerID"))
            setOwnerID(tag.getUUID("ownerID"));
        terminatedFamiliar = tag.getBoolean("terminated");
        this.holderID = tag.getString("holderID");
        this.persistentData = deserializePersistentData(tag.getCompound("familiarData"));
        syncAfterPersistentFamiliarInit();
    }


    @Override
    public void onFamiliarSpawned(FamiliarSummonEvent event) {
        if(level.isClientSide)
            return;
        IFamiliar.super.onFamiliarSpawned(event);
        if(!event.getEntity().equals(this) && event.owner.equals(this.getOwner()))
            this.terminatedFamiliar = true;
    }

    /**
     * Called after the Familiar is returned and summoned from AbstractFamiliarHolder.
     * @param tag The persistent data tag stored on the player.
     */
    public void setTagData(@Nullable CompoundTag tag){
        this.persistentData = deserializePersistentData(tag != null && tag.contains("familiarData") ? tag.getCompound("familiarData") : new CompoundTag());
        syncAfterPersistentFamiliarInit();
    }

    /**
     * Sync the data you want to store from the familiar on the player cap here.
     * Get the owner from getOwner
     */
    public void syncTag(){
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(getOwner()).orElse(null);
        if(cap != null && persistentData != null){
            cap.getFamiliarData(getHolderID()).entityTag.put("familiarData", persistentData.toTag(new CompoundTag()));
        }
    }

    /**
     * Override and return your own implementation of PersistentData. See FamiliarStarbuncle for an example.
     */
    public PersistentFamiliarData deserializePersistentData(CompoundTag tag){
        return new PersistentFamiliarData(tag);
    }

    public PersistentFamiliarData getPersistentFamiliarData(){
        return persistentData;
    }

    /**
     * Called once the Persistent Familiar Data has been constructed.
     * Use this to de-duplify your persistent entity data as it relates to your PersistentFamiliarData.
     */
    public void syncAfterPersistentFamiliarInit(){
        setCustomName(persistentData.name);
    }
}