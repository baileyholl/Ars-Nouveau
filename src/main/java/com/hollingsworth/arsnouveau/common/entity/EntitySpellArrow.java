package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.registry.DataSerializers;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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

import javax.annotation.Nullable;

public class EntitySpellArrow extends Arrow {
    @Deprecated
    public SpellResolver spellResolver;
    public int pierceLeft;
    BlockPos lastPosHit;
    Entity lastEntityHit;
    public static final EntityDataAccessor<Integer> RED = SynchedEntityData.defineId(EntitySpellArrow.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GREEN = SynchedEntityData.defineId(EntitySpellArrow.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE = SynchedEntityData.defineId(EntitySpellArrow.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<SpellResolver> SPELL_RESOLVER = SynchedEntityData.defineId(EntitySpellArrow.class, DataSerializers.SPELL_RESOLVER.get());
    ParticleEmitter trailEmitter;
    public EntitySpellArrow(EntityType<? extends Arrow> type, Level worldIn) {
        super(type, worldIn);
        setDefaultColors();
    }

    public EntitySpellArrow(Level worldIn, double x, double y, double z, ItemStack pPickupItemStack, @Nullable ItemStack p_345233_) {
        super(worldIn, x, y, z, pPickupItemStack, p_345233_);
        setDefaultColors();
    }

    public EntitySpellArrow(Level worldIn, LivingEntity shooter, ItemStack pPickupItemStack, @Nullable ItemStack weaponStack) {
        super(worldIn, shooter, pPickupItemStack, weaponStack);
        setDefaultColors();
    }


    public SpellResolver resolver(){
        return this.entityData.get(SPELL_RESOLVER);
    }

    public void setResolver(SpellResolver resolver){
        this.entityData.set(SPELL_RESOLVER, resolver);
        this.spellResolver = resolver;
        this.trailEmitter = new ParticleEmitter(() -> this.getPosition(0), this::getRotationVector, this.resolver().spell.particleTimeline().trailEffect);
    }

    public void setDefaultColors() {
        setColors(ParticleColor.defaultParticleColor());
    }

    public void setColors(ParticleColor color) {
        ParticleColor.IntWrapper wrapper = color.toWrapper();
        this.entityData.set(RED, wrapper.r);
        this.entityData.set(GREEN, wrapper.g);
        this.entityData.set(BLUE, wrapper.b);
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

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);


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

            if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !isNoClip && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, raytraceresult)) {
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
        float f3 = 0.05F;
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                float f4 = 0.25F;
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

        if (level.isClientSide && tickCount > 1) {
//
//            double deltaX = getX() - xOld;
//            double deltaY = getY() - yOld;
//            double deltaZ = getZ() - zOld;
//            double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
//            int counter = 0;
//
//            for (double j = 0; j < dist; j++) {
//                double coeff = j / dist;
//                counter += level.random.nextInt(3);
//                if (counter % (Minecraft.getInstance().options.particles().get().getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles().get().getId()) == 0) {
//                    level.addParticle(GlowParticleData.createData(new ParticleColor(entityData.get(RED), entityData.get(GREEN), entityData.get(BLUE))), (float) (xo + deltaX * coeff), (float) (yo + deltaY * coeff), (float) (zo + deltaZ * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));
//                }
//            }
            if(trailEmitter == null && resolver() != null) {
                this.trailEmitter = new ParticleEmitter(() -> this.getPosition(0), this::getRotationVector, this.resolver().spell.particleTimeline().trailEffect);
            }
            if(trailEmitter != null){
                trailEmitter.tick(level);
            }
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

    protected void onHitEntity(EntityHitResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        Entity entity = p_213868_1_.getEntity();
        float f = (float)this.getDeltaMovement().length();
        double d0 = this.getBaseDamage();
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().arrow(this, entity1 != null ? entity1 : this);
        if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverlevel) {
            d0 = (double)EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, (float)d0);
        }

        int j = Mth.ceil(Mth.clamp((double)f * d0, 0.0, 2.147483647E9));

        if (this.isCritArrow()) {
            long k = (long)this.random.nextInt(j / 2 + 2);
            j = (int)Math.min(k + (long)j, 2147483647L);
        }

        if (entity1 instanceof LivingEntity livingentity1) {
            livingentity1.setLastHurtMob(entity);
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int i = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.igniteForSeconds(5.0F);
        }

        if (entity.hurt(damagesource, (float)j)) {
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
        pBuilder.define(RED, 0);
        pBuilder.define(GREEN, 0);
        pBuilder.define(BLUE, 0);
        pBuilder.define(SPELL_RESOLVER, new SpellResolver(new SpellContext(level, new Spell(), null, null)));
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.spellResolver != null)
            this.spellResolver.onResolveEffect(level, result);
        HitResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == HitResult.Type.ENTITY) {
            if (spellResolver != null) {
                spellResolver.onResolveEffect(level, result);
            }
            this.onHitEntity((EntityHitResult) result);
            attemptRemoval();
            lastEntityHit = ((EntityHitResult) result).getEntity();
        } else if (raytraceresult$type == HitResult.Type.BLOCK && !((BlockHitResult) result).getBlockPos().equals(lastPosHit)) {
            if (spellResolver != null) {
                spellResolver.onResolveEffect(level, result);
            }
            this.onHitBlock((BlockHitResult) result);
            lastPosHit = ((BlockHitResult) result).getBlockPos();
            attemptRemoval();
        }

    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_SPELL_ARROW.get();
    }

}
