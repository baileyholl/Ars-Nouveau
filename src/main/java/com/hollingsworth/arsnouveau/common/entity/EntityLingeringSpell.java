package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.PlayMessages;

public class EntityLingeringSpell extends EntityProjectileSpell{

    public static final EntityDataAccessor<Integer> ACCELERATES = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> AOE = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> LANDED = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SENSITIVE = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public double extendedTime;
    public int maxProcs = 100;
    public int totalProcs;

    public EntityLingeringSpell(EntityType<? extends EntityProjectileSpell> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityLingeringSpell(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityLingeringSpell(Level worldIn, LivingEntity shooter) {
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
                this.remove(RemovalReason.DISCARDED);
                return;
            }
        }
        int aoe =  getAoe();
        if(!level.isClientSide && age % (20 - 2* getAccelerates()) == 0){
            if(isSensitive()){
                for(BlockPos p : BlockPos.betweenClosed(blockPosition().east(aoe).north(aoe), blockPosition().west(aoe).south(aoe))){
                    spellResolver.onResolveEffect(level, getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null, new
                            BlockHitResult(new Vec3(p.getX(), p.getY(), p.getZ()), Direction.UP, p, false));
                }
            }else {
                int i = 0;
                for(Entity entity : level.getEntities(null, new AABB(this.blockPosition()).inflate(getAoe()))) {
                    if(entity.equals(this) || entity instanceof EntityLingeringSpell || entity instanceof LightningBolt)
                        continue;
                    spellResolver.onResolveEffect(level, getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null, new EntityHitResult(entity));
                    i++;
                    if(i > 5)
                        break;
                }
                totalProcs += i;
                if(totalProcs>= maxProcs)
                    this.remove(RemovalReason.DISCARDED);
            }
        }

        if(!isOnGround){
            this.setDeltaMovement(0, -0.2, 0);
            super.tick();
        }else{
            age++;
        }
        if(age > 70 + extendedTime * 20)
            this.remove(RemovalReason.DISCARDED);
        if(level.isClientSide){
            ParticleUtil.spawnRitualAreaEffect(getOnPos(), level, random, getParticleColor(), getAoe(), 5, 20);
            ParticleUtil.spawnLight(level, getParticleColor(), position.add(0, 0.5, 0),10);
        }


    }

    public EntityLingeringSpell(PlayMessages.SpawnEntity packet, Level world){
        super(ModEntities.LINGER_SPELL, world);
    }
    @Override
    public EntityType<?> getType() {
        return ModEntities.LINGER_SPELL;
    }
    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide && result instanceof BlockHitResult && !this.isRemoved()) {
            BlockState state = level.getBlockState(((BlockHitResult) result).getBlockPos());
            if(state.getMaterial() == Material.PORTAL){
                state.getBlock().entityInside(state, level, ((BlockHitResult) result).getBlockPos(),this);
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sensitive", isSensitive());
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setSensitive(compound.getBoolean("sensitive"));
    }
}
