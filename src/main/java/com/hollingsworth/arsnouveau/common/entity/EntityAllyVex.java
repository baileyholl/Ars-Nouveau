package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.ISummon;
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

public class EntityAllyVex extends VexEntity implements ISummon {
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
        setOwnerId(owner.getUniqueID());
        this.moveController = new EntityAllyVex.MoveHelperController(this);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALLY_VEX;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setEquipmentBasedOnDifficulty(difficultyIn);
        this.setEnchantmentBasedOnDifficulty(difficultyIn);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
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
                (entity ) -> (entity instanceof MobEntity && ((MobEntity) entity).getAttackTarget() != null &&
                        ((MobEntity) entity).getAttackTarget().equals(this.owner)) || (entity instanceof LivingEntity && entity.getAttackingEntity() != null && entity.getAttackingEntity().equals(this.owner))
        ));
    }
    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(true);
        flyingpathnavigator.setCanEnterDoors(true);
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
        return this.world;
    }

    @Override
    public PathNavigator getPathNav() {
        return this.navigator;
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
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (EntityAllyVex.this.getAttackTarget() != null && !EntityAllyVex.this.getMoveHelper().isUpdating() && EntityAllyVex.this.rand.nextInt(7) == 0) {
                return EntityAllyVex.this.getDistanceSq(EntityAllyVex.this.getAttackTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EntityAllyVex.this.getMoveHelper().isUpdating() && EntityAllyVex.this.isCharging() && EntityAllyVex.this.getAttackTarget() != null && EntityAllyVex.this.getAttackTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            LivingEntity livingentity = EntityAllyVex.this.getAttackTarget();
            Vector3d vec3d = livingentity.getEyePosition(1.0F);
            EntityAllyVex.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            EntityAllyVex.this.setCharging(true);
            EntityAllyVex.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EntityAllyVex.this.setCharging(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = EntityAllyVex.this.getAttackTarget();
            if (EntityAllyVex.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                EntityAllyVex.this.attackEntityAsMob(livingentity);
                EntityAllyVex.this.setCharging(false);
            } else {
                double d0 = EntityAllyVex.this.getDistanceSq(livingentity);
                if (d0 < 9.0D) {
                    Vector3d vec3d = livingentity.getEyePosition(1.0F);
                    EntityAllyVex.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }


    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }

        if (compound.contains("LifeTicks")) {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }
        UUID s;
        if (compound.contains("OwnerUUID", 8)) {
            s = compound.getUniqueId("OwnerUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
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

            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }


    @Nullable
    public UUID getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }


    protected void registerData() {
        super.registerData();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.boundOrigin != null) {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
        if (this.getOwnerId() == null) {
            compound.putUniqueId("OwnerUUID", Util.DUMMY_UUID);
        } else {
            compound.putUniqueId("OwnerUUID", this.getOwnerId());
        }

    }


    class MoveHelperController extends MovementController {
        public MoveHelperController(VexEntity vex) {
            super(vex);
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vec3d = new Vector3d(this.posX - EntityAllyVex.this.getPosX(), this.posY - EntityAllyVex.this.getPosY(), this.posZ - EntityAllyVex.this.getPosZ());
                double d0 = vec3d.length();
                if (d0 < EntityAllyVex.this.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    EntityAllyVex.this.setMotion(EntityAllyVex.this.getMotion().scale(0.5D));
                } else {
                    EntityAllyVex.this.setMotion(EntityAllyVex.this.getMotion().add(vec3d.scale(this.speed * 0.05D / d0)));
                    if (EntityAllyVex.this.getAttackTarget() == null) {
                        Vector3d vec3d1 = EntityAllyVex.this.getMotion();
                        EntityAllyVex.this.rotationYaw = -((float) MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float)Math.PI);
                        EntityAllyVex.this.renderYawOffset = EntityAllyVex.this.rotationYaw;
                    } else {
                        double d2 = EntityAllyVex.this.getAttackTarget().getPosX() - EntityAllyVex.this.getPosX();
                        double d1 = EntityAllyVex.this.getAttackTarget().getPosZ() - EntityAllyVex.this.getPosZ();
                        EntityAllyVex.this.rotationYaw = -((float)MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI);
                        EntityAllyVex.this.renderYawOffset = EntityAllyVex.this.rotationYaw;
                    }
                }

            }
        }
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final EntityPredicate field_220803_b = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();

        public CopyOwnerTargetGoal(CreatureEntity creature) {
            super(creature, false);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            return EntityAllyVex.this.owner != null && EntityAllyVex.this.owner.getLastAttackedEntity() != null;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            EntityAllyVex.this.setAttackTarget(EntityAllyVex.this.owner.getLastAttackedEntity());
            super.startExecuting();
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            return !EntityAllyVex.this.getMoveHelper().isUpdating() && EntityAllyVex.this.rand.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = EntityAllyVex.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = new BlockPos(EntityAllyVex.this.getPosition());
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityAllyVex.this.rand.nextInt(15) - 7, EntityAllyVex.this.rand.nextInt(11) - 5, EntityAllyVex.this.rand.nextInt(15) - 7);
                if (EntityAllyVex.this.world.isAirBlock(blockpos1)) {
                    EntityAllyVex.this.moveController.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (EntityAllyVex.this.getAttackTarget() == null) {
                        EntityAllyVex.this.getLookController().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
