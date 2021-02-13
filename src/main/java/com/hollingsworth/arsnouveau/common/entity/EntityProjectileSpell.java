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


        Vector3d vector3d = this.getMotion();

        if(this.age > 60*20){
            this.remove();
            return;
        }


        this.lastTickPosX = this.getPosX();
        this.lastTickPosY = this.getPosY();
        this.lastTickPosZ = this.getPosZ();


        if (this.inGround) {
            this.inGround = false;
            this.setMotion(this.getMotion());
        }


        Vector3d vector3d2 = this.getPositionVec();
        Vector3d vector3d3 = vector3d2.add(vector3d);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d3 = raytraceresult.getHitVec();
        }
        EntityRayTraceResult entityraytraceresult = this.rayTraceEntities(vector3d2, vector3d3);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult != null && raytraceresult instanceof EntityRayTraceResult) {
            Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
            Entity entity1 = this.func_234616_v_();
            if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canAttackPlayer((PlayerEntity)entity)) {
                raytraceresult = null;
            }
        }

        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS  && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
            this.isAirBorne = true;
        }
        if(raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.MISS && raytraceresult instanceof BlockRayTraceResult){
            BlockRegistry.PORTAL_BLOCK.onProjectileCollision(world,world.getBlockState(new BlockPos(raytraceresult.getHitVec())),
                    (BlockRayTraceResult)raytraceresult, this );

        }



        Vector3d vec3d = this.getMotion();
        double x = this.getPosX() + vec3d.x;
        double y = this.getPosY() + vec3d.y;
        double z = this.getPosZ() + vec3d.z;


        if (!this.hasNoGravity()) {
            Vector3d vec3d1 = this.getMotion();
            this.setMotion(vec3d1.x, vec3d1.y , vec3d1.z);
        }

        this.setPosition(x,y,z);

        if(world.isRemote && this.age > 2) {
//
            for (int i = 0; i < 10; i++) {

                double deltaX = getPosX() - lastTickPosX;
                double deltaY = getPosY() - lastTickPosY;
                double deltaZ = getPosZ() - lastTickPosZ;
                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
                int counter = 0;

                for (double j = 0; j < dist; j++) {
                    double coeff = j / dist;
                    counter += world.rand.nextInt(3);

                    world.addParticle(GlowParticleData.createData(getParticleColor()), (float) (prevPosX + deltaX * coeff), (float) (prevPosY + deltaY * coeff), (float) (prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));

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
        Vector3d vec3d = entityThrower.getLookVec();
        this.setMotion(this.getMotion().add(vec3d.x, vec3d.y, vec3d.z));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        Vector3d vec3d = (new Vector3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SPELL_PROJ;
    }

    protected void attemptRemoval(){
        this.pierceLeft--;
        if(this.pierceLeft < 0){
            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(!world.isRemote &&  result != null && result.getType() == RayTraceResult.Type.ENTITY) {
            if (((EntityRayTraceResult) result).getEntity().equals(this.getShooter())) return;
            if(this.spellResolver != null) {
                this.spellResolver.onResolveEffect(world, (LivingEntity) this.getShooter(), result);
                Networking.sendToNearby(world, new BlockPos(result.getHitVec()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        new BlockPos(result.getHitVec()),getParticleColorWrapper()));
                attemptRemoval();
            }
        }

        if (!world.isRemote && result instanceof BlockRayTraceResult  && !this.removed) {

            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
            BlockState state = world.getBlockState(((BlockRayTraceResult) result).getPos());
            if(state.getMaterial() == Material.PORTAL){
                state.getBlock().onEntityCollision(state, world, ((BlockRayTraceResult) result).getPos(),this);
                return;
            }

            if(this.spellResolver != null) {
                this.spellResolver.onResolveEffect(this.world, (LivingEntity) this.getShooter(), blockraytraceresult);
            }
            Networking.sendToNearby(world, ((BlockRayTraceResult) result).getPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                    new BlockPos(result.getHitVec()).down(), getParticleColorWrapper()));
           attemptRemoval();
        }
    }
    public Entity getShooter(){
        return this.func_234616_v_();
    }


    public EntityProjectileSpell(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.SPELL_PROJ, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("pierce")){
            this.pierceLeft = tag.getInt("pierce");
        }
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt("pierce", this.pierceLeft);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }
}
