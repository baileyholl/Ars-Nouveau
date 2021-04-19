package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;

import java.util.Random;

public class ParticleSparkleData implements IParticleFactory<ColoredDynamicTypeData> {
    private final IAnimatedSprite spriteSet;
    public static final String NAME = "sparkle";

    public static final Random random = new Random();
    public ParticleSparkleData(IAnimatedSprite sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(ColoredDynamicTypeData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new ParticleSparkle(worldIn, x,y,z,xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(),
                data.scale,
                data.age,  this.spriteSet);
    }

    public static IParticleData createData(ParticleColor color) {
        return new ColoredDynamicTypeData(ModParticles.SPARKLE_TYPE, color,.25f,  36);
    }

    public static IParticleData createData(ParticleColor color, float scale, int age) {
        return new ColoredDynamicTypeData(ModParticles.SPARKLE_TYPE, color, scale, age);
    }

}