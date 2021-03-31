package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
// Copy of LightningBoltEntity
public class LightningEntity extends LightningBoltEntity {
    private int lightningState;
    public long boltVertex;
    private int boltLivingTime;
    private boolean effectOnly;
    List<Integer> hitEntities = new ArrayList<>();
    @Nullable
    private ServerPlayerEntity caster;

    public int amps;
    public int extendTimes;

    public LightningEntity(EntityType<? extends LightningBoltEntity> p_i231491_1_, World world) {
        super(p_i231491_1_, world);
        this.ignoreFrustumCheck = true;
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
    }

    public void setEffectOnly(boolean effectOnly) {
        this.effectOnly = effectOnly;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    public void setCaster(@Nullable ServerPlayerEntity casterIn) {
        this.caster = casterIn;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        if (this.lightningState == 2) {
            Difficulty difficulty = this.world.getDifficulty();
            if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
                this.igniteBlocks(4);
            }

            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0f, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 1.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;
        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0) {
                this.remove();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                --this.boltLivingTime;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();
                this.igniteBlocks(0);
            }
        }

        if (this.lightningState >= 0) {
            if (!(this.world instanceof ServerWorld)) {
                this.world.setTimeLightningFlash(2);
            } else if (!this.effectOnly) {
                double d0 = 3.0D;
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(this.getPosX() - 3.0D, this.getPosY() - 3.0D, this.getPosZ() - 3.0D, this.getPosX() + 3.0D, this.getPosY() + 6.0D + 3.0D, this.getPosZ() + 3.0D), Entity::isAlive);

                for(Entity entity : list) {
                    if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                        entity.func_241841_a((ServerWorld) this.world, this);
                        if(!world.isRemote && !hitEntities.contains(entity.getEntityId()) && entity instanceof LivingEntity){
                            EffectInstance effectInstance = ((LivingEntity) entity).getActivePotionEffect(ModPotions.SHOCKED_EFFECT);
                            int amp = effectInstance != null ? effectInstance.getAmplifier() : -1;
                            ((LivingEntity) entity).addPotionEffect(new EffectInstance(ModPotions.SHOCKED_EFFECT, 200 + 10*20*extendTimes, Math.min(2, amp + 1)));
                        }
                        if(!world.isRemote && !hitEntities.contains(entity))
                            hitEntities.add(entity.getEntityId());

                    }
                }

                if (this.caster != null) {
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.caster, list);
                }
            }
        }
    }

    private void igniteBlocks(int extraIgnitions) {
        if (!this.effectOnly && !this.world.isRemote && this.world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            BlockPos blockpos = this.getPosition();
            BlockState blockstate = AbstractFireBlock.getFireForPlacement(this.world, blockpos);
            if (this.world.getBlockState(blockpos).isAir() && blockstate.isValidPosition(this.world, blockpos)) {
                this.world.setBlockState(blockpos, blockstate);
            }

            for(int i = 0; i < extraIgnitions; ++i) {
                BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
                blockstate = AbstractFireBlock.getFireForPlacement(this.world, blockpos1);
                if (this.world.getBlockState(blockpos1).isAir() && blockstate.isValidPosition(this.world, blockpos1)) {
                    this.world.setBlockState(blockpos1, blockstate);
                }
            }

        }
    }

    public float getDamage(Entity entity){
        return 5.0f + 3.0f * amps + (entity.isWet() ? 2.0f : 0.0f);
    }

    /**
     * Checks if the entity is in range to render.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }

    protected void registerData() {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(CompoundNBT compound) {
    }

    protected void writeAdditional(CompoundNBT compound) {
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.LIGHTNING_ENTITY;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public LightningEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        super(ModEntities.LIGHTNING_ENTITY, world);
    }
}
