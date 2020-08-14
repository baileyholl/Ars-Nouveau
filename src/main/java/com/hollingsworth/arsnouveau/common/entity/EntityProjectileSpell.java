package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.SpellResolver;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityProjectileSpell extends ArrowEntity {

    private int age;
    private SpellResolver spellResolver;
    public int xpColor;

    public EntityProjectileSpell(EntityType<? extends ArrowEntity> type, World worldIn, SpellResolver spellResolver) {
        super(type, worldIn);
        this.spellResolver = spellResolver;
        age = 0;
    }

    public EntityProjectileSpell(final EntityType<? extends EntityProjectileSpell> entityType, final World world) {
        super(entityType, world);
    }

    public EntityProjectileSpell(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public EntityProjectileSpell(final World world, final LivingEntity shooter) {
        super(world, shooter);
    }

    public void particles(){

        if(world.getGameTime() % 1 == 0)

        for(int i =0; i < 2; i++){
            double d0 = getPosX(); //+ world.rand.nextFloat();
            double d1 = getPosY();//+ world.rand.nextFloat() ;
            double d2 = getPosZ(); //+ world.rand.nextFloat();

            world.addParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2, 0.0, 0.0, 0.0);
//             d0 = getX() + world.rand.nextFloat();
//             d1 = getY() + world.rand.nextFloat() -1 ;
//             d2 = getZ() + world.rand.nextFloat();
            //world.addParticle(ParticleTypes.WITCH, d0, d1, d2, 0.1, 0.1, 0.1);
            //WispParticleData data = WispParticleData.wisp(0.25F + (float) Math.random() * 0.1F, (float) Math.random() * 0.25F, 0.75F + (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, 1);
           // world.addParticle(data, d0, d1 + 0.25, d2, 0, -(-0.075F - (float) Math.random() * 0.015F), 0);

          //  world.addParticle(WispParticleData.wisp(0.25F + (float) Math.random() * 0.1F, (float) Math.random() * 0.25F, 0.75F + (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, 1), d0, d1, d2, 0, 0, 0);
        }
////        for(int i =0; i < 1; i++){
//            double d0 = posX + world.rand.nextFloat();
//            double d1 = posY + world.rand.nextFloat();
//            double d2 = posZ + world.rand.nextFloat();
//            world.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.1, 0.1, 0.1);
////        }
    }

    @Override
    public void tick() {
        if(world.isRemote){
            particles();
        }

        this.lastTickPosX = this.getPosX();
        this.lastTickPosY = this.getPosY();
        this.lastTickPosZ = this.getPosZ();
        //super.tick();

        if (this.inGround) {
            this.inGround = false;
            this.setMotion(this.getMotion().mul((double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F)));
        }

        AxisAlignedBB axisalignedbb = this.getBoundingBox().expand(this.getMotion()).grow(1.0D);

        for(Entity entity : this.world.getEntitiesInAABBexcluding(this, axisalignedbb, (p_213881_0_) -> {
            return !p_213881_0_.isSpectator() && p_213881_0_.canBeCollidedWith();
        })) {

        }

        RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, axisalignedbb, (p_213880_1_) -> {
            return !p_213880_1_.isSpectator() && p_213880_1_.canBeCollidedWith();
        }, RayTraceContext.BlockMode.OUTLINE, true);


        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && this.world.getBlockState(((BlockRayTraceResult)raytraceresult).getPos()).getBlock() == Blocks.NETHER_PORTAL) {
                this.setPortal(((BlockRayTraceResult)raytraceresult).getPos());
            } else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)){
                this.onHit(raytraceresult);
            }
        }

        Vec3d vec3d = this.getMotion();
        double x = this.getPosX() +vec3d.x;
        double y = this.getPosY() + vec3d.y;
        double z = this.getPosZ() + vec3d.getZ();

        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));


        while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
        this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);


        if (!this.hasNoGravity()) {
            Vec3d vec3d1 = this.getMotion();
            this.setMotion(vec3d1.x, vec3d1.y , vec3d1.z);
        }

        this.setPosition(x,y,z);
    }

    public EntityProjectileSpell( World world, LivingEntity shooter, SpellResolver spellResolver) {
        super(world, shooter);
        this.spellResolver = spellResolver;
        age = 0;
    }


    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        Vec3d vec3d = entityThrower.getMotion();
        this.setMotion(this.getMotion().add(vec3d.x, entityThrower.onGround ? 0.0D : vec3d.y, vec3d.z));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
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

    @Override
    protected void onHit(RayTraceResult result) {
        //super.onHit(result);

        if(!world.isRemote && result.getType() == RayTraceResult.Type.ENTITY) {
            if (((EntityRayTraceResult) result).getEntity().equals(this.getShooter())) return;
            if(this.spellResolver != null && result != null) {
                this.spellResolver.onResolveEffect(world, (LivingEntity) this.getShooter(), (EntityRayTraceResult) result);
                this.world.setEntityState(this, (byte)3);
                this.remove();
            }

        }
        if(world.isRemote && result instanceof BlockRayTraceResult){
            SpellBook.spawnParticles(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z, world);
        }

        if (!world.isRemote && result instanceof BlockRayTraceResult) {
           if(!world.getBlockState(((BlockRayTraceResult) result).getPos()).getMaterial().isSolid())
               return;
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
//            BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());
//            System.out.println(blockstate.getBlock());
            if(this.spellResolver != null) {
                this.spellResolver.onResolveEffect(this.world, (LivingEntity) this.getShooter(), blockraytraceresult);
                System.out.println("Resolving");
            }
            this.world.setEntityState(this, (byte)3);
            this.remove();

        }
    }

    public EntityProjectileSpell(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.SPELL_PROJ, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
