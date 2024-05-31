package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.network.NetworkHooks;
import net.neoforged.neoforge.network.PlayMessages;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LightningEntity extends LightningBolt {
    private int lightningState;
    public long boltVertex;
    private int boltLivingTime;
    private boolean effectOnly;
    List<Integer> hitEntities = new ArrayList<>();
    @Nullable
    private ServerPlayer caster;

    public float amps;
    public int extendTimes;

    public float ampScalar;
    public float wetBonus;

    public LightningEntity(EntityType<? extends LightningBolt> p_i231491_1_, Level world) {
        super(p_i231491_1_, world);
        this.noCulling = true;
        this.lightningState = 2;
        this.boltVertex = this.random.nextLong();
        this.boltLivingTime = this.random.nextInt(3) + 1;
    }

    public void setVisualOnly(boolean effectOnly) {
        this.effectOnly = effectOnly;
    }

    public SoundSource getSoundSource() {
        return SoundSource.WEATHER;
    }

    public void setCause(@Nullable ServerPlayer casterIn) {
        this.caster = casterIn;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        this.baseTick();
        if (this.lightningState == 2) {
            Difficulty difficulty = this.level.getDifficulty();

            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0f, 0.8F + this.random.nextFloat() * 0.2F);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 1.0F, 0.5F + this.random.nextFloat() * 0.2F);
        }

        --this.lightningState;
        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0) {
                this.remove(RemovalReason.DISCARDED);
            } else if (this.lightningState < -this.random.nextInt(10)) {
                --this.boltLivingTime;
                this.lightningState = 1;
                this.boltVertex = this.random.nextLong();
            }
        }

        if (this.lightningState >= 0) {
            if (!(this.level instanceof ServerLevel)) {
                this.level.setSkyFlashTime(2);
            } else if (!this.effectOnly) {
                List<Entity> list = this.level.getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);
                for (Entity entity : list) {
                    if (!net.neoforged.neoforge.event.EventHooks.onEntityStruckByLightning(entity, this)) {
                        float origDamage = this.getDamage();
                        this.setDamage(this.getDamage(entity));
                        EntityStruckByLightningEvent event = new EntityStruckByLightningEvent(entity, this);
                        NeoForge.EVENT_BUS.post(event);
                        if (event.isCanceled())
                            continue;
                        entity.thunderHit((ServerLevel) this.level, this);
                        this.setDamage(origDamage);
                        if (!level.isClientSide && !hitEntities.contains(entity.getId()) && entity instanceof LivingEntity) {
                            MobEffectInstance effectInstance = ((LivingEntity) entity).getEffect(ModPotions.SHOCKED_EFFECT.get());
                            int amp = effectInstance != null ? effectInstance.getAmplifier() : -1;
                            ((LivingEntity) entity).addEffect(new MobEffectInstance(ModPotions.SHOCKED_EFFECT.get(), 200 + 10 * 20 * extendTimes, Math.min(2, amp + 1)));
                        }
                        if (!level.isClientSide && !hitEntities.contains(entity.getId()))
                            hitEntities.add(entity.getId());

                    }
                }

                if (this.caster != null) {
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.caster, list);
                }
            }
        }
    }

    private void igniteBlocks(int extraIgnitions) {
        if (!this.effectOnly && !this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate = BaseFireBlock.getState(this.level, blockpos);
            if (this.level.getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level, blockpos)) {
                this.level.setBlockAndUpdate(blockpos, blockstate);
            }

            for (int i = 0; i < extraIgnitions; ++i) {
                BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
                blockstate = BaseFireBlock.getState(this.level, blockpos1);
                if (this.level.getBlockState(blockpos1).isAir() && blockstate.canSurvive(this.level, blockpos1)) {
                    this.level.setBlockAndUpdate(blockpos1, blockstate);
                }
            }

        }
    }


    public float getDamage(Entity entity) {
        float baseDamage = getDamage() + ampScalar * amps + (entity.isInWaterOrRain() ? wetBonus : 0.0f);
        int multiplier = 1;
        for (ItemStack i : entity.getArmorSlots()) {
            IEnergyStorage energyStorage = i.getCapability(Capabilities.ENERGY).orElse(null);
            if (energyStorage != null) {
                multiplier++;
            }
        }
        if (entity instanceof LivingEntity) {
            IEnergyStorage energyStorage = ((LivingEntity) entity).getMainHandItem().getCapability(Capabilities.ENERGY).orElse(null);
            if (energyStorage != null)
                multiplier++;
            energyStorage = ((LivingEntity) entity).getOffhandItem().getCapability(Capabilities.ENERGY).orElse(null);
            if (energyStorage != null)
                multiplier++;
        }
        return baseDamage * multiplier;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = 64.0D * getViewScale();
        return distance < d0 * d0;
    }

    protected void defineSynchedData() {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.LIGHTNING_ENTITY.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public LightningEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.LIGHTNING_ENTITY.get(), world);
    }
}
