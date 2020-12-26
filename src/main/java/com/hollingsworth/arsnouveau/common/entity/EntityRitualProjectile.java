package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityRitualProjectile extends ColoredProjectile{

    public BlockPos tilePos;

    public EntityRitualProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityRitualProjectile(EntityType<EntityRitualProjectile> entityAOEProjectileEntityType, World world) {
        super(entityAOEProjectileEntityType, world);
    }
    @Override
    public void tick() {
        if(!world.isRemote() && (tilePos == null || !(world.getTileEntity(tilePos) instanceof RitualTile) || ((RitualTile) world.getTileEntity(tilePos)).ritual == null )) {
            this.remove();
            System.out.println("removing");
            return;
        }


        lastTickPosX = getPosX();
        lastTickPosY = getPosY();
        lastTickPosZ = getPosZ();
      //  this.setPosition(Math.sin(world.getGameTime()/10D)/9d+ getPosX(), getPosY(), Math.cos(world.getGameTime()/10D)/9d + getPosZ());
       // this.setPosition(getpost);
        this.setPosition(getPosX(), getPosY() + Math.sin(world.getGameTime()/10D)/10, getPosZ());
        prevPosX = getPosX();
        prevPosY = getPosY();
        prevPosZ = getPosZ();

        System.out.println(this.getPositionVec());
        if(world.isRemote) {
            int counter = 0;
            for (double j = 0; j < 3; j++) {

                counter += world.rand.nextInt(3);
                if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
                    world.addParticle(ParticleSparkleData.createData(getParticleColor()),
                            (float) (getPositionVec().getX()) + Math.sin(world.getGameTime()/3D),
                            (float) (getPositionVec().getY()),
                            (float) (getPositionVec().getZ()) + Math.cos(world.getGameTime()/3D),
                            0.0225f * (rand.nextFloat() ), 0.0225f * (rand.nextFloat()), 0.0225f * (rand.nextFloat() ));
                }
            }
//
//            for (int i = 0; i < 10; i++) {
//
//                double deltaX = getPosX() - lastTickPosX;
//                double deltaY = getPosY() - lastTickPosY;
//                double deltaZ = getPosZ() - lastTickPosZ;
//                double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);
//                int counter = 0;
//
//                for (double j = 0; j < dist; j++) {
//                    double coeff = j / dist;
//                    counter += world.rand.nextInt(3);
//                    if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
//                        System.out.println(prevPosX + deltaX * coeff);
//                        world.addParticle(GlowParticleData.createData(getParticleColor()), (float) (prevPosX + deltaX * coeff), (float) (prevPosY + deltaY * coeff), (float) (prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));
//                    }
//                }
//            }
        }

//        float offsetY = sylph.getPositionY()/9f;
//        float roteAngle = propellers.getRotationY() / 4;
//
//        if(rand.nextInt(5) == 0){
//            for(int i =0; i < 5; i++){
//                world.addParticle(ParticleSparkleData.createData(new ParticleColor(52,255,36), 0.05f, 60),
//                        particlePos.getX()  + Math.cos(roteAngle)/2 , particlePos.getY() +0.5+ offsetY , particlePos.getZ()  + Math.sin(roteAngle)/2,
//                        0, 0,0);
//            }
//
//        }
    }





    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_RITUAL;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityRitualProjectile(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.ENTITY_RITUAL, world);
    }


}
