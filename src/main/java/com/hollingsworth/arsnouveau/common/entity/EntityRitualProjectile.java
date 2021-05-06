package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
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

    public EntityRitualProjectile(World worldIn, BlockPos pos) {
        super(worldIn, pos.getX(), pos.getY(), pos.getZ());
    }

    public EntityRitualProjectile(EntityType<EntityRitualProjectile> entityAOEProjectileEntityType, World world) {
        super(entityAOEProjectileEntityType, world);
    }
    @Override
    public void tick() {
        if(!level.isClientSide() && (tilePos == null || !(level.getBlockEntity(tilePos) instanceof RitualTile) || ((RitualTile) level.getBlockEntity(tilePos)).ritual == null )) {
            this.remove();
            return;
        }


        xOld = getX();
        yOld = getY();
        zOld = getZ();
      //  this.setPosition(Math.sin(world.getGameTime()/10D)/9d+ getPosX(), getPosY(), Math.cos(world.getGameTime()/10D)/9d + getPosZ());
       // this.setPosition(getpost);
        this.setPos(getX(), getY() + Math.sin(level.getGameTime()/10D)/10, getZ());
        xo = getX();
        yo = getY();
        zo = getZ();


        if(level.isClientSide) {
            int counter = 0;
            for (double j = 0; j < 3; j++) {

                counter += level.random.nextInt(3);
                if (counter % (Minecraft.getInstance().options.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles.getId()) == 0) {
                    level.addParticle(ParticleSparkleData.createData(getParticleColor()),
                            (float) (position().x()) + Math.sin(level.getGameTime()/3D),
                            (float) (position().y()),
                            (float) (position().z()) + Math.cos(level.getGameTime()/3D),
                            0.0225f * (random.nextFloat() ), 0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat() ));
                }
            }

            for (double j = 0; j < 3; j++) {

                counter += level.random.nextInt(3);
                if (counter % (Minecraft.getInstance().options.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().options.particles.getId()) == 0) {
                    level.addParticle(ParticleSparkleData.createData(new ParticleColor(2, 0, 144)),
                            (float) (position().x()) - Math.sin(level.getGameTime()/3D),
                            (float) (position().y()),
                            (float) (position().z()) - Math.cos(level.getGameTime()/3D),
                            0.0225f * (random.nextFloat() ), 0.0225f * (random.nextFloat()), 0.0225f * (random.nextFloat() ));
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityRitualProjectile(FMLPlayMessages.SpawnEntity packet, World world){
        super(ModEntities.ENTITY_RITUAL, world);
    }

    @Override
    public boolean save(CompoundNBT tag) {
        if(tilePos != null)
            tag.put("ritpos", NBTUtil.writeBlockPos(tilePos));
        return super.save(tag);
    }

    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
        if(compound.contains("ritpos")){
            tilePos = NBTUtil.readBlockPos(compound.getCompound("ritpos"));
        }
    }
}
