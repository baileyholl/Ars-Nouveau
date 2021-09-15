package com.hollingsworth.arsnouveau.client.particle;


import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class ParticleUtil {
    public static Random r = new Random();

    public static double inRange(double min, double max){
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double getCenterOfBlock(double a){
        return (a + .5);
    }

    // https://karthikkaranth.me/blog/generating-random-points-in-a-sphere/
    public static Vector3d pointInSphere(){
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
        return new Vector3d(x,y,z);
    }

    public static void spawnFollowProjectile(World world, BlockPos from, BlockPos to){
        if(world.getChunkSource().isEntityTickingChunk(new ChunkPos(from)) && world.getChunkSource().isEntityTickingChunk(new ChunkPos(to))){
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, from, to);
            world.addFreshEntity(aoeProjectile);
        }
    }

    public static void beam(BlockPos toThisBlock, BlockPos fromThisBlock, World world){

        double x2 = getCenterOfBlock(toThisBlock.getX());
        double z2 = getCenterOfBlock(toThisBlock.getZ());
        double y2 = getCenterOfBlock(toThisBlock.getY());
        double x1 = getCenterOfBlock(fromThisBlock.getX());
        double z1 = getCenterOfBlock(fromThisBlock.getZ());
        double y1 = getCenterOfBlock(fromThisBlock.getY());
        double d5 = 1.2;
        double d0 = x2 - x1;
        double d1 = y2 - y1;
        double d2 = z2 - z1;
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;

        double d4 = r.nextDouble();

        while ((d4 + .65) < d3)
        {
            d4 += 1.8D - d5 + r.nextDouble() * (1.5D - d5);
            if(world.isClientSide)
                world.addParticle(ParticleTypes.ENCHANT, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D);
            if(world instanceof ServerWorld){
                ((ServerWorld)world).sendParticles(ParticleTypes.WITCH,x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4,r.nextInt(4), 0,0.0,0, 0.0);
            }
        }
    }

    public static ParticleColor defaultParticleColor(){
        return new ParticleColor(255, 25, 180);
    }

    public static ParticleColor.IntWrapper defaultParticleColorWrapper(){
        return new ParticleColor.IntWrapper(255, 25, 180);
    }

    public static void spawnPoof(ServerWorld world, BlockPos pos){
        for(int i =0; i < 10; i++){
            double d0 = pos.getX() +0.5;
            double d1 = pos.getY() +1.2;
            double d2 = pos.getZ() +.5 ;
            (world).sendParticles(ParticleTypes.END_ROD, d0, d1, d2, 2,(world.random.nextFloat() * 1 - 0.5)/3, (world.random.nextFloat() * 1 - 0.5)/3, (world.random.nextFloat() * 1 - 0.5)/3, 0.1f);
        }
    }

    public static void spawnTouch(ClientWorld world, BlockPos loc){
        spawnTouch(world, loc, defaultParticleColor());
    }

    public static void spawnTouch(ClientWorld world, BlockPos loc, ParticleColor particleColor){
        for(int i =0; i < 10; i++){
            double d0 = loc.getX() +0.5;
            double d1 = loc.getY() +1.0;
            double d2 = loc.getZ() +.5 ;
            world.addParticle(GlowParticleData.createData(particleColor),d0, d1, d2, (world.random.nextFloat() * 1 - 0.5)/5, (world.random.nextFloat() * 1 - 0.5)/5, (world.random.nextFloat() * 1 - 0.5)/5);
        }
    }

    public static void spawnTouchPacket(World world, BlockPos pos, ParticleColor.IntWrapper color){
        Networking.sendToNearby(world, pos,
                new PacketANEffect(PacketANEffect.EffectType.BURST, pos, color));
    }

    public static void spawnRitualAreaEffect(TileEntity entity, Random rand, ParticleColor color, int range){
        spawnRitualAreaEffect(entity.getBlockPos(), entity.getLevel(), rand, color, range);
    }

    public static void spawnRitualAreaEffect(BlockPos pos, World world, Random rand, ParticleColor color, int range){
        spawnRitualAreaEffect(pos, world, rand, color, range, 10, 10);
    }
    public static void spawnRitualAreaEffect(BlockPos pos, World world, Random rand, ParticleColor color, int range, int chance, int numParticles){
        BlockPos.betweenClosedStream(pos.offset(range, 0, range), pos.offset(-range, 0, -range)).forEach(blockPos -> {
            if(rand.nextInt(chance) == 0){
                for(int i =0; i< rand.nextInt(numParticles); i++) {
                    double x = blockPos.getX() + ParticleUtil.inRange(-0.5, 0.5);
                    double y = blockPos.getY() + ParticleUtil.inRange(-0.5, 0.5);
                    double z = blockPos.getZ() + ParticleUtil.inRange(-0.5, 0.5);
                    world.addParticle(ParticleLineData.createData(color),
                            x, y, z,
                            x, y  + ParticleUtil.inRange(0.5, 5), z);
                }
            }
        });
    }
    public static void spawnRitualSkyEffect(TileEntity tileEntity, Random rand, ParticleColor.IntWrapper color){

        int min = -5;
        int max = 5;
        BlockPos nearPos = new BlockPos(tileEntity.getBlockPos().getX() + rand.nextInt(max - min) + min, tileEntity.getBlockPos().getY(),  tileEntity.getBlockPos().getZ() + rand.nextInt(max - min) + min);
        BlockPos toPos = nearPos.above(rand.nextInt(3) + 10);
        EntityFollowProjectile proj1 = new EntityFollowProjectile(tileEntity.getLevel(),
                tileEntity.getBlockPos().above(), toPos,
                color);
//                if(getProgress() >= 10)
        proj1.getEntityData().set(EntityFollowProjectile.SPAWN_TOUCH, true);
        proj1.getEntityData().set(EntityFollowProjectile.DESPAWN, 15);

        tileEntity.getLevel().addFreshEntity(proj1);

    }

    public static void spawnRitualSkyEffect(AbstractRitual ritual, TileEntity tileEntity, Random rand, ParticleColor.IntWrapper color){
        int scalar = 20;
        if(ritual.getContext().progress >= 5)
            scalar = 10;
        if(ritual.getContext().progress >= 10)
            scalar = 5;
        if(ritual.getContext().progress >= 13)
            scalar = 3;
        if(!ritual.getWorld().isClientSide && ritual.getProgress() <= 15 && (ritual.getWorld().getGameTime() % 20 == 0 || rand.nextInt(scalar) == 0)){
            ParticleUtil.spawnRitualSkyEffect(tileEntity, rand, color);
        }
    }

    public static void spawnFallingSkyEffect(TileEntity tileEntity, Random rand, ParticleColor.IntWrapper color){
        int min = -5;
        int max = 5;
        BlockPos nearPos = new BlockPos(tileEntity.getBlockPos().getX() + rand.nextInt(max - min) + min, tileEntity.getBlockPos().getY() + 8,  tileEntity.getBlockPos().getZ() + rand.nextInt(max - min) + min);
        BlockPos toPos = nearPos.below(8);
        EntityFollowProjectile proj1 = new EntityFollowProjectile(tileEntity.getLevel(),
                nearPos, toPos,
                color);

        proj1.getEntityData().set(EntityFollowProjectile.SPAWN_TOUCH, true);
        proj1.getEntityData().set(EntityFollowProjectile.DESPAWN, 20);

        tileEntity.getLevel().addFreshEntity(proj1);

    }

    public static void spawnFallingSkyEffect(AbstractRitual ritual, TileEntity tileEntity, Random rand, ParticleColor.IntWrapper color){
        int scalar = 20;
        if(ritual.getContext().progress >= 5)
            scalar = 10;
        if(ritual.getContext().progress >= 10)
            scalar = 5;
        if(ritual.getContext().progress >= 13)
            scalar = 3;
        if(!ritual.getWorld().isClientSide && ritual.getProgress() <= 15 && (ritual.getWorld().getGameTime() % 20 == 0 || rand.nextInt(scalar) == 0)){
            ParticleUtil.spawnFallingSkyEffect(tileEntity, rand, color);
        }
    }

    public static void spawnParticleSphere(World world, BlockPos pos){
        if(!world.isClientSide)
            return;
        spawnParticleSphere(world, pos, ParticleColor.makeRandomColor(255, 255, 255, world.random));
    }

    public static void spawnParticleSphere(World world, BlockPos pos, ParticleColor color){
        if(!world.isClientSide)
            return;
        for(int i =0; i< 5; i++){
            Vector3d particlePos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
            particlePos = particlePos.add(ParticleUtil.pointInSphere());
            world.addParticle(ParticleLineData.createData(color),
                    particlePos.x(), particlePos.y(), particlePos.z(),
                    pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
        }
    }

    public static void spawnLight(World world, ParticleColor color, Vector3d vec, int intensity){
        for(int i =0; i < intensity; i++) {
            world.addParticle(
                    GlowParticleData.createData(color),
                    vec.x() + ParticleUtil.inRange(-0.1, 0.1), vec.y() + ParticleUtil.inRange(-0.1, 0.1), vec.z() + ParticleUtil.inRange(-0.1, 0.1),
                    0, 0, 0);
        }
    }
}