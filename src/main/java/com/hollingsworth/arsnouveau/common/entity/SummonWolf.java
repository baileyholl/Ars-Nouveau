package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SummonWolf extends Wolf implements ISummon, IDispellable {
    public int ticksLeft;
    public boolean isWildenSummon;

    public SummonWolf(EntityType<? extends Wolf> type, Level worldIn) {
        super(type, worldIn);
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

    @Override
    public void tick() {
        super.tick();
        // Handle lifetime
        // Ticks left of -1 means infinite lifetime
        if (!level.isClientSide && ticksLeft > -1) {
            ticksLeft--;
            if (ticksLeft <= 0) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, true);
            }
        }
    }

    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;

        reloadGoalsAndTargeting(this.goalSelector, this.targetSelector);
        registerGoals();
    }

    @Override
    protected void registerGoals() {
        if (isWildenSummon) {
            super.registerGoals();
            return;
        }

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        if (getCurrentBehavior() != SummonBehavior.PASSIVE) {
            // Defensive and Aggressive both get these target goals
            this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
            this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());

            if (getCurrentBehavior() == SummonBehavior.AGGRESSIVE) {
                this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false, e -> !this.isAlliedTo(e)));
            }
        }
    }

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(SummonWolf.class, EntityDataSerializers.OPTIONAL_UUID);

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        onSummonDeath(level, cause, false);
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.ticksLeft = 0;
        return true;
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
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.ticksLeft = compound.getInt("left");
        this.isWildenSummon = compound.getBoolean("wildenSummon");
        setCurrentBehavior(SummonBehavior.fromId(compound.getInt("behavior")));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("left", ticksLeft);
        compound.putBoolean("wildenSummon", isWildenSummon);
        compound.putInt("behavior", getCurrentBehavior().ordinal());
    }

    @Override
    public int getBaseExperienceReward() {
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
    public float getManaReserve() {
        return hasArmor() ? 100f : 50f;
    }
}
