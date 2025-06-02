package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;

public class LeafParticle extends PropParticle {

    public double swayOffset;
    float rotSpeed;

    public LeafParticle(PropertyParticleOptions propertyParticleOptions, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(propertyParticleOptions, level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.lifetime = 40 + this.random.nextInt(20); // shorter lifespan
        this.yd = yd * 0.7 - 0.015; // slightly down from projectile
        this.xd = xd + (this.random.nextDouble() - 0.5) * 0.005;
        this.zd = zd + (this.random.nextDouble() - 0.5) * 0.005;
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.roll = (float)Math.random() * (float) (Math.PI * 2);
//        hasPhysics = false;
    }

    @Override
    public boolean tinted() {
        return true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        setColorFromProps();
        if(onGround){
            return;
        }
        // --- Local leaf flutter settings ---
        double gravity = 0.005;              // very gentle fall
        double swayFrequency = 0.05;          // slow horizontal wiggle
        double swayAmplitude = 0.01;         // subtle flutter (smaller than before)
        double bobFrequency = 0.15;
        double bobAmplitude = 0.0015;        // barely noticeable vertical bob
        double horizontalDrag = 0.79;        // keep most of projectile velocity
        double verticalDrag = 0.89;

        // Get a consistent random per-particle phase
        double phase = (this.hashCode() % 6283) / 1000.0;

        // Sway gently sideways relative to projectile direction
        double swayX = Math.sin(this.age * swayFrequency + phase) * swayAmplitude;
        double swayZ = Math.cos(this.age * swayFrequency + phase + Math.PI / 2) * swayAmplitude;
        double bobY = Math.sin(this.age * bobFrequency + phase) * bobAmplitude;

        // Apply soft drift
        this.yd -= gravity;
        this.yd += bobY;
        this.xd += swayX;
        this.zd += swayZ;

        // Drag to slowly detach from projectile's momentum
        this.xd *= horizontalDrag;
        this.yd *= verticalDrag;
        this.zd *= horizontalDrag;

        // Move
        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.xd *= 0.6;
            this.zd *= 0.6;
        }

        this.oRoll = this.roll;
        double rollFrequency = 0.1;           // lower = slower swaying
        double rollAmplitude = 0.4;           // max angle in radians (~23 degrees)
        double motionMagnitude = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        double dynamicAmplitude = Mth.clamp(motionMagnitude * 2.5, 0.1, 0.6); // faster = bigger flip
        this.roll = (float)(Math.sin(this.age * rollFrequency + phase) * dynamicAmplitude) * 10f;
    }
}
