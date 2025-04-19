package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class HelixMotion extends ParticleMotion {

    public static MapCodec<HelixMotion> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("spiralRadius").forGetter(i -> i.spiralSpeed),
            Codec.DOUBLE.fieldOf("spiralSpeed").forGetter(i -> i.spiralRadius)
    ).apply(instance, HelixMotion::new));


    public static StreamCodec<RegistryFriendlyByteBuf, HelixMotion> STREAM = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            (i) -> i.spiralRadius,
            ByteBufCodecs.DOUBLE,
            (i) -> i.spiralSpeed,
            HelixMotion::new
    );

    public double spiralRadius;
    public double spiralSpeed;


    public HelixMotion() {
        spiralRadius = 1f;
        spiralSpeed = 1f;
    }

    public HelixMotion(double spiralRadius, double spiralSpeed) {
        this.spiralRadius = spiralRadius;
        this.spiralSpeed = spiralSpeed;
    }
    public HelixMotion(double radius, double angle, double angleSpeed, double radiusSpeed) {
        this.spiralRadius = radius;
        this.spiralSpeed = angleSpeed;
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleConfigRegistry.HELIX_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
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
