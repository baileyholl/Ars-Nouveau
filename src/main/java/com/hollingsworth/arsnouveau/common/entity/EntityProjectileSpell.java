package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.block.SpellPrismBlock;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityProjectileSpell extends ColoredProjectile {

    public int age;
    public SpellResolver spellResolver;
    public int pierceLeft;
    public int numSensitive;
    public boolean isNoGravity = true;
    public boolean canTraversePortals = true;
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
        this.spellResolver = resolver;
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

    @Override
    public void tick() {
        super.tick();
        age++;

        if (this.age > getExpirationTime() || (!level.isClientSide && spellResolver == null)) {
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

        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }
        if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.MISS && raytraceresult instanceof BlockHitResult
                && canTraversePortals()) {
            BlockRegistry.PORTAL_BLOCK.onProjectileHit(level, level.getBlockState(new BlockPos(raytraceresult.getLocation())),
                    (BlockHitResult) raytraceresult, this);

        }
    }

    public boolean canTraversePortals() {
        return canTraversePortals;
    }

    public int getExpirationTime() {
        return expireTime;
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
            Vec3 vec3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vec3d1.x, vec3d1.y, vec3d1.z);
        }
        this.setPos(x, y, z);
    }

    public int getParticleDelay() {
        return 2;
    }

    public void playParticles() {
        for (int i = 0; i < 1; i++) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
            for (double j = 0; j < dist; j++) {
                double coeff = j / dist;
                level.addParticle(GlowParticleData.createData(getParticleColor()),
                        (float) (xo + deltaX * coeff),
                        (float) (yo + deltaY * coeff) + 0.1, (float)
                                (zo + deltaZ * coeff),
                        0.0125f * (random.nextFloat() - 0.5f),
                        0.0125f * (random.nextFloat() - 0.5f),
                        0.0125f * (random.nextFloat() - 0.5f));
            }
        }
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
        this.shoot(f, f1, f2, 0.0F, inaccuracy); //overriding this, a better solution might exists
        Vec3 vec3d = entityThrower.getLookAngle();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, vec3d.y, vec3d.z).scale(velocity));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
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

    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide && result instanceof EntityHitResult) {
            if (((EntityHitResult) result).getEntity().equals(this.getOwner())) return;
            if (this.spellResolver != null) {
                this.spellResolver.onResolveEffect(level, result);
                Networking.sendToNearby(level, new BlockPos(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        new BlockPos(result.getLocation()), getParticleColorWrapper()));
                attemptRemoval();
            }
        }

        if (!level.isClientSide && result instanceof BlockHitResult blockraytraceresult && !this.isRemoved() && !hitList.contains(((BlockHitResult) result).getBlockPos())) {

            BlockState state = level.getBlockState(((BlockHitResult) result).getBlockPos());

            if (state.getBlock() instanceof SpellPrismBlock) {
                SpellPrismBlock.redirectSpell((ServerLevel) level, blockraytraceresult.getBlockPos(), this);
                return;
            }


            if (state.getMaterial() == Material.PORTAL) {
                state.getBlock().entityInside(state, level, ((BlockHitResult) result).getBlockPos(), this);
                return;
            }

            if (this.spellResolver != null) {
                this.hitList.add(blockraytraceresult.getBlockPos());
                this.spellResolver.onResolveEffect(this.level, blockraytraceresult);
            }
            Networking.sendToNearby(level, ((BlockHitResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                    new BlockPos(result.getLocation()).below(), getParticleColorWrapper()));
            attemptRemoval();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.getType().is(EntityTags.SPELL_CAN_HIT);
    }

    public EntityProjectileSpell(PlayMessages.SpawnEntity packet, Level world) {
        super(ModEntities.SPELL_PROJ.get(), world);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level.getEntity(pPacket.getData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("pierce")) {
            this.pierceLeft = tag.getInt("pierce");
        }
        isNoGravity = tag.getBoolean("gravity");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("pierce", this.pierceLeft);
        tag.putBoolean("gravity", isNoGravity);
    }
}
