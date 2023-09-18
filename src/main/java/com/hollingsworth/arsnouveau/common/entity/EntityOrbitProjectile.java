package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class EntityOrbitProjectile extends EntityProjectileSpell {
    public Entity wardedEntity;
    public int ticksLeft;
    public static final EntityDataAccessor<Integer> OWNER_UUID = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> OFFSET = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ACCELERATES = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> AOE = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> TOTAL = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.INT);
    public int extendTimes;

    public EntityOrbitProjectile(Level worldIn, double x, double y, double z) {
        super(ModEntities.ORBIT_SPELL.get(), worldIn, x, y, z);
    }

    public EntityOrbitProjectile(Level worldIn, LivingEntity shooter) {
        super(ModEntities.ORBIT_SPELL.get(), worldIn, shooter);
    }

    public EntityOrbitProjectile(Level world, SpellResolver resolver) {
        super(ModEntities.ORBIT_SPELL.get(), world, resolver);
    }

    public EntityOrbitProjectile(EntityType<EntityOrbitProjectile> entityWardProjectileEntityType, Level world) {
        super(entityWardProjectileEntityType, world);
    }


    public void setOffset(int offset) {
        entityData.set(OFFSET, offset);
    }

    public int getOffset() {
        int val = 15;
        return (entityData.get(OFFSET)) * val;
    }

    public void setTotal(int total) {
        entityData.set(TOTAL, total);
    }

    public int getTotal() {
        return entityData.get(TOTAL) > 0 ? entityData.get(TOTAL) : 1;
    }

    public void setAccelerates(int accelerates) {
        entityData.set(ACCELERATES, accelerates);
    }

    public int getAccelerates() {
        return entityData.get(ACCELERATES);
    }

    public void setAoe(float aoe) {
        entityData.set(AOE, aoe);
    }

    public float getAoe() {
        return entityData.get(AOE);
    }

    public double getRotateSpeed() {
        return 10.0 - getAccelerates();
    }

    public double getRadiusMultiplier() {
        return 1.5 + 0.5 * getAoe();
    }

    @Override
    public void tick() {
        Entity owner = level.getEntity(getOwnerID());
        if (!level.isClientSide && owner == null) {
            this.remove(RemovalReason.DISCARDED);
            return;
        } else if (owner == null) {
            return;
        }
        super.tick();
    }

    @Override
    public Vec3 getNextHitPosition() {
        return getAngledPosition(tickCount + 3); // trace 3 ticks ahead for hit
    }

    @Override
    public void tickNextPosition() {
        this.setPos(getAngledPosition(tickCount));
    }

    public Vec3 getAngledPosition(int nextTick) {
        double rotateSpeed = getRotateSpeed();
        double radiusMultiplier = getRadiusMultiplier();
        Entity owner = level.getEntity(getOwnerID());
        return new Vec3(
                owner.getX() - radiusMultiplier * Math.sin(nextTick / rotateSpeed + getOffset()),
                owner.getY() + 1 - (owner.isShiftKeyDown() ? 0.25 : 0),
                owner.getZ() - radiusMultiplier * Math.cos(nextTick / rotateSpeed + getOffset()));
    }

    @Override
    public boolean canTraversePortals() {
        return false;
    }

    @Override
    public int getExpirationTime() {
        return 60 * 20 + 30 * 20 * extendTimes;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (level.isClientSide || result.getType() == HitResult.Type.MISS)
            return;

        if (result.getType() == HitResult.Type.ENTITY) {
            if (((EntityHitResult) result).getEntity().equals(this.getOwner())) return;
            if (this.spellResolver != null) {
                this.spellResolver.onResolveEffect(level, result);
                Networking.sendToNearby(level, BlockPos.containing(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        BlockPos.containing(result.getLocation()), getParticleColor()));
                attemptRemoval();
            }
        } else if (numSensitive > 0 && result instanceof BlockHitResult blockraytraceresult && !this.isRemoved()) {
            if (this.spellResolver != null) {
                this.spellResolver.onResolveEffect(this.level, blockraytraceresult);
            }
            Networking.sendToNearby(level, ((BlockHitResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                    BlockPos.containing(result.getLocation()).below(), getParticleColor()));
            attemptRemoval();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, 0);
        this.entityData.define(OFFSET, 0);
        this.entityData.define(ACCELERATES, 0);
        this.entityData.define(AOE, 0f);
        this.entityData.define(TOTAL, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        tag.putInt("offset", getOffset());
        tag.putFloat("aoe", getAoe());
        tag.putInt("accelerate", getAccelerates());
        tag.putInt("total", getTotal());
        tag.putInt("ownerID", getOwnerID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ticksLeft = tag.getInt("left");
        setOffset(tag.getInt("offset"));
        setAoe(tag.getFloat("aoe"));
        setAccelerates(tag.getInt("accelerate"));
        setOwnerID(tag.getInt("ownerID"));
        setTotal(tag.getInt("total"));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ORBIT_SPELL.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityOrbitProjectile(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.ORBIT_SPELL.get(), world);
    }

    public int getOwnerID() {
        return this.getEntityData().get(OWNER_UUID);
    }

    public void setOwnerID(int uuid) {
        this.getEntityData().set(OWNER_UUID, uuid);
    }
}
