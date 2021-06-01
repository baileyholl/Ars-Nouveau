package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySpellArrow extends ArrowEntity {
    public SpellResolver spellResolver;
    public int pierceLeft;
    BlockPos lastPosHit;
    Entity lastEntityHit;
    public static final DataParameter<Integer> RED = EntityDataManager.defineId(EntitySpellArrow.class, DataSerializers.INT);
    public static final DataParameter<Integer> GREEN = EntityDataManager.defineId(EntitySpellArrow.class, DataSerializers.INT);
    public static final DataParameter<Integer> BLUE = EntityDataManager.defineId(EntitySpellArrow.class, DataSerializers.INT);

    public EntitySpellArrow(EntityType<? extends ArrowEntity> type, World worldIn) {
        super(type, worldIn);
        setDefaultColors();
    }

    public EntitySpellArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setDefaultColors();
    }

    public EntitySpellArrow(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
        setDefaultColors();
    }

    public void setDefaultColors(){
        setColors(255, 25, 180);
    }

    public void setColors(int r, int g, int b){
        this.entityData.set(RED, r);
        this.entityData.set(GREEN, g);
        this.entityData.set(BLUE, b);
    }

    @Override
    public void tick() {
        boolean isNoClip = this.isNoPhysics();
        Vector3d vector3d = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
            this.yRot = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
            this.xRot = (float) (MathHelper.atan2(vector3d.y, (double) f) * (double) (180F / (float) Math.PI));
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
        Vector3d vector3d2 = this.position();
        Vector3d vector3d3 = vector3d2.add(vector3d);
        RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d3 = raytraceresult.getLocation();
        }

        while (!this.removed) {
            EntityRayTraceResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                raytraceresult = entityraytraceresult;
            }

            if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
                Entity entity = ((EntityRayTraceResult) raytraceresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity) entity1).canHarmPlayer((PlayerEntity) entity)) {
                    raytraceresult = null;
                    entityraytraceresult = null;
                }
            }

            if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !isNoClip && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
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
        float f1 = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
        if (isNoClip) {
            this.yRot = (float) (MathHelper.atan2(-d3, -d0) * (double) (180F / (float) Math.PI));
        } else {
            this.yRot = (float) (MathHelper.atan2(d3, d0) * (double) (180F / (float) Math.PI));
        }

        this.xRot = (float) (MathHelper.atan2(d4, (double) f1) * (double) (180F / (float) Math.PI));
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

        this.setDeltaMovement(vector3d.scale((double) f2));
        if (!this.isNoGravity() && !isNoClip) {
            Vector3d vector3d4 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d4.x, vector3d4.y - (double) 0.05F, vector3d4.z);
        }

        this.setPos(d5, d1, d2);
        this.checkInsideBlocks();

        if (level.isClientSide && tickCount > 1) {
            for (int i = 0; i < 10; i++) {
                double deltaX = getX() - xOld;
                double deltaY = getY() - yOld;
                double deltaZ = getZ() - zOld;
                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
                int counter = 0;

                for (double j = 0; j < dist; j++) {
                    double coeff = j / dist;
                    counter += level.random.nextInt(3);
                    if (counter % (Minecraft.getInstance().options.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles.getId()) == 0) {
                        level.addParticle(GlowParticleData.createData(new ParticleColor(entityData.get(RED), entityData.get(GREEN), entityData.get(BLUE))), (float) (xo + deltaX * coeff), (float) (yo + deltaY * coeff), (float) (zo + deltaZ * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));
                    }
                }
            }
        }
    }

    protected void attemptRemoval() {
        if (level.isClientSide)
            return;
        this.pierceLeft--;
        if (this.pierceLeft < 0) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.remove();
        }
    }

    @Override
    public byte getPierceLevel() {
        //Handle pierce on our end to account for blocks
        return (byte) 12;
    }

    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        Entity entity = p_213868_1_.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = MathHelper.ceil(MathHelper.clamp((double) f * this.getBaseDamage(), 0.0D, 2.147483647E9D));

        if (this.isCritArrow()) {
            long j = (long) this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastHurtMob(entity);
            }
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                if (this.knockback > 0) {
                    Vector3d vector3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) this.knockback * 0.6D);
                    if (vector3d.lengthSqr() > 0.0D) {
                        livingentity.push(vector3d.x, 0.1D, vector3d.z);
                    }
                }

                if (!this.level.isClientSide && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
                }

                this.doPostHurtEffects(livingentity);
            }
        } else {
            entity.setRemainingFireTicks(k);

        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RED, 0);
        this.entityData.define(GREEN, 0);
        this.entityData.define(BLUE, 0);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (this.spellResolver != null)
            this.spellResolver.onResolveEffect(level, (LivingEntity) this.getOwner(), result);
        RayTraceResult.Type raytraceresult$type = result.getType();
        LivingEntity shooter = getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null;
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            if (spellResolver != null) {
                spellResolver.onResolveEffect(level, shooter, result);
            }
            this.onHitEntity((EntityRayTraceResult) result);
            attemptRemoval();
            lastEntityHit = ((EntityRayTraceResult) result).getEntity();
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK && !((BlockRayTraceResult) result).getBlockPos().equals(lastPosHit)) {
            if (spellResolver != null) {
                spellResolver.onResolveEffect(level, shooter, result);
            }
            this.onHitBlock((BlockRayTraceResult) result);
            lastPosHit = ((BlockRayTraceResult) result).getBlockPos();
            attemptRemoval();
        }

    }

    @Override
    public boolean isNoPhysics() {
        return super.isNoPhysics();
    }

    protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_SPELL_ARROW;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntitySpellArrow(FMLPlayMessages.SpawnEntity packet, World world) {
        super(ModEntities.ENTITY_SPELL_ARROW, world);
    }
}
