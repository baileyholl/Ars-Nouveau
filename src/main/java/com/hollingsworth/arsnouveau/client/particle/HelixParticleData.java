package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public class HelixParticleData implements ParticleProvider<HelixParticleTypeData> {
    private final SpriteSet spriteSet;
    public static final String NAME = "helix";

    public HelixParticleData(SpriteSet sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(HelixParticleTypeData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new ParticleHelixGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(), data.alpha, data.size, data.age, this.spriteSet, data.disableDepthTest, data.angle);
    }

    public static ParticleOptions createData(ParticleColor color, float size, float alpha, int age, float angle) {
        return new HelixParticleTypeData(ModParticles.HELIX_TYPE.get(), color, false, size, alpha, age, angle, 0.2F, 0.1F, 0.2F);
    }

}
