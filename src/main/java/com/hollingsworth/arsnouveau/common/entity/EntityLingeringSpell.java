package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntityLingeringSpell extends EntityProjectileSpell{

    public static final DataParameter<Integer> ACCELERATES = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.INT);
    public static final DataParameter<Integer> AOE = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.INT);
    public static final DataParameter<Boolean> LANDED = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> SENSITIVE = EntityDataManager.defineId(EntityLingeringSpell.class, DataSerializers.BOOLEAN);
    public double extendedTime;

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

        boolean isOnGround = level.getBlockState(blockPosition()).getMaterial().blocksMotion();
        if(!level.isClientSide) {
            this.setLanded(isOnGround);
            if(spellResolver == null) {
                this.remove();
                return;
            }
        }
        int aoe =  getAoe();
        if(!level.isClientSide && age % (20 - 2* getAccelerates()) == 0){
            if(isSensitive()){
                for(BlockPos p : BlockPos.betweenClosed(blockPosition().east(aoe).north(aoe), blockPosition().west(aoe).south(aoe))){
                    spellResolver.onResolveEffect(level, getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null, new
                            BlockRayTraceResult(new Vector3d(p.getX(), p.getY(), p.getZ()), Direction.UP, p, false));
                }
            }else {
                for(Entity entity : level.getEntities(null, new AxisAlignedBB(this.blockPosition()).inflate(getAoe()))) {
                    if(entity.equals(this) || entity instanceof EntityLingeringSpell || entity instanceof LightningBoltEntity)
                        continue;
                    spellResolver.onResolveEffect(level, getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null, new EntityRayTraceResult(entity));
                }
            }
        }

        if(!isOnGround){
            this.setDeltaMovement(0, -0.2, 0);
            super.tick();
        }else{
            age++;
        }
        if(age > 70 + extendedTime * 20)
            this.remove();
        if(level.isClientSide){
            ParticleUtil.spawnRitualAreaEffect(getOnPos(), level, random, getParticleColor(), getAoe(), 5, 20);
            ParticleUtil.spawnLight(level, getParticleColor(), position.add(0, 0.5, 0),10);
        }


    }

    public EntityLingeringSpell(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.LINGER_SPELL, world);
    }
    @Override
    public EntityType<?> getType() {
        return ModEntities.LINGER_SPELL;
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
        return (this.isSensitive() ? 1 : 3) + entityData.get(AOE) ;
    }

    public void setLanded(boolean landed){
        entityData.set(LANDED, landed);
    }

    public boolean getLanded(){
        return entityData.get(LANDED);
    }

    public void setSensitive(boolean sensitive){
        entityData.set(SENSITIVE, sensitive);
    }

    public boolean isSensitive(){
        return entityData.get(SENSITIVE);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACCELERATES, 0);
        entityData.define(AOE, 0);
        entityData.define(LANDED, false);
        entityData.define(SENSITIVE, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sensitive", isSensitive());
    }

    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
        setSensitive(compound.getBoolean("sensitive"));
    }
}
