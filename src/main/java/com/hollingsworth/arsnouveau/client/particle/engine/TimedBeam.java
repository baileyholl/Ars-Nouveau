package com.hollingsworth.arsnouveau.client.particle.engine;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static com.hollingsworth.arsnouveau.client.particle.ParticleUtil.getCenterOfBlock;

public class TimedBeam extends TimedEffect{
    int delay;

    public TimedBeam(BlockPos toPos, BlockPos fromPos, int delay, ServerWorld world){
        this.particles = beam(toPos, fromPos);
        this.delay = delay;
        this.world = world;
    }

    @Override
    public void tick() {
        super.tick();
        if(world == null || particles.isEmpty()){
            this.isDone = true;
            return;
        }
        if(ticks % delay == 0){
            particles.poll().addToWorld(world);
        }
    }


    public static LinkedList<ParticleElem> beam(BlockPos toThisBlock, BlockPos fromThisBlock){
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
        LinkedList<ParticleElem> elemArrayList = new LinkedList<>();
        while ((d4 + .65) < d3)
        {
            d4 += 1.8D - d5 + rand.nextDouble() * (1.5D - d5);
            elemArrayList.add(new ParticleElem(ParticleTypes.WITCH, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D));

//            if(world instanceof ServerWorld){
//                ((ServerWorld)world).spawnParticle(ParticleTypes.WITCH,x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4,rand.nextInt(4), 0,0.0,0, 0.0);
//            }
        }
        return elemArrayList;
    }

}
