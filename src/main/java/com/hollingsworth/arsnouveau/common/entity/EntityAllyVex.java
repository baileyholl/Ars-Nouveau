package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.common.entity.goal.FollowSummonerFlyingGoal;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class EntityAllyVex extends Vex implements IFollowingSummon, ISummon, IDispellable {

    // EntityReference<LivingEntity> tracks our summoner — separate from Vex's own Mob-typed owner field.
    @Nullable
    private EntityReference<LivingEntity> ownerReference;

    private boolean limitedLifespan;
    private int limitedLifeTicks;

    public EntityAllyVex(EntityType<? extends Vex> type, Level level) {
        super(type, level);
    }

    public EntityAllyVex(Level level, LivingEntity owner) {
        super(ModEntities.ALLY_VEX.get(), level);
        this.limitedLifespan = false;
        this.ownerReference = EntityReference.of(owner);
        this.moveControl = new EntityAllyVex.MoveHelperController(this);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALLY_VEX.get();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, EntitySpawnReason pSpawnType, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnGroupData) {
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn, getRandom(), difficultyIn);
        return super.finalizeSpawn(worldIn, difficultyIn, pSpawnType, pSpawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource source, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityAllyVex.ChargeAttackGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        LivingEntity ownerEntity = getOwner();
        if (ownerEntity != null) {
            this.goalSelector.addGoal(2, new FollowSummonerFlyingGoal(this, ownerEntity, 1.0, 6.0f, 12.0f));
        }
        this.targetSelector.addGoal(1, new CopyOwnerTargetGoal<>(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Mob>(this, Mob.class, 10, false, true,
                (entity, serverLevel) -> (entity instanceof Mob mob && mob.getTarget() != null &&
                        mob.getTarget().equals(this.getOwner())) || (entity != null && entity.getKillCredit() != null && entity.getKillCredit().equals(this.getOwner()))
        ));
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        // setCanPassDoors removed in 1.21.11
        return flyingpathnavigator;
    }

    // ISummon / OwnableEntity implementation — ownerReference is EntityReference<LivingEntity>
    @Override
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return ownerReference;
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.ownerReference = EntityReference.of(uuid);
    }

    @Override
    public Level getWorld() {
        return this.level();
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
        return this.getOwner();
    }

    @Override
    public LivingEntity getOwnerAlt() {
        return getOwner();
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.limitedLifeTicks = 0;
        return true;
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityAllyVex.this.getTarget() != null && !EntityAllyVex.this.getMoveControl().hasWanted() && EntityAllyVex.this.random.nextInt(7) == 0) {
                return EntityAllyVex.this.distanceToSqr(EntityAllyVex.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return EntityAllyVex.this.getMoveControl().hasWanted() && EntityAllyVex.this.isCharging() && EntityAllyVex.this.getTarget() != null && EntityAllyVex.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = EntityAllyVex.this.getTarget();
            Vec3 vec3d = livingentity.getEyePosition(1.0F);
            EntityAllyVex.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            EntityAllyVex.this.setIsCharging(true);
            EntityAllyVex.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
        }

        public void stop() {
            EntityAllyVex.this.setIsCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = EntityAllyVex.this.getTarget();
            if (EntityAllyVex.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                EntityAllyVex.this.doHurtTarget((ServerLevel) EntityAllyVex.this.level(), livingentity);
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
    protected int getBaseExperienceReward(ServerLevel level) {
        return 0;
    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        if (compound.keySet().contains("LifeTicks")) {
            this.setLimitedLife(compound.getIntOr("LifeTicks", 0));
        }
        // Read owner UUID and build EntityReference from it
        compound.read("OwnerUUID", net.minecraft.core.UUIDUtil.CODEC)
            .ifPresent(uuid -> this.ownerReference = EntityReference.of(uuid));
    }

    @Override
    public PlayerTeam getTeam() {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            return owner.getTeam();
        }
        return super.getTeam();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
        if (this.ownerReference != null) {
            compound.store("OwnerUUID", net.minecraft.core.UUIDUtil.CODEC, this.ownerReference.getUUID());
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        onSummonDeath(level(), cause, false);
    }

    @Override
    public int getTicksLeft() {
        return limitedLifeTicks;
    }

    @Override
    public void setTicksLeft(int ticks) {
        this.limitedLifeTicks = ticks;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel p_level, DamageSource damageSource) {
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
    }

    @Override
    protected void dropExperience(ServerLevel level, @org.jetbrains.annotations.Nullable Entity entity) {
    }

    @Override
    protected void dropFromLootTable(ServerLevel level, DamageSource damageSource, boolean attackedRecently) {
    }

    @Override
    public void dropPreservedEquipment(ServerLevel level) {
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
