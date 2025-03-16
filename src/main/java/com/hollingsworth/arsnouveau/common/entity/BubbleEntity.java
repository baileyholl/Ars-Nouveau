package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.event.EntityPreRemovalEvent;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BubbleEntity extends Projectile implements GeoEntity {
    int maxAge;
    int age;
    float damage;
    public int poppingTicks;
    public boolean hasPopped;
    List<UUID> hasDismounted = new ArrayList<>();

    public static final EntityDataAccessor<Boolean> HAS_POPPED = SynchedEntityData.defineId(BubbleEntity.class, EntityDataSerializers.BOOLEAN);
    public BubbleEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BubbleEntity(Level pLevel, int maxAge, float damage) {
        super(ModEntities.BUBBLE.get(), pLevel);
        this.maxAge = maxAge;
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(HAS_POPPED, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public void tick() {
        super.tick();

        if (entityData.get(HAS_POPPED)) {
            poppingTicks++;
        }

        if (!level.isClientSide) {
            age++;
            if (age > maxAge) {
                this.pop();
            }

            if (this.getPassengers().isEmpty()) {
                for (Entity entity1 : level.getEntities(this, this.getBoundingBox().inflate(0.5f), this::canHitEntity)) {
                    entity1.startRiding(this);
                }
            }
        }

        if (poppingTicks > 5 && !level.isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.setDeltaMovement(ParticleUtil.inRange(-0.01, 0.01), 0.1, ParticleUtil.inRange(-0.01, 0.01));
        this.setPos(getNextHitPosition());
    }

    public boolean tryCapturing(Entity target){
        if (!this.entityData.get(HAS_POPPED) && this.getPassengers().isEmpty() && this.canHitEntity(target)) {
            target.startRiding(this);
            return !this.getPassengers().isEmpty();
        }
        return false;
    }

    public Vec3 getNextHitPosition() {
        return this.position().add(this.getDeltaMovement());
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if(passenger != null && passenger.getUUID() != null){
            this.hasDismounted.add(passenger.getUUID());
        }
        return super.getDismountLocationForPassenger(passenger);
    }

    public void pop(){
        if(this.level.isClientSide)
            return;
        if(this.entityData.get(HAS_POPPED)){
            return;
        }
        this.entityData.set(HAS_POPPED, true);
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, this.getSoundSource(), 3.0F, 1.0F);
    }

    // The only purpose of this is to prevent the default attack noise that occurs.
    public static void onAttacked(AttackEntityEvent event){
        if(event.getTarget() instanceof BubbleEntity bubble){
            if(bubble.getPassengers().isEmpty()
                    || bubble.getFirstPassenger() instanceof ItemEntity
                    || bubble.getFirstPassenger() == event.getEntity()) {
                bubble.pop();
                event.setCanceled(true);
            }else if(bubble.getFirstPassenger() instanceof LivingEntity passenger){
                event.getEntity().attack(passenger);
            }
        }
    }

    public static void entityHurt(LivingDamageEvent.Pre e) {
        if(e.getEntity().getVehicle() instanceof BubbleEntity bubble){
            if(bubble.age > 1
                    && !bubble.hasPopped
                    && bubble.getFirstPassenger() != bubble.getOwner()) {
                float damage = bubble.damage;
                Entity owner = bubble.getOwner();
                if(owner instanceof LivingEntity shooter){
                    damage += shooter.getAttributes().hasAttribute(PerkAttributes.SPELL_DAMAGE_BONUS) ?
                            (float) shooter.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS) : 0;
                }
                e.setNewDamage(e.getNewDamage() + damage);
            }
            bubble.pop();
        }
    }

    public static void preEntityRemoval(EntityPreRemovalEvent event) {
        if (event.getEntity().getVehicle() instanceof BubbleEntity bubble) {
            bubble.getEntityData().set(BubbleEntity.HAS_POPPED, true);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        tryCapturing(pResult.getEntity());
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.getPassengers().isEmpty() || this.getFirstPassenger() instanceof ItemEntity){
            this.pop();
        }
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        if(pTarget != null && pTarget.getUUID() != null){
            if(this.hasDismounted.contains(pTarget.getUUID())){
                return false;
            }
        }
        return !pTarget.getType().is(EntityTags.BUBBLE_BLACKLIST) && !(pTarget.getVehicle() instanceof BubbleEntity);
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity pEntity) {
        return pEntity instanceof ItemEntity ? this.position.add(0, 0.25f, 0) : this.position;
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return !(pVehicle instanceof BubbleEntity) && super.canRide(pVehicle);
    }

    public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        pCompound.putInt("maxAge", this.maxAge);
        pCompound.putFloat("damage", this.damage);
        pCompound.putInt("age", this.age);
        return super.save(pCompound);
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        this.maxAge = pCompound.getInt("maxAge");
        this.damage = pCompound.getFloat("damage");
        this.age = pCompound.getInt("age");
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.getPassengers().isEmpty() || this.getFirstPassenger() instanceof ItemEntity && (this.isAlive() && age > 1);
    }

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }
}
