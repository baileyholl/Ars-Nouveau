package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class ColoredProjectile extends Projectile {
    public static final EntityDataAccessor<Integer> RED = SynchedEntityData.defineId(ColoredProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GREEN = SynchedEntityData.defineId(ColoredProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE = SynchedEntityData.defineId(ColoredProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<CompoundTag> PARTICLE_TAG = SynchedEntityData.defineId(ColoredProjectile.class, EntityDataSerializers.COMPOUND_TAG);
    public int rainbowStartTick = 0;
    private ParticleColor color;

    public ColoredProjectile(EntityType<? extends ColoredProjectile> type, Level worldIn) {
        super(type, worldIn);
        rainbowStartTick = level.random.nextInt(1536);
    }

    public ColoredProjectile(EntityType<? extends ColoredProjectile> type, Level worldIn, double x, double y, double z) {
        this(type, worldIn);
        setPos(x, y, z);
    }

    public ColoredProjectile(EntityType<? extends ColoredProjectile> type, Level worldIn, LivingEntity shooter) {
        this(type, worldIn);
        setOwner(shooter);
    }

    public ParticleColor getColor(){
        if (this.color == null) {
            this.color = ParticleColorRegistry.from(entityData.get(PARTICLE_TAG));
        }
        return this.color;
    }

    public ParticleColor getParticleColor() {
        return getColor().transition(tickCount*50);
    }

    @Deprecated(forRemoval = true)
    public boolean isRainbow() {
        return getParticleColor() instanceof RainbowParticleColor;
    }

    @Deprecated(forRemoval = true)
    public ParticleColor.IntWrapper getParticleColorWrapper() {
        return new ParticleColor.IntWrapper(entityData.get(RED), entityData.get(GREEN), entityData.get(BLUE));
    }

    public void setColor(ParticleColor colors) {
        ParticleColor.IntWrapper wrapper = colors.toWrapper();
        entityData.set(RED, wrapper.r);
        entityData.set(GREEN, wrapper.g);
        entityData.set(BLUE, wrapper.b);
        entityData.set(PARTICLE_TAG, colors.serialize());
        this.color = colors;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.color = null;
        entityData.set(RED, compound.getInt("red"));
        entityData.set(GREEN, compound.getInt("green"));
        entityData.set(BLUE, compound.getInt("blue"));
        entityData.set(PARTICLE_TAG, compound.getCompound("particle"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("red", entityData.get(RED));
        compound.putInt("green", entityData.get(GREEN));
        compound.putInt("blue", entityData.get(BLUE));
        compound.put("particle", entityData.get(PARTICLE_TAG));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(RED, 255);
        pBuilder.define(GREEN, 25);
        pBuilder.define(BLUE, 180);
        pBuilder.define(PARTICLE_TAG, new ParticleColor(255, 25, 180).serialize());
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level, this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

}
