package com.hollingsworth.arsnouveau.common.entity;

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
    public static final EntityDataAccessor<Float> AOE = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> LANDED = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SENSITIVE = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SHOULD_FALL = SynchedEntityData.defineId(EntityLingeringSpell.class, EntityDataSerializers.BOOLEAN);
    public double extendedTime;
    public int maxProcs = 100;
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
        float aoe = getAoe();
        int flatAoe = Math.round(aoe);
        if (!level.isClientSide && age % (20 - 2 * getAccelerates()) == 0) {
            if (isSensitive()) {
                for (BlockPos p : BlockPos.betweenClosed(blockPosition().east(flatAoe).north(flatAoe), blockPosition().west(flatAoe).south(flatAoe))) {
                    p = p.immutable();
                    spellResolver.onResolveEffect(level, new
                            BlockHitResult(new Vec3(p.getX(), p.getY(), p.getZ()), Direction.UP, p, false));
                }
            } else {
                int i = 0;
                for (Entity entity : level.getEntities(null, new AABB(this.blockPosition()).inflate(getAoe()))) {
                    if (entity.equals(this) || entity.getType().is(EntityTags.LINGERING_BLACKLIST))
                        continue;
                    spellResolver.onResolveEffect(level, new EntityHitResult(entity));
                    i++;
                    if (i > 5)
                        break;
                }
                totalProcs += i;
                if (totalProcs >= maxProcs)
                    this.remove(RemovalReason.DISCARDED);
            }
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
        ParticleUtil.spawnRitualAreaEffect(getOnPos(), level, random, getParticleColor(), Math.round(getAoe()), 5, 20);
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

    public void setAoe(float aoe) {
        entityData.set(AOE, aoe);
    }

    //for compat
    public float getAoe() {
        return (this.isSensitive() ? 1 : 3) + entityData.get(AOE);
    }

    public void setLanded(boolean landed) {
        entityData.set(LANDED, landed);
    }

    public boolean getLanded() {
        return entityData.get(LANDED);
    }

    public void setSensitive(boolean sensitive) {
        entityData.set(SENSITIVE, sensitive);
    }

    public boolean isSensitive() {
        return entityData.get(SENSITIVE);
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
        entityData.define(AOE, 0f);
        entityData.define(LANDED, false);
        entityData.define(SENSITIVE, false);
        entityData.define(SHOULD_FALL, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sensitive", isSensitive());
        tag.putBoolean("shouldFall", shouldFall());
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setSensitive(compound.getBoolean("sensitive"));
        setShouldFall(compound.getBoolean("shouldFall"));
    }
}
