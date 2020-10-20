package com.hollingsworth.arsnouveau.client.particle;


import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bailey on 12/26/2016.
 */
public class ParticleUtil {
    public static Random r = new Random();

    public static double inRange(double min, double max){
        return ThreadLocalRandom.current().nextDouble(min, max);
    }


    public static double getCenterOfBlock(double a){
        return (a + .5);
    }

    // https://karthikkaranth.me/blog/generating-random-points-in-a-sphere/
    public static Vec3d pointInSphere(BlockPos pos){
        double u = Math.random();
        double v = Math.random();
        double theta = u * 2.0 * Math.PI;
        double phi = Math.acos(2.0 * v - 1.0);
        double r = Math.cbrt(Math.random());
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double x = r * sinPhi * cosTheta;
        double y = r * sinPhi * sinTheta;
        double z = r * cosPhi;
        return new Vec3d(x,y,z);
    }

    public static void beam(BlockPos toThisBlock, BlockPos fromThisBlock, World world){
        Random rand = new Random();
        double x2 = getCenterOfBlock(toThisBlock.getX());
        double z2 = getCenterOfBlock(toThisBlock.getZ());
        double y2 = getCenterOfBlock(toThisBlock.getY());
        double x1 = getCenterOfBlock(fromThisBlock.getX());
        double z1 = getCenterOfBlock(fromThisBlock.getZ());
        double y1 = getCenterOfBlock(fromThisBlock.getY());
        double d5 = 1.2;
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
            d4 += 1.8D - d5 + rand.nextDouble() * (1.5D - d5);
            if(world.isRemote)
                world.addParticle(ParticleTypes.ENCHANT, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D);
            if(world instanceof ServerWorld){
                ((ServerWorld)world).spawnParticle(ParticleTypes.WITCH,x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4,rand.nextInt(4), 0,0.0,0, 0.0);
            }
        }
    }


    public static void spawnPoof(ServerWorld world, BlockPos pos){
        for(int i =0; i < 10; i++){
            double d0 = pos.getX() +0.5;
            double d1 = pos.getY() +1.2;
            double d2 = pos.getZ() +.5 ;
            (world).spawnParticle(ParticleTypes.END_ROD, d0, d1, d2, 2,(world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, 0.1f);
        }
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
//
//        EnchantmentTableParticle blur = new EnchantmentTableParticle(world, x, y, z, scale, 1,1,1);
//        Minecraft.getInstance().eff.addEffect(blur);
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