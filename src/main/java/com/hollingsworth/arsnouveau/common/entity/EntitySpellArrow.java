package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.IPacket;
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

    public EntitySpellArrow(EntityType<? extends ArrowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public EntitySpellArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntitySpellArrow(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    @Override
    public void tick() {
        boolean isNoClip = this.getNoClip();
        Vector3d vector3d = this.getMotion();
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(horizontalMag(vector3d));
            this.rotationYaw = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
            this.rotationPitch = (float) (MathHelper.atan2(vector3d.y, (double) f) * (double) (180F / (float) Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = this.getPosition();
        BlockState blockstate = this.world.getBlockState(blockpos);


        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.isWet()) {
            this.extinguish();
        }


        this.timeInGround = 0;
        Vector3d vector3d2 = this.getPositionVec();
        Vector3d vector3d3 = vector3d2.add(vector3d);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d3 = raytraceresult.getHitVec();
        }

        while (!this.removed) {
            EntityRayTraceResult entityraytraceresult = this.rayTraceEntities(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                raytraceresult = entityraytraceresult;
            }

            if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
                Entity entity = ((EntityRayTraceResult) raytraceresult).getEntity();
                Entity entity1 = this.func_234616_v_();
                if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity) entity1).canAttackPlayer((PlayerEntity) entity)) {
                    raytraceresult = null;
                    entityraytraceresult = null;
                }
            }

            if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !isNoClip && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onImpact(raytraceresult);
                this.isAirBorne = true;
            }

            if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
                break;
            }

            raytraceresult = null;
        }

        vector3d = this.getMotion();
        double d3 = vector3d.x;
        double d4 = vector3d.y;
        double d0 = vector3d.z;
        if (this.getIsCritical()) {
            for (int i = 0; i < 4; ++i) {
                this.world.addParticle(ParticleTypes.CRIT, this.getPosX() + d3 * (double) i / 4.0D, this.getPosY() + d4 * (double) i / 4.0D, this.getPosZ() + d0 * (double) i / 4.0D, -d3, -d4 + 0.2D, -d0);
            }
        }

        double d5 = this.getPosX() + d3;
        double d1 = this.getPosY() + d4;
        double d2 = this.getPosZ() + d0;
        float f1 = MathHelper.sqrt(horizontalMag(vector3d));
        if (isNoClip) {
            this.rotationYaw = (float) (MathHelper.atan2(-d3, -d0) * (double) (180F / (float) Math.PI));
        } else {
            this.rotationYaw = (float) (MathHelper.atan2(d3, d0) * (double) (180F / (float) Math.PI));
        }

        this.rotationPitch = (float) (MathHelper.atan2(d4, (double) f1) * (double) (180F / (float) Math.PI));
        this.rotationPitch = func_234614_e_(this.prevRotationPitch, this.rotationPitch);
        this.rotationYaw = func_234614_e_(this.prevRotationYaw, this.rotationYaw);
        float f2 = 0.99F;
        float f3 = 0.05F;
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                float f4 = 0.25F;
                this.world.addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
            }

            f2 = this.getWaterDrag();
        }

        this.setMotion(vector3d.scale((double) f2));
        if (!this.hasNoGravity() && !isNoClip) {
            Vector3d vector3d4 = this.getMotion();
            this.setMotion(vector3d4.x, vector3d4.y - (double) 0.05F, vector3d4.z);
        }

        this.setPosition(d5, d1, d2);
        this.doBlockCollisions();

        if (world.isRemote && ticksExisted > 1) {
            for (int i = 0; i < 10; i++) {
                double deltaX = getPosX() - lastTickPosX;
                double deltaY = getPosY() - lastTickPosY;
                double deltaZ = getPosZ() - lastTickPosZ;
                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
                int counter = 0;

                for (double j = 0; j < dist; j++) {
                    double coeff = j / dist;
                    counter += world.rand.nextInt(3);
                    if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
                        world.addParticle(GlowParticleData.createData(ParticleUtil.defaultParticleColor()), (float) (prevPosX + deltaX * coeff), (float) (prevPosY + deltaY * coeff), (float) (prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));
                    }
                }
            }
        }
    }

    protected void attemptRemoval() {
        if (world.isRemote)
            return;
        this.pierceLeft--;
        if (this.pierceLeft < 0) {
            this.world.setEntityState(this, (byte) 3);
            this.remove();
        }
    }

    @Override
    public byte getPierceLevel() {
        //Handle pierce on our end to account for blocks
        return (byte) 12;
    }

    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        Entity entity = p_213868_1_.getEntity();
        float f = (float) this.getMotion().length();
        int i = MathHelper.ceil(MathHelper.clamp((double) f * this.getDamage(), 0.0D, 2.147483647E9D));

        if (this.getIsCritical()) {
            long j = (long) this.rand.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.func_234616_v_();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.causeArrowDamage(this, this);
        } else {
            damagesource = DamageSource.causeArrowDamage(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastAttackedEntity(entity);
            }
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getFireTimer();
        if (this.isBurning() && !flag) {
            entity.setFire(5);
        }

        if (entity.attackEntityFrom(damagesource, (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!this.world.isRemote && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCountInEntity(livingentity.getArrowCountInEntity() + 1);
                }

                if (this.knockbackStrength > 0) {
                    Vector3d vector3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double) this.knockbackStrength * 0.6D);
                    if (vector3d.lengthSquared() > 0.0D) {
                        livingentity.addVelocity(vector3d.x, 0.1D, vector3d.z);
                    }
                }

                if (!this.world.isRemote && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity) entity1, livingentity);
                }

                this.arrowHit(livingentity);
            }
        } else {
            entity.forceFireTicks(k);

        }

    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.spellResolver != null)
            this.spellResolver.onResolveEffect(world, (LivingEntity) this.func_234616_v_(), result);
        RayTraceResult.Type raytraceresult$type = result.getType();
        LivingEntity shooter = func_234616_v_() instanceof LivingEntity ? (LivingEntity) func_234616_v_() : null;
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            if (spellResolver != null) {
                spellResolver.onResolveEffect(world, shooter, result);
            }
            this.onEntityHit((EntityRayTraceResult) result);
            attemptRemoval();
            lastEntityHit = ((EntityRayTraceResult) result).getEntity();
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK && !((BlockRayTraceResult) result).getPos().equals(lastPosHit)) {
            if (spellResolver != null) {
                spellResolver.onResolveEffect(world, shooter, result);
            }
            this.func_230299_a_((BlockRayTraceResult) result);
            lastPosHit = ((BlockRayTraceResult) result).getPos();
            attemptRemoval();
        }

    }

    @Override
    public boolean getNoClip() {
        return super.getNoClip();
    }

    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.world.getBlockState(p_230299_1_.getPos());
        blockstate.onProjectileCollision(this.world, blockstate, p_230299_1_, this);
        this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_SPELL_ARROW;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntitySpellArrow(FMLPlayMessages.SpawnEntity packet, World world) {
        super(ModEntities.ENTITY_SPELL_ARROW, world);
    }
}
