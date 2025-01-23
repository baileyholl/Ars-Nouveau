package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class SpiralConfiguration extends ParticleConfiguration {

    public static MapCodec<SpiralConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particleOptions").forGetter(i -> i.particleOptions)
    ).apply(instance, SpiralConfiguration::new));


    public static StreamCodec<RegistryFriendlyByteBuf, SpiralConfiguration> STREAM = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC,
            ParticleConfiguration::particleOptions,
            SpiralConfiguration::new
    );

    public double radius;
    public double angle;
    public double angleSpeed;
    public double radiusSpeed;

    public SpiralConfiguration(ParticleOptions particleOptions) {
        super(particleOptions);
        this.radius = 1f;
        angleSpeed = 1f;
        radiusSpeed = 0.3f;

    }

    public SpiralConfiguration(ParticleOptions particleOptions, double radius, double angle, double angleSpeed, double radiusSpeed) {
        super(particleOptions);
        this.radius = radius;
        this.angle = angle;
        this.angleSpeed = angleSpeed;
        this.radiusSpeed = radiusSpeed;
    }

    @Override
    public IParticleType<?> getType() {
        return ParticleConfigRegistry.SPIRAL_TYPE.get();
    }
    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        double spiralRadius = 1f;  // Radius of the spiral
        double spiralSpeed = 1;  // Speed at which the particles move along the spiral

        double distance = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2) + Math.pow(z - prevZ, 2));
        int interpolationSteps = Math.max(1, (int) (distance / 0.1)); // Adjust 0.1 for step granularity
        for (int step = 0; step <= interpolationSteps; step++) {
            // Linear interpolation factor
            double t = (double) step / interpolationSteps;

            // Interpolated position between previous and current
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double interpolatedAge = emitter.age + t;
            double angle = interpolatedAge * spiralSpeed;
            float localX = (float) (Math.cos(angle) * spiralRadius);
            float localZ = 0;
            float localY = (float) (Math.sin(angle) * spiralRadius);

            Matrix4d transform = new Matrix4d()
                    .rotateY(Math.toRadians(emitter.rotation.get().y))
                    .rotateX(Math.toRadians(emitter.rotation.get().x));
            Vector3d worldPosition = new Vector3d();
            transform.transformPosition(new Vector3d(localX, localY, localZ), worldPosition);

            level.addParticle(particleOptions, interpolatedX + worldPosition.x, interpolatedY + worldPosition.y, interpolatedZ + worldPosition.z, 0, 0, 0);
        }
    }

    public static Vec3 rotate(double x, double y, double z, double rotationX, double rotationY) {
        double rotationXRad = Math.toRadians(rotationX);
        double rotationYRad = Math.toRadians(rotationY);

        double cosX = Math.cos(rotationXRad);
        double sinX = Math.sin(rotationXRad);
        double cosY = Math.cos(rotationYRad);
        double sinY = Math.sin(rotationYRad);

        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;

        double x2 = x * cosY + z1 * sinY;
        double z2 = -x * sinY + z1 * cosY;

        return new Vec3(x2, y1, z2);
    }
//
//    @Override
//    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
////        angle += angleSpeed;
////
////        System.out.println(emitter.getPositionOffset());
////        RandomSource random = level.random;
////        double deltaX = x - prevX;
////        double deltaY = y - prevY;
////        double deltaZ = z - prevZ;
////        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
////        for (double j = 0; j < dist; j+= 0.2f) {
////            double coeff = j / dist;
////            level.addParticle(this.particleOptions,
////                    prevX + deltaX * coeff,
////                    prevY + deltaY * coeff,
////                    prevZ + deltaZ * coeff,
////                    0.0125f * (random.nextFloat() - 0.5f),
////                    0.0125f * (random.nextFloat() - 0.5f),
////                    0.0125f * (random.nextFloat() - 0.5f));
////        }
////        emitter.setPositionOffset(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
//
//
//        // Spiral parameters
////        double spiralRadius = 0.25f;  // Radius of the spiral
////        double spiralSpeed = 1;  // Speed at which the particles move along the spiral
////        int particlesPerTick = 10; // Number of particles to spawn per tick
////
////        // Calculate the velocity vector of the emitter (direction of motion)
////        double velocityX = x - prevX;
////        double velocityY = y - prevY;
////        double velocityZ = z - prevZ;
////
////        // Normalize the velocity vector to get the direction
////        double magnitude = Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
////        if (magnitude == 0) return; // Prevent division by zero if the emitter is stationary
////        double dirX = velocityX / magnitude;
////        double dirY = velocityY / magnitude;
////        double dirZ = velocityZ / magnitude;
////
////        // Create orthogonal basis vectors for the spiral plane
////        Vec3 forward = new Vec3(dirX, dirY, dirZ); // Direction of movement
////        Vec3 up = new Vec3(0, 1, 0); // Arbitrary "up" vector
////        if (Math.abs(forward.y) > 0.99) {
////            // Handle near-vertical directions by adjusting the "up" vector
////            up = new Vec3(1, 0, 0);
////        }
////        Vec3 right = forward.cross(up).normalize(); // Perpendicular to forward and up
////        Vec3 adjustedUp = right.cross(forward).normalize(); // Perpendicular to forward and right
////
////        for (int i = 0; i < particlesPerTick; i++) {
////            // Calculate the angle for the current particle
////            double angle = (emitter.age + (i / (double) particlesPerTick)) * spiralSpeed;
////
////            // Calculate the position offset for the particle in the spiral
////            double localX = Math.cos(angle) * spiralRadius;
////            double localZ = Math.sin(angle) * spiralRadius;
////            double localY = 0; // Spiral moves upwards gradually
////
////            // Transform the local offset into world space using the orthogonal basis vectors
////            double finalX = right.x * localX + adjustedUp.x * localZ + forward.x * localY;
////            double finalY = right.y * localX + adjustedUp.y * localZ + forward.y * localY;
////            double finalZ = right.z * localX + adjustedUp.z * localZ + forward.z * localY;
////
////            // Spawn the particle at the transformed position relative to the emitter
////            level.addParticle(particleOptions, x + finalX, y + finalY, z + finalZ, 0, 0, 0);
////        }
//
//        // Spiral parameters
//        // Spiral parameters
//        // Spiral parameters
//        level.addParticle(ParticleTypes.CRIT.getType(), x, y, z, 0, 0, 0);
//        double spiralRadius = 1;  // Radius of the spiral
//        double spiralSpeed = 1.0;  // Speed at which the particles move along the spiral
//        int particlesPerTick = 1; // Number of particles to spawn per tick
//        double verticalSpacing = 0.05; // Vertical spacing for the spiral
//
//        // Calculate the distance between the current and previous positions
//        double distance = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2) + Math.pow(z - prevZ, 2));
//
//        // Number of intermediate steps to fill gaps
//        int interpolationSteps = Math.max(1, (int) (distance / 0.1)); // Adjust 0.1 for step granularity
//
//        // Get the emitter's rotation (yaw and pitch) for alignment
//        Vec2 rotation = emitter.getAdjustedRotation();
//        double yaw = Math.toRadians(rotation.x); // Horizontal rotation (yaw)
//        double pitch = Math.toRadians(rotation.y); // Vertical rotation (pitch)
//
//        // Calculate the forward, right, and up vectors for the emitter's orientation
//        Vec3 forward = new Vec3(
//                Math.cos(pitch) * Math.cos(yaw),
//                Math.sin(pitch),
//                Math.cos(pitch) * Math.sin(yaw)
//        );
//        Vec3 up = new Vec3(0, 1, 0); // World up vector
//        Vec3 right = forward.cross(up).normalize(); // Perpendicular to forward and up
//        Vec3 adjustedUp = right.cross(forward).normalize(); // Perpendicular to forward and right
//
//        for (int step = 0; step <= interpolationSteps; step++) {
//            // Linear interpolation factor
//            double t = (double) step / interpolationSteps;
//
//            // Interpolated position between previous and current
//            double interpolatedX = prevX + t * (x - prevX);
//            double interpolatedY = prevY + t * (y - prevY);
//            double interpolatedZ = prevZ + t * (z - prevZ);
//
//            // Interpolate the angle for the current step
//            double interpolatedAge = emitter.age + t; // Interpolated age for smoother spiral rotation
//
//            for (int i = 0; i < particlesPerTick; i++) {
//                // Calculate the angle for the current particle
//                double angle = (interpolatedAge + (i / (double) particlesPerTick)) * spiralSpeed;
//
//                // Calculate the local position offset for the particle in 3D
//                double localX = Math.cos(angle) * spiralRadius;
//                double localY = i * verticalSpacing; // Gradually increase Y for each particle in the spiral
//                double localZ = Math.sin(angle) * spiralRadius;
//
//                // Transform the local spiral offsets into the emitter's local 3D plane
//                double offsetX = localX * right.x + localY * adjustedUp.x + localZ * forward.x;
//                double offsetY = localX * right.y + localY * adjustedUp.y + localZ * forward.y;
//                double offsetZ = localX * right.z + localY * adjustedUp.z + localZ * forward.z;
//
//                // Final particle position in world space
//                double finalX = interpolatedX + offsetX;
//                double finalY = interpolatedY + offsetY;
//                double finalZ = interpolatedZ + offsetZ;
//
//                // Spawn the particle at the final position
//                level.addParticle(particleOptions, finalX, finalY, finalZ, 0, 0, 0);
//            }
//        }
//    }
}
