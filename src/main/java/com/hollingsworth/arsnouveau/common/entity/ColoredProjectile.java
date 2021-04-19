package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public abstract class ColoredProjectile extends ArrowEntity {
    public static final DataParameter<Integer> RED = EntityDataManager.defineId(ColoredProjectile.class, DataSerializers.INT);
    public static final DataParameter<Integer> GREEN = EntityDataManager.defineId(ColoredProjectile.class, DataSerializers.INT);
    public static final DataParameter<Integer> BLUE = EntityDataManager.defineId(ColoredProjectile.class, DataSerializers.INT);

    public ColoredProjectile(EntityType<? extends ArrowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public ColoredProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public ColoredProjectile(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
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
    public void load(CompoundNBT compound) {
        super.load(compound);
        entityData.set(RED, compound.getInt("red"));
        entityData.set(GREEN, compound.getInt("green"));
        entityData.set(BLUE, compound.getInt("blue"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("red", entityData.get(RED));
        compound.putInt("green", entityData.get(GREEN));
        compound.putInt("blue", entityData.get(BLUE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RED, 255);
        this.entityData.define(GREEN, 25);
        this.entityData.define(BLUE, 180);
    }
}
