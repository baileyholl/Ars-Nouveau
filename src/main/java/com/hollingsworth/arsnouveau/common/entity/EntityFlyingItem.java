package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.NearbyPlayerCache;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.util.EasingManager;
import com.hollingsworth.arsnouveau.common.util.EasingType;
import com.hollingsworth.arsnouveau.setup.registry.DataSerializers;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityFlyingItem extends ColoredProjectile {
    public static final EntityDataAccessor<Vec3> to = SynchedEntityData.defineId(EntityFlyingItem.class, DataSerializers.VEC.get());
    public static final EntityDataAccessor<Vec3> from = SynchedEntityData.defineId(EntityFlyingItem.class, DataSerializers.VEC.get());
    public static final EntityDataAccessor<Boolean> SPAWN_TOUCH = SynchedEntityData.defineId(EntityFlyingItem.class, EntityDataSerializers.BOOLEAN);

    public int age;
    int maxAge;

    public static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(EntityFlyingItem.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Float> OFFSET = SynchedEntityData.defineId(EntityFlyingItem.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> DIDOFFSET = SynchedEntityData.defineId(EntityFlyingItem.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_BUBBLE = SynchedEntityData.defineId(EntityFlyingItem.class, EntityDataSerializers.BOOLEAN);

    public EntityFlyingItem(Level worldIn, Vec3 from, Vec3 to) {
        this(worldIn, from, to, 255, 25, 180);
    }

    public EntityFlyingItem(Level worldIn, Vec3 from, Vec3 to, int r, int g, int b) {
        this(ModEntities.ENTITY_FLYING_ITEM.get(), worldIn);
        this.entityData.set(EntityFlyingItem.to, to);
        this.entityData.set(EntityFlyingItem.from, from);
        this.maxAge = (int) Math.floor(from.subtract(to).length() * 5);
        this.entityData.set(RED, r);
        this.entityData.set(GREEN, g);
        this.entityData.set(BLUE, b);
        setPos(from);
    }

    public EntityFlyingItem(Level worldIn, BlockPos from, BlockPos to) {
        this(worldIn, new Vec3(from.getX() + 0.5, from.getY(), from.getZ() + 0.5), new Vec3(to.getX() + 0.5, to.getY(), to.getZ() + 0.5), 255, 25, 180);
    }

    public EntityFlyingItem(Level worldIn, BlockPos from, BlockPos to, int r, int g, int b) {
        this(worldIn, new Vec3(from.getX() + 0.5, from.getY(), from.getZ() + 0.5), new Vec3(to.getX() + 0.5, to.getY(), to.getZ() + 0.5), r,g,b);
    }

    public EntityFlyingItem(EntityType<EntityFlyingItem> entityAOEProjectileEntityType, Level world) {
        super(entityAOEProjectileEntityType, world);
    }

    public static @NotNull EntityFlyingItem spawn(ServerLevel level, Vec3 from, Vec3 to, int r, int g, int b){
        boolean canSpawn = NearbyPlayerCache.isPlayerNearby(BlockPos.containing(from), level, 64);
        EntityFlyingItem entity = new EntityFlyingItem(level, from, to, r, g, b);
        if(canSpawn && level.isLoaded(BlockPos.containing(to))) {
            level.addFreshEntity(entity);
        }
        return entity;
    }

    public static @NotNull EntityFlyingItem spawn(ServerLevel level, Vec3 from, Vec3 to){
        return spawn(level, from, to, 255, 25, 180);
    }


    public static @NotNull EntityFlyingItem spawn(ServerLevel level, BlockPos from, BlockPos to, int r, int g, int b){
        boolean canSpawn = NearbyPlayerCache.isPlayerNearby(from, level, 64);
        EntityFlyingItem entity = new EntityFlyingItem(level, from, to, r, g, b);
        if(canSpawn && level.isLoaded(to)) {
            level.addFreshEntity(entity);
        }
        return entity;
    }

    public static @NotNull EntityFlyingItem spawn(ServerLevel level, BlockPos from, BlockPos to){
        return spawn(level, from, to, 255, 25, 180);
    }

    public EntityFlyingItem setStack(ItemStack stack) {
        this.entityData.set(HELD_ITEM, stack.copy());
        return this;
    }

    /**
     * This is the actual function that smoothly interpolates (lerp) between keyframes
     *
     * @param startValue The animation's start value
     * @param endValue   The animation's end value
     * @return The interpolated value
     */
    public static float lerp(double percentCompleted, double startValue, double endValue, EasingType type) {
        if (percentCompleted >= 1) {
            return (float) endValue;
        }
        percentCompleted = EasingManager.ease(percentCompleted, type, null);
        // current tick / position should be between 0 and 1 and represent the percentage of the lerping that has completed
        return (float) lerpInternal(percentCompleted, startValue,
                endValue);
    }

    public static double lerpInternal(double pct, double start, double end) {
        return start + pct * (end - start);
    }

    /**
     * Calculates a value between 0 and 1, given the precondition that value
     * is between min and max. 0 means value = max, and 1 means value = min.
     */
    public double normalize(double value, double min, double max) {
        return 1.0 - ((value - min) / (max - min));
    }

    boolean wentUp;

    @Override
    public void tick() {
        super.tick();
        this.age++;


        if (age > 400)
            this.remove(RemovalReason.DISCARDED);

        Vec3 start = entityData.get(from);
        Vec3 end = entityData.get(to);
        if (BlockUtil.distanceFrom(end, this.position()) <= 1 || this.age > 1000 || BlockUtil.distanceFrom(end, this.position()) > 16) {
            this.remove(RemovalReason.DISCARDED);
            if (level.isClientSide && entityData.get(SPAWN_TOUCH)) {
                ParticleUtil.spawnTouch((ClientLevel) level, BlockPos.containing(end.x, end.y, end.z), new ParticleColor(this.entityData.get(RED), this.entityData.get(GREEN), this.entityData.get(BLUE)));
            }
            return;
        }

        double posX = getX();
        double posY = getY();
        double posZ = getZ();


        double time = 1 - normalize(age, 0.0, 80);

        EasingType type = EasingType.NONE;

        double startY = start.y();
        double endY = end.y() + getDistanceAdjustment(start, end);
        double lerpX = lerp(time, start.x(), end.x(), type);
        double lerpY = lerp(time, lerp(time, startY, endY, type), lerp(time, endY, startY, type), type);
        double lerpZ = lerp(time, start.z(), end.z(), type);

        Vec3 adjustedPos = new Vec3(posX, end.y(), posZ);
        if (BlockUtil.distanceFrom(end, adjustedPos) <= 0.5) {
            posY = getY() - 0.05;
            this.setPos(lerpX, posY, lerpZ);
        } else {
            this.setPos(lerpX, lerpY, lerpZ);
        }

        if (level.isClientSide && this.age > 1 && !this.getEntityData().get(IS_BUBBLE)) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 20);
            int counter = 0;

            for (double i = 0; i < dist; i++) {
                double coeff = i / dist;
                counter += level.random.nextInt(3);
                if (counter % (Minecraft.getInstance().options.particles().get().getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles().get().getId()) == 0) {
                    level.addParticle(GlowParticleData.createData(
                                    new ParticleColor(this.entityData.get(RED), this.entityData.get(GREEN), this.entityData.get(BLUE))),
                            (float) (xo + deltaX * coeff), (float) (yo + deltaY * coeff), (float) (zo + deltaZ * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));
                }
            }

        }
    }

    public EntityFlyingItem withNoTouch() {
        this.entityData.set(SPAWN_TOUCH, false);
        return this;
    }

    public void setDistanceAdjust(float offset) {
        this.entityData.set(OFFSET, offset);
        this.entityData.set(DIDOFFSET, true);
    }

    private double getDistanceAdjustment(Vec3 start, Vec3 end) {
        if (this.entityData.get(DIDOFFSET))
            return this.entityData.get(OFFSET);

        double distance = BlockUtil.distanceFrom(start, end);
        if (distance <= 1.5)
            return 2.5;

        return 3;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.entityData.set(HELD_ITEM, ItemStack.parseOptional(level.registryAccess(), compound.getCompound("item")));
        this.age = compound.getInt("age");
        this.entityData.set(DIDOFFSET, compound.getBoolean("didoffset"));
        this.entityData.set(OFFSET, compound.getFloat("offset"));
        this.entityData.set(EntityFlyingItem.from, NBTUtil.getVec(compound, "from"));
        this.entityData.set(EntityFlyingItem.to, NBTUtil.getVec(compound, "to"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (getStack() != null) {
            Tag tag = getStack().save(level.registryAccess());
            compound.put("item", tag);
        }
        compound.putInt("age", age);
        compound.putBoolean("didoffset", this.entityData.get(DIDOFFSET));
        compound.putFloat("offset", this.entityData.get(OFFSET));
        if (from != null)
            NBTUtil.storeVec(compound, "from", this.entityData.get(EntityFlyingItem.from));
        if (to != null)
            NBTUtil.storeVec(compound, "to", this.entityData.get(EntityFlyingItem.to));
    }

    public ItemStack getStack() {
        return this.entityData.get(HELD_ITEM);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HELD_ITEM, ItemStack.EMPTY);
        pBuilder.define(OFFSET, 0.0f);
        pBuilder.define(DIDOFFSET, false);
        pBuilder.define(to, new Vec3(0, 0, 0));
        pBuilder.define(from, new Vec3(0, 0, 0));
        pBuilder.define(SPAWN_TOUCH, true);
        pBuilder.define(IS_BUBBLE, false);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FLYING_ITEM.get();
    }
}
