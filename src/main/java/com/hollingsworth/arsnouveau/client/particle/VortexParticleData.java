package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class VortexParticleData implements IParticleFactory<ColorParticleTypeData> {
    private final IAnimatedSprite spriteSet;
    public static final String NAME = "vortex";

    public VortexParticleData(IAnimatedSprite sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle makeParticle(ColorParticleTypeData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new ParticleVortex(worldIn, x,y,z,xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(), 1.0f, .25f, 36, this.spriteSet);
    }

    public static IParticleData createData(ParticleColor color) {
        return new ColorParticleTypeData(ModParticles.VORTEX_TYPE, color);
    }

}