package com.hollingsworth.arsnouveau.client.particle.engine;

import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.LinkedList;

import static com.hollingsworth.arsnouveau.client.particle.ParticleUtil.getCenterOfBlock;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TimedHelix extends TimedEffect{
    BlockPos pos;
    int delay;
    public TimedHelix(BlockPos pos, int delay, IParticleData data, ServerWorld world){
        this.particles = buildList(pos, data);
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
        if(delay == 0){
            particles.poll().addToWorld(world);
        }else if(ticks % delay == 0){
            particles.poll().addToWorld(world);
        }
    }

    public LinkedList<ParticleElem> buildList(BlockPos pos , IParticleData particle) {
        double radius = 0.75;
        LinkedList<ParticleElem> elemArrayList = new LinkedList<>();
        for (double y = 0; y <= 4; y += 0.25) {
            double x = radius * cos(y);
            double z = radius * sin(y);
            elemArrayList.add(new ParticleElem(particle, getCenterOfBlock(pos.getX()) - .5 + x, pos.getY() + 1.5 + y, getCenterOfBlock(pos.getZ()) + z, 0, 0, 0));
            elemArrayList.add(new ParticleElem(particle, getCenterOfBlock(pos.getX()) - .5 + -x, pos.getY() + 1.5 + y, getCenterOfBlock(pos.getZ()) + -z, 0, 0, 0));

        }
        return elemArrayList;
    }
}
