package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.IFollowingSummon;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.common.entity.goal.FollowSummonerFlyingGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class EntityAllyVex extends VexEntity implements IFollowingSummon, ISummon {
    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean limitedLifespan;
    private int limitedLifeTicks;

    public EntityAllyVex(EntityType<? extends VexEntity> p_i50190_1_, World p_i50190_2_) {
        super(ModEntities.ALLY_VEX, p_i50190_2_);
    }


    public EntityAllyVex(World p_i50190_2_, LivingEntity owner) {
        super(EntityType.VEX, p_i50190_2_);
        this.owner = owner;
        this.limitedLifespan = false;
        setOwnerId(owner.getUUID());
        this.moveControl = new EntityAllyVex.MoveHelperController(this);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALLY_VEX;
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.populateDefaultEquipmentSlots(difficultyIn);
        this.populateDefaultEquipmentEnchantments(difficultyIn);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new EntityAllyVex.ChargeAttackGoal());

        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.addGoal(2, new FollowSummonerFlyingGoal(this, this.owner, 1.0, 6.0f, 3.0f));
        this.targetSelector.addGoal(1, new EntityAllyVex.CopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 10, false, true,
                (entity ) -> (entity instanceof MobEntity && ((MobEntity) entity).getTarget() != null &&
                        ((MobEntity) entity).getTarget().equals(this.owner)) || (entity instanceof LivingEntity && entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner))
        ));
    }
    protected PathNavigator createNavigation(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
    }

    @Override
    public World getWorld() {
        return this.level;
    }

    @Override
    public PathNavigator getPathNav() {
        return this.navigation;
    }

    @Override
    public MobEntity getSelfEntity() {
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
            Vector3d vec3d = livingentity.getEyePosition(1.0F);
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
                    Vector3d vec3d = livingentity.getEyePosition(1.0F);
                    EntityAllyVex.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }


    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundNBT compound) {
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
            s = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), s1);
        }

        if (s != null) {
            try {
                this.setOwnerId(s);

            } catch (Throwable ignored) {
            }
        }

    }

    public LivingEntity getOwnerFromID(){
        try {
            UUID uuid = this.getOwnerId();

            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }


    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }


    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (this.boundOrigin != null) {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
        if (this.getOwnerId() == null) {
            compound.putUUID("OwnerUUID", Util.NIL_UUID);
        } else {
            compound.putUUID("OwnerUUID", this.getOwnerId());
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

    class MoveHelperController extends MovementController {
        public MoveHelperController(VexEntity vex) {
            super(vex);
        }

        public void tick() {
            if (this.operation == MovementController.Action.MOVE_TO) {
                Vector3d vec3d = new Vector3d(this.wantedX - EntityAllyVex.this.getX(), this.wantedY - EntityAllyVex.this.getY(), this.wantedZ - EntityAllyVex.this.getZ());
                double d0 = vec3d.length();
                if (d0 < EntityAllyVex.this.getBoundingBox().getSize()) {
                    this.operation = MovementController.Action.WAIT;
                    EntityAllyVex.this.setDeltaMovement(EntityAllyVex.this.getDeltaMovement().scale(0.5D));
                } else {
                    EntityAllyVex.this.setDeltaMovement(EntityAllyVex.this.getDeltaMovement().add(vec3d.scale(this.speedModifier * 0.05D / d0)));
                    if (EntityAllyVex.this.getTarget() == null) {
                        Vector3d vec3d1 = EntityAllyVex.this.getDeltaMovement();
                        EntityAllyVex.this.yRot = -((float) MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float)Math.PI);
                        EntityAllyVex.this.yBodyRot = EntityAllyVex.this.yRot;
                    } else {
                        double d2 = EntityAllyVex.this.getTarget().getX() - EntityAllyVex.this.getX();
                        double d1 = EntityAllyVex.this.getTarget().getZ() - EntityAllyVex.this.getZ();
                        EntityAllyVex.this.yRot = -((float)MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI);
                        EntityAllyVex.this.yBodyRot = EntityAllyVex.this.yRot;
                    }
                }

            }
        }
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final EntityPredicate copyOwnerTargeting = (new EntityPredicate()).allowUnseeable().ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(CreatureEntity creature) {
            super(creature, false);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean canUse() {
            return EntityAllyVex.this.owner != null && EntityAllyVex.this.owner.getLastHurtMob() != null;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            EntityAllyVex.this.setTarget(EntityAllyVex.this.owner.getLastHurtMob());
            super.start();
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean canUse() {
            return !EntityAllyVex.this.getMoveControl().hasWanted() && EntityAllyVex.this.random.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = EntityAllyVex.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = new BlockPos(EntityAllyVex.this.blockPosition());
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityAllyVex.this.random.nextInt(15) - 7, EntityAllyVex.this.random.nextInt(11) - 5, EntityAllyVex.this.random.nextInt(15) - 7);
                if (EntityAllyVex.this.level.isEmptyBlock(blockpos1)) {
                    EntityAllyVex.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (EntityAllyVex.this.getTarget() == null) {
                        EntityAllyVex.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
