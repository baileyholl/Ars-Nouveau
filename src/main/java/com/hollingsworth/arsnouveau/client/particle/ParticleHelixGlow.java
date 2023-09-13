package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;

public class ParticleHelixGlow extends ParticleGlow {
    private final float angleOffset; // Adjust this value to control the starting angle of the helix
    float radius; // Adjust this value to control the horizontal spacing
    float radiusY; // Adjust this value to control the vertical spacing
    float speed; // Adjust this value to control the speed of the helix

    public ParticleHelixGlow(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime, SpriteSet sprite, boolean disableDepthTest, float angle) {
        this(worldIn, x, y, z, vx, vy, vz, r, g, b, a, scale, lifetime, sprite, disableDepthTest, angle, 0.2F, 0.1F, 0.2F);
    }

    public ParticleHelixGlow(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime, SpriteSet sprite, boolean disableDepthTest, float angle, float radius, float radiusY, float speed) {
        super(worldIn, x, y, z, vx, vy, vz, r, g, b, a, scale, lifetime, sprite, disableDepthTest);
        this.angleOffset = angle;
        this.radius = radius;
        this.speed = speed;
        this.radiusY = radiusY;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void move(double pX, double pY, double pZ) {
        super.move(pX, pY, pZ);
        // Increment the angle to make the helix spiral
        float angle = age * speed + angleOffset; // You can adjust the increment to control the speed of the helix

        // Calculate new positions for the particle in the double helix pattern
        double newX = xo + Math.cos(angle) * radius;
        double newY = yo + age * radiusY; // Adjust this value to control the vertical spacing
        double newZ = zo + Math.sin(angle) * radius;

        // Update the particle's position
        this.setPos(newX, newY, newZ);
    }
}
