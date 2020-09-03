package com.hollingsworth.arsnouveau.client.particle.engine;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.Random;

import static com.hollingsworth.arsnouveau.client.particle.ParticleUtil.getCenterOfBlock;

public class TimedBeam extends TimedEffect{
    int delay;

    public TimedBeam(BlockPos toPos, BlockPos fromPos, int delay, ClientWorld world){
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

        double d4 = 0.1;
        LinkedList<ParticleElem> elemArrayList = new LinkedList<>();
        while ((d4) < d3)
        {
            d4 += 1.8D - d5 + 0.05 * (1.5D - d5);
//            d4 = d4/2;
            for(int i =0; i < 3; i ++){
                elemArrayList.add(new ParticleElem(GlowParticleData.createData(new ParticleColor(255,25,155)), x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D));
            }
//            elemArrayList.add(new ParticleElem(ParticleGlow.createData(new ParticleColor(255,25,180)), x1 + d0 * d4 + ParticleUtil.inRange(-0.1, 0.1), y1 + d1 * d4 + ParticleUtil.inRange(-0.1, 0.1), z1 + d2 * d4 + ParticleUtil.inRange(-0.1, 0.1), 0.0D, 0.0D, 0.0D));
//            elemArrayList.add(new ParticleElem(ParticleGlow.createData(new ParticleColor(255,25,180)), x1 + d0 * d4 + ParticleUtil.inRange(-0.1, 0.1), y1 + d1 * d4 + ParticleUtil.inRange(-0.1, 0.1), z1 + d2 * d4 + ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1)));

            //            elemArrayList.add(new ParticleElem(ParticleLineGlow.createData(new ParticleColor(225, 60, 60)), fromThisBlock.getX(), fromThisBlock.getY(), fromThisBlock.getZ(), toThisBlock.getX(), toThisBlock.getY(), toThisBlock.getZ()));

//            return elemArrayList;
//            if(world instanceof ServerWorld){
//                ((ServerWorld)world).spawnParticle(ParticleTypes.WITCH,x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4,rand.nextInt(4), 0,0.0,0, 0.0);
//            }
        }
        return elemArrayList;
    }

}
