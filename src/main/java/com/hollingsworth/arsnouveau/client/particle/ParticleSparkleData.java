package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

import java.util.Random;

public class ParticleSparkleData implements ParticleProvider<ColoredDynamicTypeData> {
    private final SpriteSet spriteSet;
    public static final String NAME = "sparkle";

    public static final Random random = new Random();

    public ParticleSparkleData(SpriteSet sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(ColoredDynamicTypeData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new ParticleSparkle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(),
                data.scale,
                data.age, this.spriteSet);
    }

    public static ParticleOptions createData(ParticleColor color) {
        return new ColoredDynamicTypeData(ModParticles.SPARKLE_TYPE.get(), color, .25f, 36);
    }

    public static ParticleOptions createData(ParticleColor color, float scale, int age) {
        return new ColoredDynamicTypeData(ModParticles.SPARKLE_TYPE.get(), color, scale, age);
    }

}