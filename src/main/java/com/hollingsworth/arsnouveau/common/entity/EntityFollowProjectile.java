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
    private static final DataParameter<BlockPos> to = EntityDataManager.createKey(ArrowEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<BlockPos> from = EntityDataManager.createKey(ArrowEntity.class, DataSerializers.BLOCK_POS);
    private int age;
//    int age;
    int maxAge;


    public EntityFollowProjectile(World worldIn, Vector3d from, Vector3d to) {
        this(ModEntities.ENTITY_FOLLOW_PROJ, worldIn);
        this.dataManager.set(EntityFollowProjectile.to, new BlockPos(to));
        this.dataManager.set(EntityFollowProjectile.from, new BlockPos(from));
//        this.age = 0;
        this.maxAge = (int) Math.floor(from.subtract(to).length() * 5);
        setPosition(from.x + 0.5, from.y+ 0.5, from.z+ 0.5);
    }
    public EntityFollowProjectile(World worldIn, BlockPos from, BlockPos to) {
        this(worldIn, new Vector3d(from.getX(), from.getY(), from.getZ()), new Vector3d(to.getX(), to.getY(), to.getZ()));
    }

    public EntityFollowProjectile(EntityType<EntityFollowProjectile> entityAOEProjectileEntityType, World world) {
        super(entityAOEProjectileEntityType, world);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(to,new BlockPos(0,0,0));
        this.dataManager.register(from,new BlockPos(0,0,0));
    }

    @Override
    public void tick() {
        this.age++;
        Vector3d vec3d2 = this.getMotion();
        BlockPos dest = this.dataManager.get(EntityFollowProjectile.to);
        if(BlockUtil.distanceFrom(this.getPosition(), dest) < 1 || this.age > 1000 || BlockUtil.distanceFrom(this.getPosition(), dest) > 10){
            this.remove();
            return;
        }
        double posX = getPosX();
        double posY = getPosY();
        double posZ = getPosZ();
        double motionX = this.getMotion().x;
        double motionY = this.getMotion().y;
        double motionZ = this.getMotion().z;

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
        this.setPosition(posX, posY, posZ);
        this.setMotion(motionX, motionY, motionZ);

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

        if(world.isRemote && this.age > 1) {
            double deltaX = getPosX() - lastTickPosX;
            double deltaY = getPosY() - lastTickPosY;
            double deltaZ = getPosZ() - lastTickPosZ;
            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
            int counter = 0;

            for (double i = 0; i < dist; i ++){
                double coeff = i/dist;
                counter += world.rand.nextInt(3);
                if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
                    world.addParticle(GlowParticleData.createData(new ParticleColor(255,25,180)),
                            (float) (prevPosX + deltaX * coeff), (float) (prevPosY + deltaY * coeff), (float) (prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));
                }
            }

        }
    }


    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(EntityFollowProjectile.from, NBTUtil.getBlockPos(compound, "from"));
        this.dataManager.set(EntityFollowProjectile.to, NBTUtil.getBlockPos(compound, "to"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if(from != null)
            NBTUtil.storeBlockPos(compound, "from",  this.dataManager.get(EntityFollowProjectile.from));
        if(to != null)
            NBTUtil.storeBlockPos(compound, "to",  this.dataManager.get(EntityFollowProjectile.to));
    }
    @Override
    public void baseTick() {
        super.baseTick();
    }
    @Override
    public IPacket<?> createSpawnPacket() {
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
    public boolean hasNoGravity() {
        return true;
    }
}
