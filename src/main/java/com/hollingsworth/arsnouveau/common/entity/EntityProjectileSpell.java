package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityProjectileSpell extends ColoredProjectile {

    public int age;
    private SpellResolver spellResolver;
    public int pierceLeft;

    public EntityProjectileSpell(EntityType<? extends ArrowEntity> type, World worldIn, SpellResolver spellResolver, int pierceLeft) {
        super(type, worldIn);
        this.spellResolver = spellResolver;
        age = 0;
        this.pierceLeft = pierceLeft;
    }

    public EntityProjectileSpell(final EntityType<? extends EntityProjectileSpell> entityType, final World world) {
        super(entityType, world);

    }

    public EntityProjectileSpell(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public EntityProjectileSpell(World world, LivingEntity shooter, SpellResolver spellResolver, int maxPierce) {
        super(world, shooter);
        this.spellResolver = spellResolver;
        age = 0;
        pierceLeft = maxPierce;
    }

    public EntityProjectileSpell(final World world, final LivingEntity shooter) {
        super(world, shooter);
    }

    @Override
    public void tick() {
        age++;


        Vector3d vector3d = this.getDeltaMovement();

        if(this.age > 60*20){
            this.remove();
            return;
        }


        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();


        if (this.inGround) {
            this.inGround = false;
            this.setDeltaMovement(this.getDeltaMovement());
        }


        Vector3d vector3d2 = this.position();
        Vector3d vector3d3 = vector3d2.add(vector3d);
        RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d3 = raytraceresult.getLocation();
        }
        EntityRayTraceResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult != null && raytraceresult instanceof EntityRayTraceResult) {
            Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
            Entity entity1 = this.getOwner();
            if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canHarmPlayer((PlayerEntity)entity)) {
                raytraceresult = null;
            }
        }

        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS  && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }
        if(raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.MISS && raytraceresult instanceof BlockRayTraceResult){
            BlockRegistry.PORTAL_BLOCK.onProjectileHit(level,level.getBlockState(new BlockPos(raytraceresult.getLocation())),
                    (BlockRayTraceResult)raytraceresult, this );

        }



        Vector3d vec3d = this.getDeltaMovement();
        double x = this.getX() + vec3d.x;
        double y = this.getY() + vec3d.y;
        double z = this.getZ() + vec3d.z;


        if (!this.isNoGravity()) {
            Vector3d vec3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vec3d1.x, vec3d1.y , vec3d1.z);
        }

        this.setPos(x,y,z);

        if(level.isClientSide && this.age > 2) {
//
            for (int i = 0; i < 10; i++) {

                double deltaX = getX() - xOld;
                double deltaY = getY() - yOld;
                double deltaZ = getZ() - zOld;
                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
                int counter = 0;

                for (double j = 0; j < dist; j++) {
                    double coeff = j / dist;
                    counter += level.random.nextInt(3);

                    level.addParticle(GlowParticleData.createData(getParticleColor()), (float) (xo + deltaX * coeff), (float) (yo + deltaY * coeff), (float) (zo + deltaZ * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));

                }
            }
        }
    }


    @Override
    public void baseTick() {
        super.baseTick();
    }



    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vector3d vec3d = entityThrower.getLookAngle();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, vec3d.y, vec3d.z));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        Vector3d vec3d = (new Vector3d(x, y, z)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
        this.setDeltaMovement(vec3d);
        float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
        this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SPELL_PROJ;
    }

    protected void attemptRemoval(){
        this.pierceLeft--;
        if(this.pierceLeft < 0){
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if(!level.isClientSide &&  result != null && result.getType() == RayTraceResult.Type.ENTITY) {
            if (((EntityRayTraceResult) result).getEntity().equals(this.getShooter())) return;
            if(this.spellResolver != null) {
                this.spellResolver.onResolveEffect(level, (LivingEntity) this.getShooter(), result);
                Networking.sendToNearby(level, new BlockPos(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        new BlockPos(result.getLocation()),getParticleColorWrapper()));
                attemptRemoval();
            }
        }

        if (!level.isClientSide && result instanceof BlockRayTraceResult  && !this.removed) {

            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
            BlockState state = level.getBlockState(((BlockRayTraceResult) result).getBlockPos());
            if(state.getMaterial() == Material.PORTAL){
                state.getBlock().entityInside(state, level, ((BlockRayTraceResult) result).getBlockPos(),this);
                return;
            }

            if(this.spellResolver != null) {
                this.spellResolver.onResolveEffect(this.level, (LivingEntity) this.getShooter(), blockraytraceresult);
            }
            Networking.sendToNearby(level, ((BlockRayTraceResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                    new BlockPos(result.getLocation()).below(), getParticleColorWrapper()));
           attemptRemoval();
        }
    }
    public Entity getShooter(){
        return this.getOwner();
    }


    public EntityProjectileSpell(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.SPELL_PROJ, world);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("pierce")){
            this.pierceLeft = tag.getInt("pierce");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("pierce", this.pierceLeft);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
}
