package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

public class SparkleParticleProvider implements ParticleProvider<ColoredDynamicTypeData> {
    private final SpriteSet spriteSet;

    public SparkleParticleProvider(SpriteSet sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(ColoredDynamicTypeData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
        return new ParticleSparkle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(),
                data.scale,
                data.age, this.spriteSet);
    }
}
