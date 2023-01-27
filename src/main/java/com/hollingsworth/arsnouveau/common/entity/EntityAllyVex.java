package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.common.entity.goal.FollowSummonerFlyingGoal;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class EntityAllyVex extends Vex implements IFollowingSummon, ISummon {
    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean limitedLifespan;
    private int limitedLifeTicks;

    public EntityAllyVex(EntityType<? extends Vex> p_i50190_1_, Level p_i50190_2_) {
        super(ModEntities.ALLY_VEX.get(), p_i50190_2_);
    }


    public EntityAllyVex(Level p_i50190_2_, LivingEntity owner) {
        super(EntityType.VEX, p_i50190_2_);
        this.owner = owner;
        this.limitedLifespan = false;
        setOwnerID(owner.getUUID());
        this.moveControl = new EntityAllyVex.MoveHelperController(this);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALLY_VEX.get();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(difficultyIn);
        this.populateDefaultEquipmentEnchantments(getRandom(), difficultyIn);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityAllyVex.ChargeAttackGoal());

        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(2, new FollowSummonerFlyingGoal(this, this.owner, 1.0, 6.0f, 3.0f));
        this.targetSelector.addGoal(1, new CopyOwnerTargetGoal<>(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, true,
                (entity) -> (entity instanceof Mob && ((Mob) entity).getTarget() != null &&
                        ((Mob) entity).getTarget().equals(this.owner)) || (entity instanceof LivingEntity && entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner))
        ));
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
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

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean canUse() {
            if (EntityAllyVex.this.getTarget() != null && !EntityAllyVex.this.getMoveControl().hasWanted() && EntityAllyVex.this.random.nextInt(7) == 0) {
                return EntityAllyVex.this.distanceToSqr(EntityAllyVex.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return EntityAllyVex.this.getMoveControl().hasWanted() && EntityAllyVex.this.isCharging() && EntityAllyVex.this.getTarget() != null && EntityAllyVex.this.getTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            LivingEntity livingentity = EntityAllyVex.this.getTarget();
            Vec3 vec3d = livingentity.getEyePosition(1.0F);
            EntityAllyVex.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            EntityAllyVex.this.setIsCharging(true);
            EntityAllyVex.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            EntityAllyVex.this.setIsCharging(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = EntityAllyVex.this.getTarget();
            if (EntityAllyVex.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                EntityAllyVex.this.doHurtTarget(livingentity);
                EntityAllyVex.this.setIsCharging(false);
            } else {
                double d0 = EntityAllyVex.this.distanceToSqr(livingentity);
                if (d0 < 9.0D) {
                    Vec3 vec3d = livingentity.getEyePosition(1.0F);
                    EntityAllyVex.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }


    @Override
    public int getExperienceReward() {
        return 0;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag compound) {
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

    }

    public LivingEntity getOwnerFromID() {
        try {
            UUID uuid = this.getOwnerID();

            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.boundOrigin != null) {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
        if (this.getOwnerID() == null) {
            compound.putUUID("OwnerUUID", Util.NIL_UUID);
        } else {
            compound.putUUID("OwnerUUID", this.getOwnerID());
        }

    }


    @Override
    public void die(DamageSource cause) {
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
    public UUID getOwnerID() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    class MoveHelperController extends MoveControl {
        public MoveHelperController(Vex vex) {
            super(vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vec3d = new Vec3(this.wantedX - EntityAllyVex.this.getX(), this.wantedY - EntityAllyVex.this.getY(), this.wantedZ - EntityAllyVex.this.getZ());
                double d0 = vec3d.length();
                if (d0 < EntityAllyVex.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    EntityAllyVex.this.setDeltaMovement(EntityAllyVex.this.getDeltaMovement().scale(0.5D));
                } else {
                    EntityAllyVex.this.setDeltaMovement(EntityAllyVex.this.getDeltaMovement().add(vec3d.scale(this.speedModifier * 0.05D / d0)));
                    if (EntityAllyVex.this.getTarget() == null) {
                        Vec3 vec3d1 = EntityAllyVex.this.getDeltaMovement();
                        EntityAllyVex.this.yRot = -((float) Mth.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI);
                        EntityAllyVex.this.yBodyRot = EntityAllyVex.this.yRot;
                    } else {
                        double d2 = EntityAllyVex.this.getTarget().getX() - EntityAllyVex.this.getX();
                        double d1 = EntityAllyVex.this.getTarget().getZ() - EntityAllyVex.this.getZ();
                        EntityAllyVex.this.yRot = -((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI);
                        EntityAllyVex.this.yBodyRot = EntityAllyVex.this.yRot;
                    }
                }

            }
        }
    }

}
