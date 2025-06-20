package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class WrappedProvider implements ParticleProvider<PropertyParticleOptions> {

    private final SpriteSet sprite;
    public ParticleProvider particleProvider;
    public SimpleParticleType originalType;

    public WrappedProvider(ParticleType<?> originalType, ParticleProvider<?> particleProvider) {
        ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(originalType);
        SpriteSet spriteSet = Minecraft.getInstance().particleEngine.spriteSets.get(key);
        this.sprite = spriteSet;
        this.particleProvider = particleProvider;
    }

    public WrappedProvider(SimpleParticleType originalType, ParticleEngine.SpriteParticleRegistration particleProvider) {
        ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(originalType);
        SpriteSet spriteSet = Minecraft.getInstance().particleEngine.spriteSets.get(key);
        this.sprite = spriteSet;
        this.particleProvider = particleProvider.create(spriteSet);
        this.originalType = originalType;
    }

    @Override
    public @Nullable Particle createParticle(PropertyParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        Particle particle = particleProvider.createParticle(originalType, level, x, y, z, xSpeed, ySpeed, zSpeed);
        var particleData = ParticleTypeProperty.PARTICLE_TYPES.get(data.getType());
        if(particleData != null && particleData.acceptsColor()){
            ColorProperty colorProperty = data.colorProp();
            if(!colorProperty.isTintDisabled()) {
                ParticleColor color = colorProperty.particleColor;
                particle.setColor(color.getRed(), color.getGreen(), color.getBlue());
            }
        }
        return particle;
    }
}

