package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.NearbyPlayerCache;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityFollowProjectile extends ColoredProjectile {
    public static final EntityDataAccessor<BlockPos> to = SynchedEntityData.defineId(EntityFollowProjectile.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<BlockPos> from = SynchedEntityData.defineId(EntityFollowProjectile.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> SPAWN_TOUCH = SynchedEntityData.defineId(EntityFollowProjectile.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DESPAWN = SynchedEntityData.defineId(EntityFollowProjectile.class, EntityDataSerializers.INT);

    private int age;
    int maxAge = 500;

    public void setDespawnDistance(int distance) {
        getEntityData().set(DESPAWN, distance);
    }

    public EntityFollowProjectile(Level worldIn, Vec3 from, Vec3 to) {
        this(ModEntities.ENTITY_FOLLOW_PROJ.get(), worldIn);
        this.entityData.set(EntityFollowProjectile.to, BlockPos.containing(to));
        this.entityData.set(EntityFollowProjectile.from, BlockPos.containing(from));
        setPos(from.x + 0.5, from.y + 0.5, from.z + 0.5);
        this.entityData.set(RED, 255);
        this.entityData.set(GREEN, 25);
        this.entityData.set(BLUE, 180);

        double distance = BlockUtil.distanceFrom(BlockPos.containing(from), BlockPos.containing(to));
        setDespawnDistance((int) (distance + 10));
    }

    public EntityFollowProjectile(Level worldIn, BlockPos from, BlockPos to, int r, int g, int b) {
        this(worldIn, new Vec3(from.getX(), from.getY(), from.getZ()), new Vec3(to.getX(), to.getY(), to.getZ()));
        this.entityData.set(RED, Math.min(r, 255));
        this.entityData.set(GREEN, Math.min(g, 255));
        this.entityData.set(BLUE, Math.min(b, 255));

    }

    public EntityFollowProjectile(Level worldIn, BlockPos from, BlockPos to, ParticleColor.IntWrapper color) {
        this(worldIn, from, to, color.r, color.g, color.b);
    }

    public EntityFollowProjectile(EntityType<? extends EntityFollowProjectile> entityAOEProjectileEntityType, Level world) {
        super(entityAOEProjectileEntityType, world);
    }

    /**
     * These are preferred for any cases where entities could be spawned without a player nearby.
     * For instance, rituals don't need to check this, but automated source would.
     */
    public static void spawn(ServerLevel level, BlockPos from, BlockPos to, int r, int g, int b) {
        boolean canSpawn = NearbyPlayerCache.isPlayerNearby(from, level, 64);
        if (!canSpawn) {
            return;
        }
        EntityFollowProjectile entity = new EntityFollowProjectile(level, from, to, r, g, b);
        level.addFreshEntity(entity);
    }

    public static void spawn(ServerLevel level, BlockPos from, BlockPos to) {
        EntityFollowProjectile.spawn(level, from, to, 255, 25, 180);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(to, new BlockPos(0, 0, 0));
        pBuilder.define(from, new BlockPos(0, 0, 0));
        pBuilder.define(SPAWN_TOUCH, defaultsBurst());
        pBuilder.define(DESPAWN, 10);
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
    }

    public boolean defaultsBurst() {
        return false;
    }


    @Override
    public void tick() {
        super.tick();

        this.age++;
        if (age > maxAge) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        Vec3 vec3d2 = this.getDeltaMovement();
        BlockPos dest = this.entityData.get(EntityFollowProjectile.to);
        if (BlockUtil.distanceFrom(this.blockPosition(), dest) < 1 || this.age > 1000 || BlockUtil.distanceFrom(this.blockPosition(), dest) > this.entityData.get(DESPAWN)) {
            if (level.isClientSide && entityData.get(SPAWN_TOUCH)) {
                ParticleUtil.spawnTouch((ClientLevel) level, this.getOnPos(), new ParticleColor(this.entityData.get(RED), this.entityData.get(GREEN), this.entityData.get(BLUE)));
            }
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        double posX = getX();
        double posY = getY();
        double posZ = getZ();
        double motionX = this.getDeltaMovement().x;
        double motionY = this.getDeltaMovement().y;
        double motionZ = this.getDeltaMovement().z;

        if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0) {
            double targetX = dest.getX() + 0.5;
            double targetY = dest.getY() + 0.5;
            double targetZ = dest.getZ() + 0.5;
            Vec3 targetVector = new Vec3(targetX - posX, targetY - posY, targetZ - posZ);
            double length = targetVector.length();
            targetVector = targetVector.scale(0.3 / length);
            double weight = 0;
            if (length <= 3) {
                weight = 0.9 * ((3.0 - length) / 3.0);
            }

            motionX = (0.9 - weight) * motionX + (0.1 + weight) * targetVector.x;
            motionY = (0.9 - weight) * motionY + (0.1 + weight) * targetVector.y;
            motionZ = (0.9 - weight) * motionZ + (0.1 + weight) * targetVector.z;
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        this.setPos(posX, posY, posZ);

        this.setDeltaMovement(motionX, motionY, motionZ);

        if (level.isClientSide && this.age > 1) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            float dist = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8.0f);
            for (double i = 0.0; i <= dist; i++) {
                double coeff = (i / dist);
                level.addAlwaysVisibleParticle(GlowParticleData.createData(new ParticleColor(this.entityData.get(RED), this.entityData.get(GREEN), this.entityData.get(BLUE))),
                        true,
                        (getX() + deltaX * coeff), (getY() + deltaY * coeff), (getZ() + deltaZ * coeff),
                        0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));

            }

        }
    }

    @Override
    public void setRemoved(RemovalReason reason) {
        if (reason == RemovalReason.UNLOADED_TO_CHUNK)
            reason = RemovalReason.DISCARDED;
        super.setRemoved(reason);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(EntityFollowProjectile.from, NBTUtil.getBlockPos(compound, "from"));
        this.entityData.set(EntityFollowProjectile.to, NBTUtil.getBlockPos(compound, "to"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (from != null)
            NBTUtil.storeBlockPos(compound, "from", this.entityData.get(EntityFollowProjectile.from));
        if (to != null)
            NBTUtil.storeBlockPos(compound, "to", this.entityData.get(EntityFollowProjectile.to));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FOLLOW_PROJ.get();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}
