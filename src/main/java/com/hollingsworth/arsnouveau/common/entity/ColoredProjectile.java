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
    public static final DataParameter<Integer> RED = EntityDataManager.createKey(ColoredProjectile.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> GREEN = EntityDataManager.createKey(ColoredProjectile.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> BLUE = EntityDataManager.createKey(ColoredProjectile.class, DataSerializers.VARINT);

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
        return new ParticleColor(dataManager.get(RED), dataManager.get(GREEN), dataManager.get(BLUE));
    }

    public ParticleColor.IntWrapper getParticleColorWrapper(){
        return new ParticleColor.IntWrapper(dataManager.get(RED), dataManager.get(GREEN), dataManager.get(BLUE));
    }

    public void setColor(ParticleColor.IntWrapper colors){
        dataManager.set(RED, colors.r);
        dataManager.set(GREEN, colors.g);
        dataManager.set(BLUE, colors.b);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        dataManager.set(RED, compound.getInt("red"));
        dataManager.set(GREEN, compound.getInt("green"));
        dataManager.set(BLUE, compound.getInt("blue"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("red", dataManager.get(RED));
        compound.putInt("green", dataManager.get(GREEN));
        compound.putInt("blue", dataManager.get(BLUE));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(RED, 255);
        this.dataManager.register(GREEN, 25);
        this.dataManager.register(BLUE, 180);
    }
}
