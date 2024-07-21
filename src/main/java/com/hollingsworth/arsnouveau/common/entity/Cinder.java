package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Cinder extends EnchantedFallingBlock {
    public LivingEntity shooter;

    public Cinder(EntityType<? extends Cinder> type, Level worldIn) {
        super(type, worldIn);
    }

    public Cinder(Level worldIn, double x, double y, double z, BlockState fallingBlockState, SpellResolver resolver) {
        super(ModEntities.CINDER.get(), worldIn, x, y, z, fallingBlockState, resolver);
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide){
            level.addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), ParticleUtil.inRange(-0.05f, 0.05f), ParticleUtil.inRange(0.01f, 0.05f), ParticleUtil.inRange(-0.05f, 0.05f));
        }
    }

    @Override
    public void callOnBrokenAfterFall(Block p_149651_, BlockPos p_149652_) {
        super.callOnBrokenAfterFall(p_149651_, p_149652_);

        if(level instanceof ServerLevel world){
//            for(LivingEntity living : world.getEntitiesOfClass(LivingEntity.class, new AABB(BlockPos.containing(position)).inflate(1.25))){
//                if(living == shooter)
//                    continue;
//                living.hurt(DamageUtil.source(world, DamageTypesRegistry.COLD_SNAP, shooter == null ? ANFakePlayer.getPlayer(world) : shooter), baseDamage);
//                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 3, 1));
//            }

            world.sendParticles(ParticleTypes.SMOKE, position.x, position.y + 0.5, position.z, 10,
                    0, ParticleUtil.inRange(-0.1, 0.1), 0, 0.03);
            world.playSound(null, BlockPos.containing(position),  SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.05f, 0.8f);
        }
    }

    @Override
    public void doPostHurtEffects(LivingEntity livingentity) {
        super.doPostHurtEffects(livingentity);

    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.getType().is(EntityTags.SPELL_CAN_HIT);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.CINDER.get();
    }

}
