package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.FollowSummonerGoal;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SummonSkeleton extends Skeleton implements IFollowingSummon, ISummon, IDispellable {
    public static EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(SummonSkeleton.class, EntityDataSerializers.OPTIONAL_UUID);

    private final RangedBowAttackGoal<SummonSkeleton> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);

    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.5D, true) {
        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            super.stop();
            SummonSkeleton.this.setAggressive(false);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            super.start();
            SummonSkeleton.this.setAggressive(true);
        }
    };

    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;
    private int limitedLifeTicks;

    public SummonSkeleton(EntityType<? extends Skeleton> entityType, Level level) {
        super(entityType, level);
    }

    public SummonSkeleton(Level level, LivingEntity owner, ItemStack item) {
        super(ModEntities.SUMMON_SKELETON.get(), level);
        this.setWeapon(item);
        this.owner = owner;
        setOwnerID(owner.getUUID());
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
    public float getManaReserve() {
        return 200;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.SUMMON_SKELETON.get();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pSpawnType, @Nullable SpawnGroupData pSpawnGroupData) {
        this.populateDefaultEquipmentSlots(getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel, getRandom(), pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance pDifficulty) {

    }

    @Override
    protected void dropAllDeathLoot(@NotNull ServerLevel p_348524_, @NotNull DamageSource p_21192_) {

    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel p_348477_, @NotNull DamageSource p_33574_, boolean p_33576_) {

    }

    @Override
    protected void dropEquipment() {
    }

    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;
        reloadGoalsAndTargeting(this.goalSelector, this.targetSelector);
        registerGoals();
        reassessWeaponGoal();
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(4, new FollowSummonerGoal(this, this.owner, 1.40, 4.0f - getCurrentBehavior().ordinal(), 12.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        if (getCurrentBehavior() != SummonBehavior.PASSIVE) {

            this.targetSelector.addGoal(2, new HurtByTargetGoal(this, SummonSkeleton.class) {
                @Override
                protected boolean canAttack(@Nullable LivingEntity pPotentialTarget, @NotNull TargetingConditions pTargetPredicate) {
                    return pPotentialTarget != null && super.canAttack(pPotentialTarget, pTargetPredicate) && !pPotentialTarget.getUUID().equals(getOwnerUUID());
                }
            });
            this.targetSelector.addGoal(1, new CopyOwnerTargetGoal<>(this));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, true,
                    (LivingEntity entity) ->
                            !this.isAlliedTo(entity) && (entity instanceof Mob mob && mob.getTarget() != null && mob.getTarget().equals(this.owner) || entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner))
            ));

            if (getCurrentBehavior() == SummonBehavior.AGGRESSIVE)
                this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false, e -> !this.isAlliedTo(e)));
        }
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public void setWeapon(ItemStack item) {
        this.setItemSlot(EquipmentSlot.MAINHAND, item);
        this.reassessWeaponGoal();
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.level instanceof ServerLevel && this.getItemInHand(InteractionHand.MAIN_HAND) != ItemStack.EMPTY) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
            if (itemstack.is(Items.BOW)) {
                this.bowGoal.setMinAttackInterval(20);
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.MOB_ATTACK) && pSource.getEntity() instanceof ISummon summon) {
            if (summon.getOwnerUUID() != null && summon.getOwnerUUID().equals(this.getOwnerUUID())) return false;
        }
        return super.hurt(pSource, pAmount);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide && limitedLifeTicks > -1) {
            limitedLifeTicks--;
            if (limitedLifeTicks <= 0) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, true);
            }
        }
    }

    @Override
    public PlayerTeam getTeam() {
        if (this.getSummoner() != null) return getSummoner().getTeam();
        return super.getTeam();
    }


    @Override
    public boolean isAlliedTo(@NotNull Entity pEntity) {
        LivingEntity summoner = this.getSummoner();

        if (summoner != null) {
            if (pEntity instanceof ISummon summon && summon.getOwnerUUID() != null && summon.getOwnerUUID().equals(this.getOwnerUUID()))
                return true;
            return pEntity == summoner || summoner.isAlliedTo(pEntity);
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public Level getWorld() {
        return this.level;
    }

    @Override
    public PathNavigation getPathNav() {
        return this.navigation;
    }

    @Override
    public Mob getSelfEntity() {
        return this;
    }

    public LivingEntity getSummoner() {
        return this.getOwnerFromID();
    }

    public LivingEntity getActualOwner() {
        return owner;
    }

    @Override
    public int getBaseExperienceReward() {
        return 0;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }

        if (compound.contains("LifeTicks")) {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }
        UUID s;
        if (compound.contains("OwnerUUID", 8)) {
            s = compound.getUUID("OwnerUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s1);
        }

        if (s != null) {
            try {
                this.setOwnerID(s);

            } catch (Throwable ignored) {
            }
        }

        setCurrentBehavior(SummonBehavior.fromId(compound.getInt("summonBehavior")));

    }

    public void setLimitedLife(int lifeTicks) {
        this.limitedLifeTicks = lifeTicks;
    }

    public LivingEntity getOwnerFromID() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UNIQUE_ID, Optional.of(Util.NIL_UUID));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.boundOrigin != null) {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        compound.putInt("LifeTicks", this.limitedLifeTicks);

        if (this.getOwnerUUID() == null) {
            compound.putUUID("OwnerUUID", Util.NIL_UUID);
        } else {
            compound.putUUID("OwnerUUID", this.getOwnerUUID());
        }

        compound.putInt("summonBehavior", this.getCurrentBehavior().ordinal());

    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        onSummonDeath(level, cause, false);
    }

    @Override
    public int getTicksLeft() {
        return limitedLifeTicks;
    }

    @Override
    public void setTicksLeft(int ticks) {
        this.limitedLifeTicks = ticks;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.limitedLifeTicks = 0;
        return true;
    }
}
