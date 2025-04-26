package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public abstract class SnowstormParticle extends TextureSheetParticle {
    public static final float TIME_SCALE = 1.0f / 20f;
    private Vector3f acceleration = new Vector3f(0,0,0);
    private Vector3f speed = new Vector3f(0,0,0);
    private Vector3f position = new Vector3f(0,0,0);
    protected PropertyParticleOptions propertyParticleOptions;
    float phase = (float) Math.random() * 2 * (float) Math.PI;
    protected SnowstormParticle(PropertyParticleOptions propertyParticleOptions, ClientLevel level, double x, double y, double z) {
        this(propertyParticleOptions, level, x, y, z, 0, 0, 0);
    }

    protected SnowstormParticle(PropertyParticleOptions propertyParticleOptions, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        Vec3 pointOffset = getPointSpawnOffset();
        this.setPos(x + pointOffset.x, y + pointOffset.y, z + pointOffset.z);
        this.propertyParticleOptions = propertyParticleOptions;
        position.set(x, y, z);
        var dir = Direction.NORTH.step().rotateX(90* Mth.DEG_TO_RAD).rotateY(180* Mth.DEG_TO_RAD);
        if(propertyParticleOptions.map.has(ParticlePropertyRegistry.EMITTER_PROPERTY.get())){
            Vec2 rotation = propertyParticleOptions.map.get(ParticlePropertyRegistry.EMITTER_PROPERTY.get()).rotation;
            dir = Direction.NORTH.step().rotateX(rotation.x * Mth.DEG_TO_RAD).rotateY(rotation.y * Mth.DEG_TO_RAD);
        }
        float initSpeed = 6f;
        speed.set(dir.x * initSpeed, dir.y * initSpeed, dir.z * initSpeed);

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        age += 1;
        if(this.age > lifetime){
            this.remove();
        }
        xo = this.position.x;
        yo = this.position.y;
        zo = this.position.z;
        Vector3f thisTickAccel = new Vector3f(0,0,0);// this.getAcceleration();
        this.acceleration.x = thisTickAccel.x;
        this.acceleration.y = thisTickAccel.y;
        this.acceleration.z = thisTickAccel.z;
        float dragCoefficient = airDrag();
//
        this.acceleration.add(this.speed.x * -dragCoefficient, this.speed.y * -dragCoefficient, this.speed.z * -dragCoefficient);
        this.speed.add(this.acceleration.x * TIME_SCALE, this.acceleration.y * TIME_SCALE, this.acceleration.z * TIME_SCALE);
        this.position.add(this.speed.x * TIME_SCALE, this.speed.y * TIME_SCALE, this.speed.z* TIME_SCALE);

        this.move(this.speed.x * TIME_SCALE, this.speed.y * TIME_SCALE, this.speed.z * TIME_SCALE);
        float swirlSpeed      = 2.0f / 20f;    // revolutions per second
        float baseRadius      = 1.0f;    // overall radius of swirl
        float radiusWobbleAmp = 0.3f / 20f;    // how much radius oscillates
        float radiusWobbleFreq= 1.5f / 20f;    // cycles of wobble per second

        // vertical drift parameters
        float fallSpeed       = 1.0f / 20f;    // blocks per second downward
        float verticalWobbleAmp = 0.2f;  // little up/down bobbing
        float verticalWobbleFreq= 3.0f / 20f;  // cycles per second

        // jitter—small random shake to break up perfect sine waves
        float jitterAmp       = 0.05f;

        // 1) compute swirling angle (in radians)
        double angle = 2.0 * Math.PI * swirlSpeed * age + phase;

        // 2) modulate radius with a slower sine wobble
        double radius = baseRadius
                + radiusWobbleAmp * Math.sin(2.0 * Math.PI * radiusWobbleFreq * age);

        // 3) horizontal coords
        double x = radius * Math.cos(angle);
        double z = radius * Math.sin(angle);

        // 4) vertical drift + bob
        double y = -fallSpeed * age + verticalWobbleAmp * Math.sin(2.0 * Math.PI * verticalWobbleFreq * age);

        // 5) tiny random jitter (use the same phase to get consistent per‐particle jitter)
//        x += jitterAmp * Math.sin(7*angle + phase);
//        y += jitterAmp * Math.cos(5*angle - phase);
//        z += jitterAmp * Math.sin(3*angle - phase);

//        this.x += x * TIME_SCALE;
//        this.y += y * TIME_SCALE;
//        this.z += z * TIME_SCALE;
        this.setPos(this.position.x, this.position.y + y / 20f, this.position.z);

    }

    public abstract Vector3f getAcceleration();

    public abstract float airDrag();

    private static final double MAX_COLLISION_VELOCITY = Mth.square(100.0);

    @Override
    public void move(double nx, double ny, double nz) {

        if (this.hasPhysics) {
            if ((nx != 0.0 || ny != 0.0 || nz != 0.0) && nx * nx + ny * ny + nz * nz < MAX_COLLISION_VELOCITY) {
                var bb = this.getBoundingBox();
                Vec3 correctedSpeedFactor = Entity.collideBoundingBox(null, new Vec3(nx, ny, nz), bb, this.level, List.of());
                boolean xc = correctedSpeedFactor.x != nx;
                boolean yc = correctedSpeedFactor.y != ny;
                boolean zc = correctedSpeedFactor.z != nz;
                boolean collided = xc || yc || zc;
                if (collided) {
                    float bounciness = bounciness();


                    if (expireOnCollide()) {
                        this.remove();
                    }

                    if (xc)
                        this.speed.x *= -1;
                    if (yc)
                        this.speed.y *= -1;
                    if (zc)
                        this.speed.z *= -1;

                    this.position.y = (float) (yo + correctedSpeedFactor.y);

                    this.speed.y *= bounciness;
                    this.speed.x = Mth.sign(this.speed.x) * Mth.clamp(Math.abs(this.speed.x) - (collisionDrag() * TIME_SCALE), 0, Float.POSITIVE_INFINITY);
                    this.speed.z = Mth.sign(this.speed.z) * Mth.clamp(Math.abs(this.speed.z) - (collisionDrag() * TIME_SCALE), 0, Float.POSITIVE_INFINITY);
                }
            }
        }
    }

    public boolean expireOnCollide(){
        return false;
    }

    public float collisionDrag(){
        return 0f;
    }

    public float bounciness() {
        return 0f;
    }

    public int ageSeconds(){
        return this.age * 20;
    }

    public Vec3 getPointSpawnOffset(){
        return new Vec3(0, 0, 0);
    }
}
