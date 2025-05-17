package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.WallProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.WallTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityWallSpell extends EntityProjectileSpell {

    public static final EntityDataAccessor<Integer> ACCELERATES = SynchedEntityData.defineId(EntityWallSpell.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> AOE = SynchedEntityData.defineId(EntityWallSpell.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> LANDED = SynchedEntityData.defineId(EntityWallSpell.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SENSITIVE = SynchedEntityData.defineId(EntityWallSpell.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(EntityWallSpell.class, EntityDataSerializers.DIRECTION);
    public static final EntityDataAccessor<Boolean> SHOULD_FALL = SynchedEntityData.defineId(EntityWallSpell.class, EntityDataSerializers.BOOLEAN);
    public double extendedTime;
    public int maxProcs = 100;
    public int totalProcs;
    List<EntityHit> hitEntities = new ArrayList<>();
    public float growthFactor = 1.0f;

    public EntityWallSpell(EntityType<? extends EntityProjectileSpell> type, Level worldIn) {
        super(ModEntities.WALL_SPELL.get(), worldIn);
    }

    public EntityWallSpell(Level worldIn, double x, double y, double z) {
        super(ModEntities.WALL_SPELL.get(), worldIn, x, y, z);
    }

    public EntityWallSpell(Level worldIn, LivingEntity shooter) {
        super(ModEntities.WALL_SPELL.get(), worldIn, shooter);
    }

    public void setAccelerates(int accelerates) {
        entityData.set(ACCELERATES, accelerates);
    }


    @Override
    public void tick() {
        if (!level.isClientSide) {
            if(resolver() == null)
                return;
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
        if(level.isClientSide)
            return;
        float aoe = getAoe();
        int flatAoe = Math.round(aoe);
        BlockPos start = blockPosition().offset(flatAoe * getDirection().getStepX(), 0, flatAoe * getDirection().getStepZ());
        BlockPos end = blockPosition().offset(-flatAoe  * getDirection().getStepX(), flatAoe, -flatAoe * getDirection().getStepZ());
        if (isSensitive()) {
            if(age % (Math.max(1,20 - 2 * getAccelerates())) != 0 && age != 1)
                return;
            for(BlockPos p : BlockPos.betweenClosed(start, end)){
                p = p.immutable();
                resolver().getNewResolver(resolver().spellContext.clone().makeChildContext()).onResolveEffect(level, new
                        BlockHitResult(new Vec3(p.getX(), p.getY(), p.getZ()), Direction.UP, p, false));
            }
        }else{
            int i = 0;
            // Expand the axis if start and end are equal

            AABB aabb = AABB.encapsulatingFullBlocks(start, end);
            if(aabb.maxX == aabb.minX){
                aabb = aabb.inflate(growthFactor, 0, 0);
            }
            if(aabb.maxY == aabb.minY){
                aabb = aabb.inflate(0, growthFactor, 0);
            }
            if(aabb.maxZ == aabb.minZ){
                aabb = aabb.inflate(0, 0, growthFactor);
            }
            for (Entity entity : level.getEntities(null, aabb)) {
                if (entity.equals(this) || entity.getType().is(EntityTags.LINGERING_BLACKLIST)) {
                    continue;
                }
                Optional<EntityHit> hit = hitEntities.stream().filter(e -> e.entity.refersTo(entity)).findFirst();
                boolean skipEntity = hit.isPresent();
                if(hit.isPresent() && level.getGameTime() - hit.get().gameTime > 20){
                    hitEntities.remove(hit.get());
                    skipEntity = false;
                }
                if(skipEntity)
                    continue;
                resolver().getNewResolver(resolver().spellContext.clone().makeChildContext()).onResolveEffect(level, new EntityHitResult(entity));
                i++;
                if(hit.isEmpty()){
                    hitEntities.add(new EntityHit(entity));
                }
                if (i > 5)
                    break;
            }
            totalProcs += i;
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
    public void buildEmitters() {
        TimelineMap timelineMap = this.resolver().spell.particleTimeline();
        WallTimeline projectileTimeline = timelineMap.get(ParticleTimelineRegistry.WALL_TIMELINE.get());
        TimelineEntryData trailConfig = projectileTimeline.trailEffect;
        TimelineEntryData resolveConfig = projectileTimeline.onResolvingEffect;
        this.tickEmitter = new ParticleEmitter(() -> this.getPosition(ClientInfo.partialTicks), this::getRotationVector, trailConfig.motion(), trailConfig.particleOptions());
        this.resolveEmitter = new ParticleEmitter(() -> this.getPosition(ClientInfo.partialTicks), this::getRotationVector, resolveConfig.motion(), resolveConfig.particleOptions());
        if (this.tickEmitter.particleOptions instanceof PropertyParticleOptions propertyParticleOptions) {
            propertyParticleOptions.map.set(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(Math.round(getAoe()), 5, 20, getDirection()));
        }
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.WALL_SPELL.get();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide && result instanceof BlockHitResult && !this.isRemoved()) {
            BlockState state = level.getBlockState(((BlockHitResult) result).getBlockPos());
            if (state.is(BlockTags.PORTALS)) {
                state.entityInside(level, ((BlockHitResult) result).getBlockPos(), this);
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

    public void setDirection(Direction direction) {
        entityData.set(DIRECTION, direction);
    }

    public @NotNull Direction getDirection() {
        return entityData.get(DIRECTION);
    }

    public void setShouldFall(boolean shouldFall) {
        entityData.set(SHOULD_FALL, shouldFall);
    }

    public boolean shouldFall() {
        return entityData.get(SHOULD_FALL);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ACCELERATES, 0);
        pBuilder.define(AOE, 0f);
        pBuilder.define(LANDED, false);
        pBuilder.define(SENSITIVE, false);
        pBuilder.define(DIRECTION, Direction.NORTH);
        pBuilder.define(SHOULD_FALL, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sensitive", isSensitive());
        tag.putString("direction", getDirection().name());
        tag.putBoolean("should_fall", shouldFall());
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setSensitive(compound.getBoolean("sensitive"));
        setDirection(Direction.valueOf(compound.getString("direction")));
        setShouldFall(compound.getBoolean("should_fall"));
    }
    public static class EntityHit{
        long gameTime;
        WeakReference<Entity> entity;
        public EntityHit(Entity entity){
            this.entity = new WeakReference<>(entity);
            gameTime = entity.level.getGameTime();
        }
    }
}