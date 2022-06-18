package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
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

    public ColoredProjectile(EntityType<? extends ColoredProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    public ColoredProjectile(EntityType<? extends ColoredProjectile> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn);
        setPos(x, y, z);
    }

    public ColoredProjectile(EntityType<? extends ColoredProjectile> type, Level worldIn, LivingEntity shooter) {
        super(type, worldIn);
        setOwner(shooter);
    }

    public ParticleColor getParticleColor(){
        return new ParticleColor(entityData.get(RED), entityData.get(GREEN), entityData.get(BLUE));
    }

    public ParticleColor.IntWrapper getParticleColorWrapper(){
        return new ParticleColor.IntWrapper(entityData.get(RED), entityData.get(GREEN), entityData.get(BLUE));
    }

    public void setColor(ParticleColor.IntWrapper colors){
        entityData.set(RED, colors.r);
        entityData.set(GREEN, colors.g);
        entityData.set(BLUE, colors.b);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        entityData.set(RED, compound.getInt("red"));
        entityData.set(GREEN, compound.getInt("green"));
        entityData.set(BLUE, compound.getInt("blue"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("red", entityData.get(RED));
        compound.putInt("green", entityData.get(GREEN));
        compound.putInt("blue", entityData.get(BLUE));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RED, 255);
        this.entityData.define(GREEN, 25);
        this.entityData.define(BLUE, 180);
    }


    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level, this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

}
