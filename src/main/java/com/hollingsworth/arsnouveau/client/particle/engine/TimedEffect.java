package com.hollingsworth.arsnouveau.client.particle.engine;

import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TimedEffect {
    public boolean isDone;
    int ticks; // Num ticks
    LinkedList<ParticleElem> particles;

    ServerWorld world;
    public TimedEffect(){
        isDone = false;
        ticks = 0;
        particles = new LinkedList<ParticleElem>();
        world = null;
    }

    public void tick(){
        ticks++;
    }

    public static class ParticleElem{
        public IParticleData particleData;
        public double x;
        public double y;
        public double z;
        public double xSpeed;
        public double ySpeed;
        public double zSpeed;

        public ParticleElem(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            this.particleData = particleData;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.zSpeed = zSpeed;
        }

        public void addToWorld(ServerWorld world){
            world.spawnParticle(particleData, x, y, z, world.rand.nextInt(10), 0,0.0,0, 0.0);
        }
    }
}
