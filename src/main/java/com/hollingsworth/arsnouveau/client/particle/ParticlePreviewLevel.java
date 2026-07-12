package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

public class ParticlePreviewLevel extends PlanariumRenderingWorld {

    @FunctionalInterface
    public interface IParticleAdded {
        void particleAdded(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
    }

    private final IParticleAdded particleAddedCallback;

    public ParticlePreviewLevel(Level realWorld, IParticleAdded particleAddedCallback) {
        super(realWorld);
        this.particleAddedCallback = particleAddedCallback;
    }

    @Override
    public void addParticle(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        particleAddedCallback.particleAdded(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void addParticle(ParticleOptions options, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        particleAddedCallback.particleAdded(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        particleAddedCallback.particleAdded(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions options, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        particleAddedCallback.particleAdded(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
