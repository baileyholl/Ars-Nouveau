package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityLingeringSpell extends EntityProjectileSpell{

    public static final DataParameter<Integer> ACCELERATES = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.INT);
    public static final DataParameter<Integer> AOE = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.INT);
    public static final DataParameter<Boolean> LANDED = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.BOOLEAN);

    public EntityLingeringSpell(EntityType<? extends EntityProjectileSpell> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityLingeringSpell(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityLingeringSpell(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    public void setAccelerates(int accelerates){
        entityData.set(ACCELERATES, accelerates);
    }


    @Override
    public void tick() {
        if(!getLanded()){
            super.tick();
        }

        if(level.isClientSide && getLanded()){
            ParticleUtil.spawnRitualAreaEffect(getOnPos(), level, random, getParticleColor(), 3 + getAoe());
        }


    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!level.isClientSide && result instanceof BlockRayTraceResult && !this.removed) {
            BlockState state = level.getBlockState(((BlockRayTraceResult) result).getBlockPos());
            if(state.getMaterial() == Material.PORTAL){
                state.getBlock().entityInside(state, level, ((BlockRayTraceResult) result).getBlockPos(),this);
                return;
            }
            this.setLanded(true);
        }
    }

    public int getAccelerates(){
        return entityData.get(ACCELERATES);
    }

    public void setAoe(int aoe){
        entityData.set(AOE, aoe);
    }

    public int getAoe(){
        return entityData.get(AOE);
    }

    public void setLanded(boolean landed){
        entityData.set(LANDED, landed);
    }

    public boolean getLanded(){
        return entityData.get(LANDED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACCELERATES, 0);
        entityData.define(AOE, 0);
        entityData.define(LANDED, false);
    }
}
