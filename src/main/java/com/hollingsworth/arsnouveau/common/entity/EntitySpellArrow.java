package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.DataSerializers;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

public class EntitySpellArrow extends Arrow {
    public int pierceLeft;
    BlockPos lastPosHit;
    public static final EntityDataAccessor<SpellResolver> SPELL_RESOLVER = SynchedEntityData.defineId(EntitySpellArrow.class, DataSerializers.SPELL_RESOLVER.get());
    public ParticleEmitter tickEmitter;
    public ParticleEmitter resolveEmitter;
    public ParticleEmitter onSpawnEmitter;
    public ParticleEmitter flairEmitter;
    public ConfiguredSpellSound castSound;
    public ConfiguredSpellSound resolveSound;
    protected boolean playedSpawnParticle;

    public EntitySpellArrow(EntityType<? extends Arrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntitySpellArrow(Level worldIn, double x, double y, double z, ItemStack pPickupItemStack, @Nullable ItemStack p_345233_) {
        super(worldIn, x, y, z, pPickupItemStack, p_345233_);
    }

    @Deprecated(forRemoval = true)
    public EntitySpellArrow(Level worldIn, LivingEntity shooter, ItemStack pPickupItemStack, @Nullable ItemStack weaponStack) {
        super(worldIn, shooter, pPickupItemStack, weaponStack);
    }

    public EntitySpellArrow(Level worldIn, LivingEntity shooter, ItemStack pPickupItemStack, @Nullable ItemStack weaponStack, @Nullable SpellResolver resolver) {
        super(worldIn, shooter, pPickupItemStack, weaponStack);
        if(resolver != null){
            this.setResolver(resolver);
        }
    }

    public SpellResolver resolver() {
        return this.entityData.get(SPELL_RESOLVER);
    }

    public void setResolver(SpellResolver resolver) {
        if (resolver.spellContext != null) {
            resolver.spellContext.level = this.level;
        }
        this.entityData.set(SPELL_RESOLVER, resolver);
        buildEmitters();
        setPierceLevel((byte) (getPierceLevel() + resolver.spell.getBuffsAtIndex(0, null, AugmentPierce.INSTANCE)));
    }

    public void buildEmitters() {
        TimelineMap timelineMap = this.resolver().spell.particleTimeline();
        ProjectileTimeline projectileTimeline = timelineMap.get(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
        TimelineEntryData trailConfig = projectileTimeline.trailEffect;
        TimelineEntryData resolveConfig = projectileTimeline.onResolvingEffect;
        TimelineEntryData spawnConfig = projectileTimeline.onSpawnEffect;
        TimelineEntryData flairConfig = projectileTimeline.flairEffect;

        this.tickEmitter = new ParticleEmitter(() -> this.getPosition(ClientInfo.partialTicks), this::getRotationVector, trailConfig);
        this.resolveEmitter = new ParticleEmitter(() -> this.getPosition(ClientInfo.partialTicks), this::getRotationVector, resolveConfig);
        this.onSpawnEmitter = new ParticleEmitter(() -> this.getPosition(ClientInfo.partialTicks), this::getRotationVector, spawnConfig);
        this.flairEmitter = new ParticleEmitter(() -> this.getPosition(ClientInfo.partialTicks), this::getRotationVector, flairConfig);

        Vec3 center = this.getDimensions(getPose()).makeBoundingBox(0, 0, 0).getCenter();
        this.tickEmitter.setPositionOffset(center);
        this.resolveEmitter.setPositionOffset(center);
        this.onSpawnEmitter.setPositionOffset(center);
        this.flairEmitter.setPositionOffset(center);

        this.castSound = projectileTimeline.castSound.sound;
        this.resolveSound = projectileTimeline.resolveSound.sound;
    }

    public void playParticles() {
        if (tickEmitter == null && resolver() != null) {
            buildEmitters();
        }
        if (this.tickEmitter != null) {
            this.tickEmitter.tick(level);
        }

        if (flairEmitter != null) {
            this.flairEmitter.tick(level);
        }

        if (!playedSpawnParticle && onSpawnEmitter != null) {
            this.onSpawnEmitter.tick(level);
            playedSpawnParticle = true;
        }
    }

    @Override
    public void tick() {
        // 1.21.1 Duplicate of super.tick but modified to omit grounding logic as our arrows support block piercing.
        boolean isNoClip = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)180.0F / (double)(float)Math.PI));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)180.0F / (double)(float)Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        // 1.21.1 This is intentionally left here as a way to quick compare the diff with Minecrafts AbstractArrow as it tends to update frequently.

//        if (!blockstate.isAir() && !isNoClip) {
//            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
//            if (!voxelshape.isEmpty()) {
//                Vec3 vec31 = this.position();
//
//                for(AABB aabb : voxelshape.toAabbs()) {
//                    if (aabb.move(blockpos).contains(vec31)) {
//                        this.inGround = true;
//                        break;
//                    }
//                }
//            }
//        }
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        // 1.21.1 Comment left intentionally to preserve diff with super method

//        if (this.inGround && !isNoClip) {
//            if (this.lastState != blockstate && this.shouldFall()) {
//                this.startFalling();
//            } else if (!this.level().isClientSide) {
//                this.tickDespawn();
//            }
//
//            ++this.inGroundTime;
//        } else {

        this.inGroundTime = 0;
        Vec3 vector3d2 = this.position();
        Vec3 vector3d3 = vector3d2.add(vec3);
        HitResult hitresult = this.level.clip(new ClipContext(vector3d2, vector3d3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vector3d3 = hitresult.getLocation();
        }

        while (!this.isRemoved()) {
            EntityHitResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                hitresult = entityraytraceresult;
            }

            if (hitresult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                Entity entity1 = this.getOwner();
                if (entity.noPhysics) {
                    hitresult = null;
                    entityraytraceresult = null;
                } else if (entity instanceof Player player1 && entity1 instanceof Player player2 && !player2.canHarmPlayer(player1)) {
                    hitresult = null;
                    entityraytraceresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !isNoClip) {
                if (EventHooks.onProjectileImpact(this, hitresult)) {
                    break;
                }

                ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(hitresult);
                this.hasImpulse = true;
                if (projectiledeflection != ProjectileDeflection.NONE) {
                    break;
                }
            }

            if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
                break;
            }

            hitresult = null;
        }
        vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;
        if (this.isCritArrow()) {
            for(int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.CRIT, this.getX() + d5 * (double)i / (double)4.0F, this.getY() + d6 * (double)i / (double)4.0F, this.getZ() + d1 * (double)i / (double)4.0F, -d5, -d6 + 0.2, -d1);
            }
        }

        double d7 = this.getX() + d5;
        double d2 = this.getY() + d6;
        double d3 = this.getZ() + d1;
        double d4 = vec3.horizontalDistance();
        if (isNoClip) {
            this.setYRot((float)(Mth.atan2(-d5, -d1) * (double)180.0F / (double)(float)Math.PI));
        } else {
            this.setYRot((float)(Mth.atan2(d5, d1) * (double)180.0F / (double)(float)Math.PI));
        }

        this.setXRot((float)(Mth.atan2(d6, d4) * (double)180.0F / (double)(float)Math.PI));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
        float f = 0.99F;
        if (this.isInWater()) {
            for(int j = 0; j < 4; ++j) {
                float f1 = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * f1, d2 - d6 * f1, d3 - d1 * f1, d5, d6, d1);
            }

            f = this.getWaterInertia();
        }

        this.setDeltaMovement(vec3.scale(f));
        if (!isNoClip) {
            this.applyGravity();
        }

        this.setPos(d7, d2, d3);
        this.checkInsideBlocks();

        if (level.isClientSide) {
            playParticles();
        }
        if (!level.isClientSide && tickCount == 1 && castSound != null) {
            castSound.playSound(level, position);
        }
    }

    protected void attemptRemoval() {
        if (level.isClientSide)
            return;

        this.setPierceLevel((byte) (this.getPierceLevel() - 1));
        if (this.getPierceLevel() < 0) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("resolver")) {
            setResolver(SpellResolver.rehydratedFromTag(tag.getCompound("resolver"), (ServerLevel) level));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.resolver() != null) {
            tag.put("resolver", ANCodecs.encode(SpellResolver.CODEC.codec(), this.resolver()));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SPELL_RESOLVER, new SpellResolver(new SpellContext(level, new Spell(), null, null)));
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        this.playResolve();
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == HitResult.Type.ENTITY) {
            if (resolver() != null) {
                resolver().onResolveEffect(level, result);
            }
            this.onHitEntity((EntityHitResult) result);
            attemptRemoval();
        } else if (result instanceof BlockHitResult blockHitResult && !(blockHitResult.getBlockPos().equals(lastPosHit))) {
            this.onHitBlock(blockHitResult);
            lastPosHit = blockHitResult.getBlockPos().immutable();
            attemptRemoval();
        }

    }

    protected void onHitBlock(BlockHitResult result) {
        if (resolver() != null) {
            resolver().onResolveEffect(level, result);
        }
        BlockState blockstate = this.level.getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, result, this);
        playResolve();

        // 1.21.1 Copy of super.onHitBlock with subtractions left as comments
        Vec3 vec3 = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        ItemStack itemstack = this.getWeaponItem();
        if (this.level() instanceof ServerLevel serverlevel && itemstack != null) {
            this.hitBlockEnchantmentEffects(serverlevel, result, itemstack);
        }

        Vec3 vec31 = vec3.normalize().scale(0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
//        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
//        this.inGround = true;
//        this.shakeTime = 7;
//        this.setCritArrow(false);
//        this.setPierceLevel((byte)0);
//        this.setSoundEvent(SoundEvents.ARROW_HIT);
//        this.resetPiercedEntities();
    }

    public void playResolve() {
        this.playSound(this.getDefaultHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if (this.resolveEmitter != null) {
            this.resolveEmitter.tick(level);
        }
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        if (this.resolveSound != null) {
            return this.resolveSound.getSound().getSoundEvent().value();
        }
        return super.getDefaultHitGroundSoundEvent();
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_SPELL_ARROW.get();
    }

}
