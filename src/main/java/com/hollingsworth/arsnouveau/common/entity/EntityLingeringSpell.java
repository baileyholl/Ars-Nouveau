package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.*;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

public class EntityLingeringSpell extends EntityProjectileSpell {

    public static final EntityDataAccessor<Integer> ACCELERATES = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> LANDED = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SHOULD_FALL = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public double extendedTime;
    public int maxProcs = 20;
    public int totalProcs;

    public EntityLingeringSpell(EntityType<? extends EntityProjectileSpell> type, Level worldIn) {
        super(ModEntities.LINGER_SPELL.get(), worldIn);
    }

    public EntityLingeringSpell(Level worldIn, double x, double y, double z) {
        super(ModEntities.LINGER_SPELL.get(), worldIn, x, y, z);
    }

    public EntityLingeringSpell(Level worldIn, LivingEntity shooter) {
        super(ModEntities.LINGER_SPELL.get(), worldIn, shooter);
    }

    public void setAccelerates(int accelerates) {
        entityData.set(ACCELERATES, accelerates);
    }


    @Override
    public void tick() {
        if (!level.isClientSide) {
            boolean isOnGround = level.getBlockState(blockPosition()).blocksMotion();
            this.setLanded(isOnGround);
        }
        super.tick();
        castSpells();
    }

    @Override
    public void traceAnyHit(@Nullable HitResult raytraceresult, Vec3 thisPosition, Vec3 nextPosition) {
    }

    @Override
    public void tickNextPosition() {
        if(!shouldFall())
            return;
        if (!getLanded()) {
            this.setDeltaMovement(0, -0.2, 0);
        } else {
            this.setDeltaMovement(0, 0, 0);
        }
        super.tickNextPosition();
    }

    public void castSpells() {
        if (!level.isClientSide && age % (20 - 4 * getAccelerates()) == 0) {
        BlockPos pos = blockPosition();
        //note: clone resolver for each pos so if it manipulates context, it doesn't affect the whole spell.
        spellResolver.getNewResolver(spellResolver.spellContext.clone()).onResolveEffect(level, new
                BlockHitResult(new Vec3(pos.getX(),pos.getY(),pos.getZ()), Direction.UP, pos, false));

            totalProcs += 1;
            if (totalProcs >= maxProcs)
                this.remove(RemovalReason.DISCARDED);
        }
    }


    @Override
    public int getExpirationTime() {
        return (int) (70 + extendedTime * 20);
    }

    @Override
    public int getParticleDelay() {
        return 0;
    }

    @Override
    public void playParticles() {
        //since this PARTICULAR lingering effect is 1x1, it has twice the particle count (40 as opposed to 20)
        //note the lingering versions of other spells are kept at the lower value to reduce lag
        ParticleUtil.spawnRitualAreaEffect(getOnPos(), level, random, getParticleColor(), 0, 5, 40);
        ParticleUtil.spawnLight(level, getParticleColor(), position.add(0, 0.5, 0), 10);
    }

    public EntityLingeringSpell(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.LINGER_SPELL.get(), world);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.LINGER_SPELL.get();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide && result instanceof BlockHitResult && !this.isRemoved()) {
            BlockState state = level.getBlockState(((BlockHitResult) result).getBlockPos());
            if (state.is(BlockTags.PORTALS)) {
                state.getBlock().entityInside(state, level, ((BlockHitResult) result).getBlockPos(), this);
                return;
            }
            this.setLanded(true);
        }
    }

    public int getAccelerates() {
        return entityData.get(ACCELERATES);
    }

    public void setLanded(boolean landed) {
        entityData.set(LANDED, landed);
    }

    public boolean getLanded() {
        return entityData.get(LANDED);
    }

    public void setShouldFall(boolean shouldFall) {
        entityData.set(SHOULD_FALL, shouldFall);
    }

    public boolean shouldFall() {
        return entityData.get(SHOULD_FALL);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACCELERATES, 0);
        entityData.define(LANDED, false);
        entityData.define(SHOULD_FALL, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("shouldFall", shouldFall());
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setShouldFall(compound.getBoolean("shouldFall"));
    }
}
