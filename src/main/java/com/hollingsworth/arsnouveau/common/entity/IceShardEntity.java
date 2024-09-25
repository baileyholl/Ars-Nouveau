package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class IceShardEntity extends EnchantedFallingBlock {
    public LivingEntity shooter;
    public IceShardEntity(EntityType<? extends ColoredProjectile> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }


    public IceShardEntity(Level worldIn, double x, double y, double z, BlockState fallingBlockState, SpellResolver resolver) {
        super(ModEntities.ICE_SHARD.get(), worldIn, x, y, z, fallingBlockState, resolver);
    }

    @Override
    public void callOnBrokenAfterFall(Block p_149651_, BlockPos p_149652_) {
        super.callOnBrokenAfterFall(p_149651_, p_149652_);

        if(level instanceof ServerLevel world){
            for(LivingEntity living : world.getEntitiesOfClass(LivingEntity.class, new AABB(BlockPos.containing(position)).inflate(1.25))){
                if(living == shooter)
                    continue;
                living.hurt(DamageUtil.source(world, DamageTypesRegistry.COLD_SNAP, shooter == null ? ANFakePlayer.getPlayer(world) : shooter), baseDamage);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 3, 1));
            }

            world.sendParticles(ParticleTypes.SPIT, position.x, position.y + 0.5, position.z, 10,
                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
            world.playSound(null, BlockPos.containing(position),  SoundEvents.GLASS_FALL, SoundSource.BLOCKS, 0.8f, 0.8f);
        }
    }

    @Override
    public void doPostHurtEffects(LivingEntity livingentity) {
        super.doPostHurtEffects(livingentity);
        livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 3, 1));
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.ICE_SHARD.get();
    }
}
