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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

public class EntitySpellArrow extends Arrow {
    public int pierceLeft;
    BlockPos lastPosHit;
    Entity lastEntityHit;
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

    public EntitySpellArrow(Level worldIn, LivingEntity shooter, ItemStack pPickupItemStack, @Nullable ItemStack weaponStack) {
        super(worldIn, shooter, pPickupItemStack, weaponStack);
    }


    public SpellResolver resolver() {
        return this.entityData.get(SPELL_RESOLVER);
    }

    public void setResolver(SpellResolver resolver) {
        this.entityData.set(SPELL_RESOLVER, resolver);
        buildEmitters();
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
        boolean isNoClip = this.isNoPhysics();
        Vec3 vector3d = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
            this.yRot = (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
            this.xRot = (float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI));
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain()) {
            this.clearFire();
        }


        this.inGroundTime = 0;
        Vec3 vector3d2 = this.position();
        Vec3 vector3d3 = vector3d2.add(vector3d);
        HitResult raytraceresult = this.level.clip(new ClipContext(vector3d2, vector3d3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (raytraceresult.getType() != HitResult.Type.MISS) {
            vector3d3 = raytraceresult.getLocation();
        }

        while (!this.isRemoved()) {
            EntityHitResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                raytraceresult = entityraytraceresult;
            }

            if (raytraceresult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                Entity entity1 = this.getOwner();
                if (entity.noPhysics) {
                    raytraceresult = null;
                    entityraytraceresult = null;
                } else if (entity instanceof Player player1 && entity1 instanceof Player player2 && !player2.canHarmPlayer(player1)) {
                    raytraceresult = null;
                    entityraytraceresult = null;
                }
            }

            if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !isNoClip && !EventHooks.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
                this.hasImpulse = true;
            }

            if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
                break;
            }

            raytraceresult = null;
        }

        vector3d = this.getDeltaMovement();
        double d3 = vector3d.x;
        double d4 = vector3d.y;
        double d0 = vector3d.z;
        if (this.isCritArrow()) {
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ParticleTypes.CRIT, this.getX() + d3 * (double) i / 4.0D, this.getY() + d4 * (double) i / 4.0D, this.getZ() + d0 * (double) i / 4.0D, -d3, -d4 + 0.2D, -d0);
            }
        }

        double d5 = this.getX() + d3;
        double d1 = this.getY() + d4;
        double d2 = this.getZ() + d0;
        float f1 = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
        if (isNoClip) {
            this.yRot = (float) (Mth.atan2(-d3, -d0) * (double) (180F / (float) Math.PI));
        } else {
            this.yRot = (float) (Mth.atan2(d3, d0) * (double) (180F / (float) Math.PI));
        }

        this.xRot = (float) (Mth.atan2(d4, f1) * (double) (180F / (float) Math.PI));
        this.xRot = lerpRotation(this.xRotO, this.xRot);
        this.yRot = lerpRotation(this.yRotO, this.yRot);
        float f2 = 0.99F;
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.level.addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
            }

            f2 = this.getWaterInertia();
        }

        this.setDeltaMovement(vector3d.scale(f2));
        if (!this.isNoGravity() && !isNoClip) {
            Vec3 vector3d4 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d4.x, vector3d4.y - (double) 0.05F, vector3d4.z);
        }

        this.setPos(d5, d1, d2);
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
        this.pierceLeft--;
        if (this.pierceLeft < 0) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public byte getPierceLevel() {
        //Handle pierce on our end to account for blocks
        return (byte) 12;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("resolver")) {
            setResolver(ANCodecs.decode(SpellResolver.CODEC.codec(), tag.get("resolver")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.resolver() != null) {
            tag.put("resolver", ANCodecs.encode(SpellResolver.CODEC.codec(), this.resolver()));
        }
    }

    protected void onHitEntity(EntityHitResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        Entity entity = p_213868_1_.getEntity();
        float f = (float) this.getDeltaMovement().length();
        double d0 = this.getBaseDamage();
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().arrow(this, entity1 != null ? entity1 : this);
        if (level instanceof ServerLevel serverlevel) {
            d0 = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, (float) d0);
        }

        int j = Mth.ceil(Mth.clamp((double) f * d0, 0.0, 2.147483647E9));

        if (this.isCritArrow()) {
            long k = (long) this.random.nextInt(j / 2 + 2);
            j = (int) Math.min(k + (long) j, 2147483647L);
        }

        if (entity1 instanceof LivingEntity livingentity1) {
            livingentity1.setLastHurtMob(entity);
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int i = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.igniteForSeconds(5.0F);
        }

        if (entity.hurt(damagesource, (float) j)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                this.doKnockback(livingentity, damagesource);
                if (this.level() instanceof ServerLevel serverlevel1) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, livingentity, damagesource, this.getWeaponItem());
                }

                this.doPostHurtEffects(livingentity);
            }
        } else {
            entity.setRemainingFireTicks(i);
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
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
        if (this.resolver() != null)
            this.resolver().onResolveEffect(level, result);
        HitResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == HitResult.Type.ENTITY) {
            if (resolver() != null) {
                resolver().onResolveEffect(level, result);
            }
            this.onHitEntity((EntityHitResult) result);
            attemptRemoval();
            lastEntityHit = ((EntityHitResult) result).getEntity();
        } else if (raytraceresult$type == HitResult.Type.BLOCK && !((BlockHitResult) result).getBlockPos().equals(lastPosHit)) {
            if (resolver() != null) {
                resolver().onResolveEffect(level, result);
            }
            this.onHitBlock((BlockHitResult) result);
            lastPosHit = ((BlockHitResult) result).getBlockPos();
            attemptRemoval();
        }

    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        playResolve();
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
