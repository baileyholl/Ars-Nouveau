package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class BubbleEntity extends Projectile implements GeoEntity {
    int age;

    public BubbleEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BubbleEntity(Level pLevel) {
        super(ModEntities.BUBBLE.get(), pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){
            age++;
            if(age > 100) {
                remove(RemovalReason.DISCARDED);
            }
            if(this.getPassengers().isEmpty()) {
                for (Entity entity1 : level.getEntities(this, this.getBoundingBox().inflate(0.5f), this::canHitEntity)) {
                    entity1.startRiding(this);
                }
            }
        }

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();

        this.setDeltaMovement( ParticleUtil.inRange(-0.01, 0.01),  0.1, ParticleUtil.inRange(-0.01, 0.01));

        this.setPos(getNextHitPosition());

    }

    public Vec3 getNextHitPosition() {
        return this.position().add(this.getDeltaMovement());
    }


    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(this.getPassengers().isEmpty()){
            pResult.getEntity().startRiding(this);
        }
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return !(pTarget instanceof BubbleEntity);
    }

    public void traceAnyHit(@Nullable HitResult raytraceresult, Vec3 thisPosition, Vec3 nextPosition) {
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            nextPosition = raytraceresult.getLocation();
        }
        EntityHitResult entityraytraceresult = this.findHitEntity(thisPosition, thisPosition);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }
        if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.MISS && raytraceresult instanceof BlockHitResult blockHitResult) {
            BlockRegistry.PORTAL_BLOCK.get().onProjectileHit(level, level.getBlockState(BlockPos.containing(raytraceresult.getLocation())),
                    blockHitResult, this);
        }
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity pEntity) {
        return pEntity instanceof ItemEntity ? this.position.add(0, 0.5, 0) : this.position;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level, this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(2.0D), this::canHitEntity);
    }

    public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
