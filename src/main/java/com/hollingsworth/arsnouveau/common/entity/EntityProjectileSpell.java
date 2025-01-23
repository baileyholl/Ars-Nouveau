package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.block.IPrismaticBlock;
import com.hollingsworth.arsnouveau.api.event.SpellProjectileHitEvent;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataSerializers;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class EntityProjectileSpell extends ColoredProjectile {

    public int age;
    @Deprecated(forRemoval = true)
    public SpellResolver spellResolver;
    public int pierceLeft;
    //to use if you want the bounce augment indipendent from the pierce augment
    //public int bouncesLeft;
    public int numSensitive;

    public boolean isNoGravity = true;
    public boolean canTraversePortals = true;
    public int prismRedirect;
    ParticleEmitter trailEmitter;
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(EntityProjectileSpell.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<SpellResolver> SPELL_RESOLVER = SynchedEntityData.defineId(EntityProjectileSpell.class, DataSerializers.SPELL_RESOLVER.get());
    @Deprecated
    public int expireTime = 60 * 20;

    public Set<BlockPos> hitList = new HashSet<>();

    public EntityProjectileSpell(EntityType<? extends EntityProjectileSpell> entityType, Level world) {
        super(entityType, world);
    }

    public EntityProjectileSpell(EntityType<? extends EntityProjectileSpell> entityType, Level world, double x, double y, double z) {
        super(entityType, world, x, y, z);
    }

    public EntityProjectileSpell(EntityType<? extends EntityProjectileSpell> type, Level worldIn, LivingEntity shooter) {
        super(type, worldIn, shooter);
        setPos(shooter.getX(), shooter.getEyeY() - (double) 0.1F, shooter.getZ());
    }

    public EntityProjectileSpell(Level world, double x, double y, double z) {
        this(ModEntities.SPELL_PROJ.get(), world, x, y, z);
    }

    public EntityProjectileSpell(EntityType<? extends EntityProjectileSpell> entityType, Level world, SpellResolver resolver) {
        this(entityType, world, resolver.spellContext.getUnwrappedCaster());
        setResolver(resolver);
        this.pierceLeft = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.getUnwrappedCaster(), AugmentPierce.INSTANCE);
        this.numSensitive = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.getUnwrappedCaster(), AugmentSensitive.INSTANCE);
        setColor(resolver.spellContext.getColors());
    }

    public EntityProjectileSpell(Level world, SpellResolver resolver) {
        this(ModEntities.SPELL_PROJ.get(), world, resolver);
    }

    public EntityProjectileSpell(final Level world, final LivingEntity shooter) {
        this(ModEntities.SPELL_PROJ.get(), world, shooter);
    }

    public SpellResolver resolver(){
        return this.entityData.get(SPELL_RESOLVER);
    }

    public void setResolver(SpellResolver resolver){
        this.entityData.set(SPELL_RESOLVER, resolver);
        this.spellResolver = resolver;
        this.trailEmitter = new ParticleEmitter(() -> this.getPosition(0), this::getRotationVector, this.resolver().spell.particleTimeline().trailEffect);
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        age++;

        if ((!level.isClientSide && this.age > getExpirationTime()) || (!level.isClientSide && resolver() == null)) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }


        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();


        Vec3 thisPosition = this.position();
        Vec3 nextPosition = getNextHitPosition();
        traceAnyHit(getHitResult(), thisPosition, nextPosition);
        tickNextPosition();

        if (level.isClientSide && this.age > getParticleDelay()) {
            playParticles();
        }
    }

    public HitResult getHitResult() {
        Vec3 thisPosition = this.position();
        Vec3 nextPosition = getNextHitPosition();
        return this.level.clip(new ClipContext(thisPosition, nextPosition, numSensitive > 0 ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_ID, -1);
        pBuilder.define(SPELL_RESOLVER,  new SpellResolver(new SpellContext(level, new Spell(), null, null)));
    }

    /**
     * The next position for ray tracing. Override if you need special tracing beyond the normal next position tracing.
     */
    public Vec3 getNextHitPosition() {
        return this.position().add(this.getDeltaMovement());
    }

    public void traceAnyHit(@Nullable HitResult raytraceresult, Vec3 thisPosition, Vec3 nextPosition) {
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            nextPosition = raytraceresult.getLocation();
        }
        EntityHitResult entityraytraceresult = this.findHitEntity(thisPosition, nextPosition);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }
        if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.MISS && raytraceresult instanceof BlockHitResult blockHitResult
            && canTraversePortals()) {
            BlockRegistry.PORTAL_BLOCK.get().onProjectileHit(level, level.getBlockState(BlockPos.containing(raytraceresult.getLocation())),
                    blockHitResult, this);

        }
    }

    /**
     * Override this to transform the hit result before resolving.
     */
    public @Nullable HitResult transformHitResult(@Nullable HitResult hitResult) {
        if (hitResult instanceof BlockHitResult hitResult1) {
            return new BlockHitResult(hitResult1.getLocation(), hitResult1.getDirection(), hitResult1.getBlockPos(), false);
        }
        return hitResult;
    }

    public boolean canTraversePortals() {
        return canTraversePortals;
    }

    public int getExpirationTime() {
        return MethodProjectile.INSTANCE.getProjectileLifespan() * 20;
    }

    /**
     * Moves the projectile to the next position
     */
    public void tickNextPosition() {

        Vec3 vec3d = this.getDeltaMovement();
        double x = this.getX() + vec3d.x;
        double y = this.getY() + vec3d.y;
        double z = this.getZ() + vec3d.z;
        if (!this.isNoGravity()) {
            setDeltaMovement(vec3d.x * 0.96, (vec3d.y > 0 ? vec3d.y * 0.97 : vec3d.y) - 0.03f, vec3d.z * 0.96);
        }
        this.setPos(x, y, z);

        double yaw = Math.toDegrees(Mth.atan2(-vec3d.z, vec3d.x));
        // Calculate horizontal distance
        double horizontalDistance = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);

        // Calculate pitch
        double pitch = Math.toDegrees(Math.atan2(-vec3d.y, horizontalDistance));

//        setXRot((float) pitch);
//        setYRot((float) yaw);
//        setYHeadRot((float) yaw);
//        setYBodyRot((float) yaw);
    }


    public int getParticleDelay() {
        return 2;
    }

    public void playParticles() {
        if(trailEmitter == null && resolver() != null) {
            this.trailEmitter = new ParticleEmitter(() -> this.getPosition(0), this::getRotationVector, this.resolver().spell.particleTimeline().trailEffect);
        }
//        if(this.trailEmitter != null) {
//            //this.trailEmitter.tick(level);
//            this.trailEmitter.age++;
        double spiralRadius = 1f;  // Radius of the spiral
        double spiralSpeed = 1;  // Speed at which the particles move along the spiral
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double prevX = this.xOld;
        double prevY = this.yOld;
        double prevZ = this.zOld;
        double distance = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2) + Math.pow(z - prevZ, 2));
        int interpolationSteps = Math.max(1, (int) (distance / 0.1)); // Adjust 0.1 for step granularity
        for (int step = 0; step <= interpolationSteps; step++) {
            // Linear interpolation factor
            double t = (double) step / interpolationSteps;

            // Interpolated position between previous and current
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double interpolatedAge = age + t;
            double angle = interpolatedAge * spiralSpeed;
            float localX = (float) (Math.cos(angle) * spiralRadius);
            float localZ = 0;
            float localY = (float) (Math.sin(angle) * spiralRadius);
            Matrix4d transform = new Matrix4d()
                    .rotateX(Math.toRadians(xRot))
                    .rotateY(Math.toRadians(yRot));
            Vector3d worldPosition = new Vector3d();
            transform.transformPosition(new Vector3d(localX, localY, localZ), worldPosition);

            level.addParticle(ModParticles.CUSTOM_TYPE.get(), interpolatedX + worldPosition.x, interpolatedY + worldPosition.y, interpolatedZ + worldPosition.z, 0, 0, 0);
        }
      //  }
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level, this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f = -Mth.sin(rotationYawIn * ((float) Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float) Math.PI / 180F));
        float f1 = -Mth.sin((rotationPitchIn + pitchOffset) * ((float) Math.PI / 180F));
        float f2 = Mth.cos(rotationYawIn * ((float) Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float) Math.PI / 180F));
        this.shoot(f, f1, f2, velocity, inaccuracy);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to an x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vec3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy).scale(velocity);
        this.setDeltaMovement(vec3d);
        float f = Mth.sqrt((float) vec3d.horizontalDistanceSqr());
        this.yRot = (float) (Mth.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));
        this.xRot = (float) (Mth.atan2(vec3d.y, f) * (double) (180F / (float) Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;

    }

    @Override
    public void setRemoved(RemovalReason reason) {
        if (reason == RemovalReason.UNLOADED_TO_CHUNK)
            reason = RemovalReason.DISCARDED;
        super.setRemoved(reason);
    }

    public EntityProjectileSpell setGravity(boolean noGravity) {
        isNoGravity = !noGravity;
        return this;
    }

    @Override
    public boolean isNoGravity() {
        return isNoGravity;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SPELL_PROJ.get();
    }

    protected void attemptRemoval() {
        this.pierceLeft--;
        if (this.pierceLeft < 0) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public boolean canBounce() {
        return !isNoGravity() && pierceLeft > 0;
    }

    public void bounce(BlockHitResult blockHitResult) {
        Direction direction = blockHitResult.getDirection();
        float factor = -0.9F;
        // bounce off the block according to the face hit and reduce momentum
        switch (direction) {
            case UP, DOWN -> {
                Vec3 vel = getDeltaMovement();
                setDeltaMovement(vel.x(), factor * vel.y(), vel.z());
            }
            case EAST, WEST -> {
                Vec3 vel = getDeltaMovement();
                setDeltaMovement(factor * vel.x(), vel.y(), vel.z());
            }
            case NORTH, SOUTH -> {
                Vec3 vel = getDeltaMovement();
                setDeltaMovement(vel.x(), vel.y(), factor * vel.z());
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        result = transformHitResult(result);

        if (!level.isClientSide) {

            SpellProjectileHitEvent event = new SpellProjectileHitEvent(this, result);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }

            if (result instanceof EntityHitResult entityHitResult) {
                if (entityHitResult.getEntity().equals(this.getOwner())) return;
                if (this.resolver() != null) {
                    this.resolver().onResolveEffect(level, result);
                    Networking.sendToNearbyClient(level, BlockPos.containing(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                            BlockPos.containing(result.getLocation()), getParticleColor()));
                    attemptRemoval();
                }
            }

            if (result instanceof BlockHitResult blockraytraceresult && !this.isRemoved() && !hitList.contains(blockraytraceresult.getBlockPos())) {

                BlockState state = level.getBlockState(blockraytraceresult.getBlockPos());

                if (state.getBlock() instanceof IPrismaticBlock prismaticBlock) {
                    prismaticBlock.onHit((ServerLevel) level, blockraytraceresult.getBlockPos(), this);
                    return;
                }

                if (state.is(BlockTags.PORTALS)) {
                    state.entityInside(level, blockraytraceresult.getBlockPos(), this);
                    return;
                }

                if (state.getBlock() instanceof TargetBlock) {
                    this.onHitBlock(blockraytraceresult);
                }

                if (canBounce()) {
                    bounce(blockraytraceresult);
                    if (numSensitive > 1) {
                        pierceLeft--; //to replace with bounce field eventually, reduce here since we're not calling attemptRemoval
                        return;
                    }
                }

                if (this.resolver() != null) {
                    this.hitList.add(blockraytraceresult.getBlockPos());
                    this.resolver().onResolveEffect(this.level, blockraytraceresult);
                }
                Networking.sendToNearbyClient(level, ((BlockHitResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        BlockPos.containing(result.getLocation()).below(), getParticleColor()));
                attemptRemoval();
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.getType().is(EntityTags.SPELL_CAN_HIT);
    }

    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isNoGravity);
        buffer.writeInt(numSensitive);
    }

    public void readSpawnData(FriendlyByteBuf additionalData) {
        isNoGravity = additionalData.readBoolean();
        numSensitive = additionalData.readInt();
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level.getEntity(pPacket.getData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }

    @Override
    public void setOwner(@org.jetbrains.annotations.Nullable Entity pOwner) {
        super.setOwner(pOwner);
        if(pOwner != null) {
            this.entityData.set(OWNER_ID, pOwner.getId());
        }else{
            this.entityData.set(OWNER_ID, -1);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spellResolver = null;
        if (tag.contains("pierce")) {
            this.pierceLeft = tag.getInt("pierce");
        }
        isNoGravity = tag.getBoolean("gravity");
        this.entityData.set(OWNER_ID, tag.getInt("ownerId"));
        if(tag.contains("resolver")) {
            setResolver(ANCodecs.decode(SpellResolver.CODEC.codec(), tag.get("resolver")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("pierce", this.pierceLeft);
        tag.putBoolean("gravity", isNoGravity);
        tag.putInt("ownerId", this.entityData.get(OWNER_ID));
        if(this.resolver() != null) {
            tag.put("resolver", ANCodecs.encode(SpellResolver.CODEC.codec(), this.resolver()));
        }
    }

    @Override
    public Entity changeDimension(@NotNull DimensionTransition transition) {
        Entity changed = super.changeDimension(transition);
        if (!(changed instanceof EntityProjectileSpell spell)) {
            return changed;
        }
        spell.setResolver(this.resolver());
        spell.resolver().spellContext.level = transition.newLevel();
        spell.prismRedirect = this.prismRedirect;
        spell.age = this.age;
        spell.numSensitive = this.numSensitive;
        return changed;
    }
}
