package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;

import java.util.Random;

public class ParticleLineData implements IParticleFactory<ColoredDynamicTypeData> {
    private final IAnimatedSprite spriteSet;
    public static final String NAME = "line";

    public static Random random = new Random();
    public ParticleLineData(IAnimatedSprite sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(ColoredDynamicTypeData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleLine(worldIn, x,y,z,xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(),
                data.scale,
                data.age,  this.spriteSet);

    }

    public static IParticleData createData(ParticleColor color) {
        return new ColoredDynamicTypeData(ModParticles.LINE_TYPE, color, (float) ParticleUtil.inRange(0.05, 0.15), 40+random.nextInt(20));
    }

    public static IParticleData createData(ParticleColor color, float scale, int age) {
        return new ColoredDynamicTypeData(ModParticles.LINE_TYPE, color, scale, age);
    }
}