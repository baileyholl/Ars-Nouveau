package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class SummonHorse extends Horse implements ISummon, IDispellable {
    public int ticksLeft;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(SummonHorse.class, EntityDataSerializers.OPTIONAL_UUID);

    public SummonHorse(EntityType<? extends Horse> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public float getManaReserve() {
        return 100;
    }

    @Override
    protected boolean canParent() {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player p_230254_1_, @NotNull InteractionHand p_230254_2_) {
        return super.mobInteract(p_230254_1_, p_230254_2_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            ticksLeft--;
            if (ticksLeft <= 0) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, true);
            }
        }
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.ticksLeft = 0;
        return true;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return ISummon.super.getOwner();
    }

    @Override
    public @NotNull Level level() {
        return super.level();
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        onSummonDeath(level, cause, false);
    }

    @Override
    public boolean canTakeItem(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    protected void dropEquipment() {
    }

    @Override
    public int getBaseExperienceReward() {
        return 0;
    }

    public SimpleContainer getHorseInventory() {
        return this.inventory;
    }

    @Override
    public void openCustomInventoryScreen(@NotNull Player playerEntity) {
    }

    @Override
    public boolean canMate(@NotNull Animal otherAnimal) {
        return false;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.ticksLeft = compound.getInt("left");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("left", ticksLeft);
        // writeOwner(compound); already handled in AbstractHorse#addAdditionalSaveData
    }

    @Override
    public int getTicksLeft() {
        return ticksLeft;
    }

    @Override
    public void setTicksLeft(int ticks) {
        this.ticksLeft = ticks;
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.getEntityData().get(OWNER_UUID).isEmpty() ? this.getUUID() : this.getEntityData().get(OWNER_UUID).get();
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setHorseStatModifiers(SpellStats spellStats) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(generateMaxHealth(this.random::nextInt) + spellStats.getAmpMultiplier() * 4);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateSpeed(() -> Math.min(1, 0.35   + spellStats.getAmpMultiplier() / 10.0)));
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(() -> Math.min(1, 0.35 + spellStats.getAmpMultiplier() / 10.0)));
        this.getAttribute(Attributes.SCALE).setBaseValue(1.0 + spellStats.getAoeMultiplier() * 0.1);
    }

}
