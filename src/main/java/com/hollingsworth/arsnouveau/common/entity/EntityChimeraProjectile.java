package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Collection;

public class EntityChimeraProjectile extends AbstractArrowEntity implements IAnimatable {
    int groundMax;
    public EntityChimeraProjectile(double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE, p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public EntityChimeraProjectile(LivingEntity p_i48548_2_, World p_i48548_3_) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE, p_i48548_2_, p_i48548_3_);
    }

    public EntityChimeraProjectile(World world){
        super(ModEntities.ENTITY_CHIMERA_SPIKE, world);
    }

    public EntityChimeraProjectile(EntityType<EntityChimeraProjectile> entityChimeraProjectileEntityType, World world) {
        super(entityChimeraProjectileEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
//        if(groundMax == 0)
//            groundMax = 60 + random.nextInt(60);
        if(!level.isClientSide && this.inGroundTime >= 1){
            this.remove();
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
    protected void onHitEntity(EntityRayTraceResult rayTraceResult) {
        Entity entity = rayTraceResult.getEntity();

        int damage = 5;

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.indirectMagic(this, null);
        } else {
            damagesource = DamageSource.MAGIC;
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity)entity1).setLastHurtMob(entity);
            }
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float)damage)) {
            if (isEnderman) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                this.doPostHurtEffects(livingentity);
            }

            this.playSound(this.getDefaultHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.remove();

        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.yRot += 180.0F;
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                this.remove();
            }
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        if(!level.isClientSide){

            Collection<EffectInstance> effects = entity.getActiveEffects();
            EffectInstance[] array = effects.toArray(new EffectInstance[effects.size()]);
            for (EffectInstance e : array) {
                if (e.getEffect().isBeneficial())
                    entity.removeEffect(e.getEffect());
            }
            entity.addEffect(new EffectInstance(Effects.WEAKNESS, 200, 2));
            entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100));
        }

    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof EntityChimeraProjectile) {
            return false;
        }

        if(entity instanceof LivingEntity) {
            LivingEntity entity1 = (LivingEntity) entity;
            // Omit our summoned sources that might aggro or accidentally hurt us
            if (entity1 instanceof WildenStalker || entity1 instanceof WildenGuardian || entity instanceof WildenHunter
                    || (entity instanceof ISummon && ((ISummon) entity).getOwnerID() != null && ((ISummon) entity).getOwnerID().equals(this.getUUID()))
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
        return ModEntities.ENTITY_CHIMERA_SPIKE;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityChimeraProjectile(FMLPlayMessages.SpawnEntity packet, World world) {
        super(ModEntities.ENTITY_CHIMERA_SPIKE, world);
    }
}
