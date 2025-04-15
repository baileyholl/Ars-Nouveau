package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class HelixConfiguration extends ParticleConfiguration {

    public static MapCodec<HelixConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particleOptions").forGetter(i -> i.particleOptions)
    ).apply(instance, HelixConfiguration::new));


    public static StreamCodec<RegistryFriendlyByteBuf, HelixConfiguration> STREAM = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC,
            ParticleConfiguration::particleOptions,
            HelixConfiguration::new
    );

    public double spiralRadius;
    public double spiralSpeed;

    public HelixConfiguration(ParticleOptions particleOptions) {
        super(particleOptions);
        spiralRadius = 1f;
        spiralSpeed = 1f;

    }

    public HelixConfiguration(ParticleOptions particleOptions, double radius, double angle, double angleSpeed, double radiusSpeed) {
        super(particleOptions);
        this.spiralRadius = radius;
        this.spiralSpeed = angleSpeed;
    }

    @Override
    public IParticleConfigType<?> getType() {
        return ParticleConfigRegistry.HELIX_TYPE.get();
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        double distance = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2) + Math.pow(z - prevZ, 2));
        float xRotRadians = (float) Math.toRadians(this.emitter.getRotation().x);
        float yRotRadians = (float) Math.toRadians(this.emitter.getRotation().y);
        int interpolationSteps = Math.max(1, (int) (distance / 0.1));
        for (int step = 0; step <= interpolationSteps; step++) {
            double t = (double) step / interpolationSteps;
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double interpolatedAge = this.emitter.age + t;
            double angle = interpolatedAge * spiralSpeed;
            float localX = (float) ((float) (Math.sin(angle) * spiralRadius));
            float localY = (float) ((float) (Math.cos(angle) * spiralRadius));
            float localZ = 0;
            Matrix4f transform = new Matrix4f();
            transform.identity()
                    .translate(new Vector3f((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ))
                    .rotateY(yRotRadians)
                    .rotateX(-xRotRadians);

            Vector3f localPos = new Vector3f(localX, localY, localZ);
            Vector3f opposite = new Vector3f(-localX, -localY, -localZ);
            transform.transformPosition(localPos);
            level.addParticle(ModParticles.CUSTOM_TYPE.get(), localPos.x, localPos.y, localPos.z, 0, 0, 0);
            transform.transformPosition(opposite);
            level.addParticle(ModParticles.CUSTOM_TYPE.get(), opposite.x, opposite.y, opposite.z, 0, 0, 0);
        }
    }
}
