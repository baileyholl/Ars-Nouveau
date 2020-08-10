package com.hollingsworth.arsnouveau.client.particle;


import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static java.lang.Math.*;

/**
 * Created by Bailey on 12/26/2016.
 */
public class ParticleUtil {

    public static void fallingLightParticles(World world, BlockPos pos){
        double posX = getCenterOfBlock(pos.getX());
        double posY = pos.getY();
        double posZ = getCenterOfBlock(pos.getZ());

        float dist = 1.0F;
        for(int i = 0; i < 10; i++) {
            float xs = (float) (Math.random() - 0.5) * dist;
            float ys = (float) (Math.random() + .8) * dist;
            float zs = (float) (Math.random() - 0.5) * dist;
            //worldObj.spawnParticle(EnumParticleTypes.END_ROD, posX + xs, posY + ys, posZ + zs, 1F, 0.4F, 1F);
            world.addParticle(ParticleTypes.END_ROD, posX + xs, posY + ys, posZ + zs, 0F, 0.0F, 0F);
            world.addParticle(ParticleTypes.WITCH, posX + xs, posY + ys, posZ+ zs, 0F, 0.0F, 0F);
            world.addParticle(ParticleTypes.CRIT, posX, posY + ys, posZ, 0F, 0.0F, 0F);
        }
    }
    public static void requestLightParticles(World world, BlockPos pos){
        double posX = getCenterOfBlock(pos.getX());
        double posY = pos.getY();
        double posZ = getCenterOfBlock(pos.getZ());

        float dist = 2.0F;
        for(int i = 0; i < 10; i++) {
            float xs = (float) (Math.random() - 0.5);
            float ys = (float) (Math.random() + .8) * dist;
            float zs = (float) (Math.random() - 0.5);
            //worldObj.spawnParticle(EnumParticleTypes.END_ROD, posX + xs, posY + ys, posZ + zs, 1F, 0.4F, 1F);
            //world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, posX + xs, posY + ys, posZ + zs, 0F, 0.0F, 0F);
            //world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY + ys, posZ, 0F, 0.0F, 0F);
        }
        world.addParticle(ParticleTypes.SPIT, posX, posY, posZ , 0F, 0.1F, 0F);
    }

    public static double getCenterOfBlock(int a){
        return (a + .5);
    }

    public static void beam(BlockPos toThisBlock, BlockPos fromThisBlock, World world){
        Random rand = new Random();
        double x2 = getCenterOfBlock(toThisBlock.getX());
        double z2 = getCenterOfBlock(toThisBlock.getZ());
        double y2 = getCenterOfBlock(toThisBlock.getY());
        double x1 = getCenterOfBlock(fromThisBlock.getX());
        double z1 = getCenterOfBlock(fromThisBlock.getZ());
        double y1 = getCenterOfBlock(fromThisBlock.getY());
        double d5 = 1.0;
        double d0 = x2 - x1;
        double y = toThisBlock.getY() - fromThisBlock.getY();
        double d1 = y2 - y1;
        double d2 = z2 - z1;
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;

        double d4 = rand.nextDouble();

        while ((d4 + .65) < d3)
        {
            d4 += 1.8D - d5 + rand.nextDouble() * (1.7D - d5);
            world.addParticle(ParticleTypes.ENCHANT, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D);
        }
    }

//    public static void helix(BlockPos pos, World world, ParticleType particle) {
//        double radius = .5;
//        for(double y = 0; y <= 50; y+=0.05) {
//            double x = radius * cos(y);
//            double z = radius * sin(y);
//            world.addParticle(particle, getCenterOfBlock(pos.getX()) - .5+ x, pos.getY() + 1.5 + y, getCenterOfBlock(pos.getZ()) + z, 0, 0 ,0);
//        }
//    }

    public static void startEffect(BlockPos pos, World world) {
        double radius = 1.5;
        double speed = PI /32;
        double particlesPerCircle = 16;
        // Color firstColor = Color.BLUE;
        //  Color secondColor = Color.WHITE;
        //  Color thirdColor = Color.RED;
        double step = 0;
        step += speed;
        for (double i = 0; i <= 2 * PI; i += PI / particlesPerCircle) {
            double x = radius * sin(step) * cos(i);
            double y = radius * cos(step) + 1.5;
            double z = radius * sin(step) * sin(i);

            //loc.add(x, y, z);
            if (y < PI / 3) {
                world.addParticle(RedstoneParticleData.REDSTONE_DUST, getCenterOfBlock(pos.getX()) + x, pos.getY() + y, pos.getZ() + z, 0, 0 ,0);
            } else if (y > PI / 3 && y < PI / 3 * 2) {
                world.addParticle(RedstoneParticleData.REDSTONE_DUST, getCenterOfBlock(pos.getX()) + x, pos.getY() + y, pos.getZ() + z, 0, 0 ,0);
            } else if (y > PI / 3 * 2) {
                world.addParticle(RedstoneParticleData.REDSTONE_DUST, getCenterOfBlock(pos.getX()) + x, pos.getY() + y, pos.getZ() + z, 0, 0 ,0);
            }
            //loc.subtract(x, y, z);
        }
    }


    public static void fumingParticles(BlockPos particleMakingPos, World world){
        double xCoord = getCenterOfBlock(particleMakingPos.getX());
        double yCoord = getCenterOfBlock(particleMakingPos.getY()) + .5;
        double zCoord = getCenterOfBlock(particleMakingPos.getZ());

        float particleMotionX = world.rand.nextFloat() * 0.2F - 0.1F;
        float particleMotionY = 0.2F;
        float particleMotionZ = world.rand.nextFloat() * 0.2F - 0.1F;
//        FMLClientHandler.instance().getClient().effectRenderer.addEffect(
//                new ParticleSmokeNormal.Factory().createParticle(EnumParticleTypes.REDSTONE.getParticleID(), world, xCoord, yCoord, zCoord, particleMotionX, particleMotionY, particleMotionZ)
//        );
    }

    public static void blur(BlockPos pos, World world){
        double x = getCenterOfBlock(pos.getX());
        double y = getCenterOfBlock(pos.getY()) + .5;
        double z = getCenterOfBlock(pos.getZ());
        Random rand = world.rand;
        float scale = 0.05F;
        float red = rand.nextFloat() * 0.03F + 0.5F;
        float green = rand.nextFloat() * 0.03F + (rand.nextBoolean() ? 0.5F : 0.3F);
        float blue = rand.nextFloat() * 0.05F;
        float ageMultiplier = (float) (rand.nextDouble() * 2.5D + 10D);

        //ParticleEnchantmentTable blur = new ParticleEnchantmentTable(world, x, y, z, scale, 1,1,1);
        //Minecraft.getMinecraft().effectRenderer.addEffect(blur);
    }
//
//    public static void spawnPoof(BlockPos pos, World world) {
//        double x = getCenterOfBlock(pos.getX());
//        double y = getCenterOfBlock(pos.getY()) + .5;
//        double z = getCenterOfBlock(pos.getZ());
//        float dist = 0.8F;
//        world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 0.0, 0.0, 0.0);
//        for(int i = 0; i < 10; i++) {
//            float xs = (float) (Math.random() - 0.5) * dist;
//            float ys = (float) (Math.random() + .1) * dist;
//            float zs = (float) (Math.random() - 0.5) * dist;
//            ParticlePoof p = new ParticlePoof(world, x + xs, y + ys, z+zs, 0, .02, 0, Minecraft.getMinecraft().effectRenderer);
//            FMLClientHandler.instance().getClient().effectRenderer.addEffect(p);
//        }
//    }
//    public static void draw_circle (BlockPos pos, World world, EnumParticleTypes particle, double lengthOffset)
//    {
//        double x, z;
//        double posx = getCenterOfBlock(pos.getX());
//        double posz = getCenterOfBlock(pos.getZ());
//        double posy = pos.getY() + 2;
//        double length = 1 + lengthOffset;
//        float angle = 0.0f;
//        float angle_stepsize = 0.1f;
//
//        // go through all angles from 0 to 2 * PI radians
//        while (angle < 2 * PI)
//        {
//            // calculate x, y from a vector with known length and angle
//            x = length * cos (angle);
//            z = length * sin (angle);
//
//            world.spawnParticle(particle , posx+ x, posy, posz + z, 0, 0 ,0);
//            angle += angle_stepsize;
//        }
//    }
}