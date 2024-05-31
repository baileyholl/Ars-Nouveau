package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import java.util.UUID;

public class EntityEvokerFangs extends EvokerFangs {


    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    float damage;

    public EntityEvokerFangs(EntityType<? extends EvokerFangs> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
    }


    public EntityEvokerFangs(Level worldIn, double x, double y, double z, float p_i47276_8_, int p_i47276_9_, LivingEntity casterIn, float damage) {
        this(EntityType.EVOKER_FANGS, worldIn);
        this.warmupDelayTicks = p_i47276_9_;
        this.setOwner(casterIn);
        this.yRot = p_i47276_8_ * (180F / (float) Math.PI);
        this.setPos(x, y, z);
        this.damage = damage;
    }


    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        // Entity.super
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.isCurrentlyGlowing());
        }
        this.baseTick();
        if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                        double d1 = this.getY() + 0.05D + this.random.nextDouble();
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                        double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        double d4 = 0.3D + this.random.nextDouble() * 0.3D;
                        double d5 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        this.level.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                    this.damage(livingentity);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level.broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    private void damage(LivingEntity p_190551_1_) {
        LivingEntity livingentity = this.getOwner();
        if (p_190551_1_.isAlive() && !p_190551_1_.isInvulnerable() && p_190551_1_ != livingentity) {
            if (livingentity == null) {
                p_190551_1_.hurt(level.damageSources().magic(), damage);
            } else {
                if (livingentity.isAlliedTo(p_190551_1_)) {
                    return;
                }
                p_190551_1_.hurt(level.damageSources().indirectMagic(this, livingentity), damage);
            }
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("OwnerUUID")) {
            this.casterUuid = compound.getUUID("OwnerUUID");
        }

    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("OwnerUUID", this.casterUuid);
        }

    }


    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        } else {
            int i = this.lifeTicks - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float) i - partialTicks) / 20.0F;
        }
    }
}
