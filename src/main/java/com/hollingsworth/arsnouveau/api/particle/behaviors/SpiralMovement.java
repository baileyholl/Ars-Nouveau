package com.hollingsworth.arsnouveau.api.particle.behaviors;

import com.hollingsworth.arsnouveau.common.mixin.ParticleAccessor;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;

public class SpiralMovement implements IParticleMovement{
    private double angle = 0.0;     // Current angle in radians
    private double angularVelocity = 2.0; // Speed of rotation (radians per second)
    private double radialVelocity = 1.0;  // Radial expansion speed
    private double radius = 0; // Current radius of the spiral
    private double height = 0.0;

    private double centerX = 0; // Center of the spiral
    private double centerY = 0; // Center of the spiral
    Particle particle;
    ParticleAccessor particleAccessor;
    @Override
    public void init(Particle particle) {
        centerX = particle.getPos().x;
        centerY = particle.getPos().y;
        angle = 0.0f;
        radialVelocity = 0.05f;
        this.particle = particle;
        particleAccessor = (ParticleAccessor) particle;
    }

    @Override
    public void tick(Particle particle) {
//        angle += angularVelocity * ClientInfo.partialTicks;
//        // Calculate new position based on the updated angle
//        double newX = centerX + radius * Math.cos(angle);
//        double newZ = centerY + radius * Math.sin(angle);
//        float delta = ClientInfo.partialTicks;
//
//        // Calculate radial velocity components (deltas)
//        double deltaX = radialVelocity * Math.cos(angle) * delta;
//        double deltaY = radialVelocity * Math.sin(angle) * delta;
//        ParticleAccessor particleAccessor = (ParticleAccessor) particle;
//        // Convert back to Cartesian coordinates
//        particleAccessor.setXd(deltaX);
//        particleAccessor.setYd(deltaY);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
    }
}
