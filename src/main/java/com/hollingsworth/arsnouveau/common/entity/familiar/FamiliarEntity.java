package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.entity.MagicalBuddyMob;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamOwnerHurtByTargetGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamOwnerHurtTargetGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.familiar.FamiliarFollowGoal;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public abstract class FamiliarEntity extends MagicalBuddyMob implements IFamiliar, IDecoratable {

    public double manaReserveModifier = 0.15;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> COSMETIC = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.ITEM_STACK);

    // Tracks all familiars in the world, used for enforcing familiar limit.
    public static Set<FamiliarEntity> FAMILIAR_SET = Collections.newSetFromMap(new WeakHashMap<>());
    // Tracks familiars that are currently on players shoulders
    public static Set<FamiliarEntity> FAMILIAR_SHOULDER_SET = Collections.newSetFromMap(new HashMap<>());

    public boolean terminatedFamiliar;
    public ResourceLocation holderID;
    public PersistentFamiliarData persistentData = new PersistentFamiliarData();

    public FamiliarEntity(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        if (!level.isClientSide) {
            FAMILIAR_SET.add(this);
            // If the familiar is spawned from the player's shoulder, remove it from the set
            FAMILIAR_SHOULDER_SET.remove(this);
        }
    }

    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    public double getManaReserveModifier() {
        return manaReserveModifier;
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        persistentData = persistentData.setName(pName);
        syncTag();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !terminatedFamiliar && (level.isClientSide || FamiliarEntity.FAMILIAR_SET.contains(this));
    }

    public void applyTickEffects() {
        // Override to apply effects each tick
    }

    @Override
    public void tick() {
        super.tick();
        if (this.terminatedFamiliar) {
            this.remove(RemovalReason.DISCARDED);
            FamiliarEntity.FAMILIAR_SET.remove(this);
        }
        if (level.getGameTime() % 20 == 0 && !level.isClientSide) {
            if (getOwnerID() == null || ((ServerLevel) level).getEntity(getOwnerID()) == null || terminatedFamiliar) {
                this.remove(RemovalReason.DISCARDED);
                this.terminatedFamiliar = true;
                FAMILIAR_SET.remove(this);
            }
        }
        applyTickEffects();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.FLY_INTO_WALL) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL))
            return false;
        if (source.getEntity() == null)
            return false;
        if (source.getEntity() == getOwner())
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

    public PlayState walkPredicate(AnimationState<? extends FamiliarEntity> event) {
        return PlayState.CONTINUE;
    }

    public AnimationController<?> controller;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        controller = new AnimationController<>(this, "walkController", 1, this::walkPredicate);
        data.add(controller);
    }


    public boolean canTeleport() {
        return getOwner() != null && getOwner().onGround();
    }

    public @Nullable LivingEntity getOwner() {
        if (level.isClientSide || getOwnerID() == null)
            return null;

        return (LivingEntity) ((ServerLevel) level).getEntity(getOwnerID());
    }

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UUID, Optional.empty());
        pBuilder.define(COLOR, "");
        pBuilder.define(COSMETIC, ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getHolderID() {
        return holderID;
    }

    @Override
    public void setHolderID(ResourceLocation id) {
        this.holderID = id;
    }

    public @Nullable UUID getOwnerID() {
        return this.getEntityData().get(OWNER_UUID).orElse(null);
    }

    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.of(uuid));
    }

    public @NotNull ItemStack getCosmeticItem() {
        return this.entityData.get(COSMETIC);
    }

    //use this for tag reload
    public void setCosmeticItem(ItemStack stack, boolean shouldDrop) {
        if (!this.entityData.get(COSMETIC).isEmpty() && shouldDrop)
            this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.entityData.get(COSMETIC)));
        this.entityData.set(COSMETIC, stack);
        this.persistentData = persistentData.setCosmetic(stack);
        syncTag();
    }

    public void setCosmeticItem(ItemStack stack) {
        setCosmeticItem(stack, true);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d).add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.value().getDefaultValue())
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    public boolean canTrample(@NotNull BlockState state, @NotNull BlockPos pos, float fallDistance) {
        return false;
    }

    @Override
    protected boolean canRide(@NotNull Entity p_184228_1_) {
        return false;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (!level().isClientSide && getOwner() != null && getOwner().equals(caster)) {
            this.remove(RemovalReason.DISCARDED);
            ParticleUtil.spawnPoof((ServerLevel) level(), blockPosition());
            return true;
        }
        return false;
    }

    @Override
    public void setEntityOnShoulder(ServerPlayer pPlayer) {
        super.setEntityOnShoulder(pPlayer);
        if (!level.isClientSide) {
            FAMILIAR_SET.remove(this);
            FAMILIAR_SHOULDER_SET.add(this);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getOwnerID() != null)
            tag.putUUID("ownerID", getOwnerID());
        tag.putBoolean("terminated", terminatedFamiliar);
        tag.put("familiarData", getPersistentFamiliarData().toTag(level));
        if (holderID != null) {
            tag.putString("holderID", holderID.toString());
        }
        tag.putString("color", this.entityData.get(COLOR));
        if (!this.entityData.get(COSMETIC).isEmpty()) {
            Tag cosmeticTag = this.entityData.get(COSMETIC).save(level.registryAccess());
            tag.put("cosmetic", cosmeticTag);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("ownerID"))
            setOwnerID(tag.getUUID("ownerID"));
        terminatedFamiliar = tag.getBoolean("terminated");
        this.holderID = ResourceLocation.tryParse(tag.getString("holderID"));
        this.persistentData = deserializePersistentData(tag.getCompound("familiarData"));
        this.entityData.set(COLOR, tag.getString("color"));
        this.entityData.set(COSMETIC, ItemStack.parseOptional(this.level.registryAccess(), tag.getCompound("cosmetic")));
        syncAfterPersistentFamiliarInit();
    }


    @Override
    public void onFamiliarSpawned(FamiliarSummonEvent event) {
        if (level.isClientSide)
            return;
        IFamiliar.super.onFamiliarSpawned(event);
        if (!event.getEntity().equals(this) && event.owner.equals(this.getOwner()))
            this.terminatedFamiliar = true;
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(DyeColor color) {
        setColor(color.getName());
    }

    public void setColor(String color) {
        this.entityData.set(COLOR, color);
        persistentData = persistentData.setColor(color);
        syncTag();
    }

    /**
     * Called after the Familiar is returned and summoned from AbstractFamiliarHolder.
     *
     * @param tag The persistent data tag stored on the player.
     */
    public void setTagData(@Nullable CompoundTag tag) {
        this.persistentData = deserializePersistentData(tag != null && tag.contains("familiarData") ? tag.getCompound("familiarData") : new CompoundTag());
        syncAfterPersistentFamiliarInit();
    }

    /**
     * Sync the data you want to store from the familiar on the player cap here.
     * Get the owner from getOwner
     */
    public void syncTag() {
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(getOwner());
        if (cap != null && persistentData != null) {
            cap.getFamiliarData(getHolderID()).entityTag.put("familiarData", persistentData.toTag(level));
        }
    }

    /**
     * Override and return your own implementation of PersistentData. See FamiliarStarbuncle for an example.
     */
    public PersistentFamiliarData deserializePersistentData(CompoundTag tag) {
        return PersistentFamiliarData.fromTag(tag);
    }

    public PersistentFamiliarData getPersistentFamiliarData() {
        return persistentData;
    }

    /**
     * Called once the Persistent Familiar Data has been constructed.
     * Use this to de-duplify your persistent entity data as it relates to your PersistentFamiliarData.
     */
    public void syncAfterPersistentFamiliarInit() {
        setCustomName(persistentData.name());
        if (persistentData.color() != null) {
            setColor(persistentData.color());
        }
        if (persistentData.cosmetic() != null) {
            setCosmeticItem(persistentData.cosmetic(), false);
        }
    }

    /**
     * Use this to return custom texture, return null for default model.
     */
    public abstract @Nullable ResourceLocation getTexture();

}