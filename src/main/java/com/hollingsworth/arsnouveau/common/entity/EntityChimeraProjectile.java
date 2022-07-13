package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Collection;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityChimeraProjectile extends AbstractArrow implements IAnimatable {
    int groundMax;

    public EntityChimeraProjectile(double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, Level p_i48547_8_) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE.get(), p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public EntityChimeraProjectile(LivingEntity p_i48548_2_, Level p_i48548_3_) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE.get(), p_i48548_2_, p_i48548_3_);
    }

    public EntityChimeraProjectile(Level world) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE.get(), world);
    }

    public EntityChimeraProjectile(EntityType<EntityChimeraProjectile> entityChimeraProjectileEntityType, Level world) {
        super(entityChimeraProjectileEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
//        if(groundMax == 0)
//            groundMax = 60 + random.nextInt(60);
        if (!level.isClientSide && this.inGroundTime >= 1) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    private <E extends Entity> PlayState attackPredicate(AnimationEvent e) {
//        e.getController().setAnimation(new AnimationBuilder().addAnimation("spike_spin"));
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 1, this::attackPredicate));
    }

    @Override
    protected void onHitEntity(EntityHitResult rayTraceResult) {
        Entity entity = rayTraceResult.getEntity();

        float damage = 7.5f;

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.indirectMagic(this, null);
        } else {
            damagesource = DamageSource.MAGIC;
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastHurtMob(entity);
            }
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, damage)) {
            if (isEnderman) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                this.doPostHurtEffects(livingentity);
            }

            this.playSound(this.getDefaultHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.remove(RemovalReason.DISCARDED);

        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180f);
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        if (!level.isClientSide) {

            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
            for (MobEffectInstance e : array) {
                if (e.getEffect().isBeneficial())
                    entity.removeEffect(e.getEffect());
            }
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        }

    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof EntityChimeraProjectile) {
            return false;
        }

        if (entity instanceof LivingEntity entity1) {
            // Omit our summoned sources that might aggro or accidentally hurt us
            if (entity1 instanceof WildenStalker || entity1 instanceof WildenGuardian || entity instanceof WildenHunter
                    || (entity instanceof ISummon summon && summon.getOwnerID() != null && summon.getOwnerID().equals(this.getUUID()))
                    || (entity1 instanceof SummonWolf && ((SummonWolf) entity1).isWildenSummon))
                return false;
        }
        return !(entity instanceof EntityChimera) && super.canHitEntity(entity);
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_CHIMERA_SPIKE.get();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityChimeraProjectile(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE.get(), world);
    }
}
