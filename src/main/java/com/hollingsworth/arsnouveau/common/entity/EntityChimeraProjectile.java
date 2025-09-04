package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collection;

public class EntityChimeraProjectile extends AbstractArrow implements GeoEntity {
    int groundMax;

    public EntityChimeraProjectile(Level world) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE.get(), world);
    }

    public EntityChimeraProjectile(EntityType<EntityChimeraProjectile> entityChimeraProjectileEntityType, Level world) {
        super(entityChimeraProjectileEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide && this.inGroundTime >= 1) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected boolean tryPickup(Player pPlayer) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    protected void onHitEntity(EntityHitResult rayTraceResult) {
        Entity entity = rayTraceResult.getEntity();

        float damage = 7.5f;

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = level.damageSources().indirectMagic(this, null);
        } else {
            damagesource = level.damageSources().magic();
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastHurtMob(entity);
            }
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.igniteForSeconds(5);
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
                if (e.getEffect().value().isBeneficial())
                    entity.removeEffect(e.getEffect());
            }
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
                    || (entity instanceof ISummon summon && summon.getOwnerUUID() != null && summon.getOwnerUUID().equals(this.getUUID()))
                    || (entity1 instanceof SummonWolf && ((SummonWolf) entity1).isWildenSummon))
                return false;
        }
        return !(entity instanceof WildenChimera) && super.canHitEntity(entity);
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_CHIMERA_SPIKE.get();
    }
}
