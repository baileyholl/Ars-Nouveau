package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

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
    public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
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
