package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityFollowProjectile extends ArrowEntity {
    public static final DataParameter<BlockPos> to = EntityDataManager.defineId(ArrowEntity.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<BlockPos> from = EntityDataManager.defineId(ArrowEntity.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<Integer> RED = EntityDataManager.defineId(EntityFollowProjectile.class, DataSerializers.INT);
    public static final DataParameter<Integer> GREEN = EntityDataManager.defineId(EntityFollowProjectile.class, DataSerializers.INT);
    public static final DataParameter<Integer> BLUE = EntityDataManager.defineId(EntityFollowProjectile.class, DataSerializers.INT);

    private int age;
//    int age;
    int maxAge = 500;

    public EntityFollowProjectile(World world){
        super(world, 0, 0,0);
    }


    public EntityFollowProjectile(World worldIn, Vector3d from, Vector3d to) {
        this(ModEntities.ENTITY_FOLLOW_PROJ, worldIn);
        this.entityData.set(EntityFollowProjectile.to, new BlockPos(to));
        this.entityData.set(EntityFollowProjectile.from, new BlockPos(from));
//        this.age = 0;
        setPos(from.x + 0.5, from.y+ 0.5, from.z+ 0.5);
        this.entityData.set(RED, 255);
        this.entityData.set(GREEN, 25);
        this.entityData.set(BLUE, 180);
    }

    public EntityFollowProjectile(World worldIn, BlockPos from, BlockPos to, int r, int g, int b) {
        this(worldIn, new Vector3d(from.getX(), from.getY(), from.getZ()), new Vector3d(to.getX(), to.getY(), to.getZ()));
        this.entityData.set(RED, r);
        this.entityData.set(GREEN, g);
        this.entityData.set(BLUE, b);
    }

    public EntityFollowProjectile(World worldIn, BlockPos from, BlockPos to) {
        this(worldIn, new Vector3d(from.getX(), from.getY(), from.getZ()), new Vector3d(to.getX(), to.getY(), to.getZ()));
    }

    public EntityFollowProjectile(EntityType<? extends EntityFollowProjectile> entityAOEProjectileEntityType, World world) {
        super(entityAOEProjectileEntityType, world);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(to,new BlockPos(0,0,0));
        this.entityData.define(from,new BlockPos(0,0,0));
        this.entityData.define(RED, 0);
        this.entityData.define(GREEN, 0);
        this.entityData.define(BLUE, 0);
    }


    @Override
    public void tick() {
        this.age++;
        if(age > maxAge) {
            this.remove();
            return;
        }
        Vector3d vec3d2 = this.getDeltaMovement();
        BlockPos dest = this.entityData.get(EntityFollowProjectile.to);
        if(BlockUtil.distanceFrom(this.blockPosition(), dest) < 1 || this.age > 1000 || BlockUtil.distanceFrom(this.blockPosition(), dest) > 10){
            this.remove();
            return;
        }
        double posX = getX();
        double posY = getY();
        double posZ = getZ();
        double motionX = this.getDeltaMovement().x;
        double motionY = this.getDeltaMovement().y;
        double motionZ = this.getDeltaMovement().z;

        if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0){
            double targetX = dest.getX()+0.5;
            double targetY = dest.getY()+0.5;
            double targetZ = dest.getZ()+0.5;
            Vector3d targetVector = new Vector3d(targetX-posX,targetY-posY,targetZ-posZ);
            double length = targetVector.length();
            targetVector = targetVector.scale(0.3/length);
            double weight  = 0;
            if (length <= 3){
                weight = 0.9*((3.0-length)/3.0);
            }

            motionX = (0.9-weight)*motionX+(0.1+weight)*targetVector.x;
            motionY = (0.9-weight)*motionY+(0.1+weight)*targetVector.y;
            motionZ = (0.9-weight)*motionZ+(0.1+weight)*targetVector.z;
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        this.setPos(posX, posY, posZ);
        this.setDeltaMovement(motionX, motionY, motionZ);

//        if (getEntityWorld().isRemote){
//            double deltaX = posX - prevPosX;
//            double deltaY = posY - prevPosY;
//            double deltaZ = posZ - prevPosZ;
//            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
//            for (double i = 0; i < dist; i ++){
//                double coeff = i/dist;
//                ParticleUtil.spawnParticleGlow(getEntityWorld(), (float)(prevPosX+ deltaX *coeff), (float)(prevPosY+ deltaY *coeff), (float)(prevPosZ+ deltaZ *coeff), 0.0125f*(rand.nextFloat()-0.5f), 0.0125f*(rand.nextFloat()-0.5f), 0.0125f*(rand.nextFloat()-0.5f), 255, 64, 16, 2.0f, 12);
//            }
//        }

        if(level.isClientSide && this.age > 1) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
            int counter = 0;

            for (double i = 0; i < dist; i ++){
                double coeff = i/dist;
                counter += level.random.nextInt(2);
                if (counter % (Minecraft.getInstance().options.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles.getId()) == 0) {
                    level.addParticle(GlowParticleData.createData(new ParticleColor(this.entityData.get(RED),this.entityData.get(GREEN),this.entityData.get(BLUE))),
                            (float) (xo + deltaX * coeff), (float) (yo + deltaY * coeff), (float) (zo + deltaZ * coeff), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f));
                }
            }

        }
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(EntityFollowProjectile.from, NBTUtil.getBlockPos(compound, "from"));
        this.entityData.set(EntityFollowProjectile.to, NBTUtil.getBlockPos(compound, "to"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if(from != null)
            NBTUtil.storeBlockPos(compound, "from",  this.entityData.get(EntityFollowProjectile.from));
        if(to != null)
            NBTUtil.storeBlockPos(compound, "to",  this.entityData.get(EntityFollowProjectile.to));
    }
    @Override
    public void baseTick() {
        super.baseTick();
    }
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityFollowProjectile(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.ENTITY_FOLLOW_PROJ, world);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FOLLOW_PROJ;
    }
    @Override
    public boolean isNoGravity() {
        return true;
    }
}
