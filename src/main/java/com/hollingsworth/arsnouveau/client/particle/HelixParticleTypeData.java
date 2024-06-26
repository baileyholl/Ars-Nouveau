package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class HelixParticleTypeData extends ColorParticleTypeData {

    float angle; // Adjust this value to control the starting angle of the helix
    float radius; // Adjust this value to control the horizontal spacing
    float radiusY; // Adjust this value to control the vertical spacing
    float speed; // Adjust this value to control the speed of the helix

    public HelixParticleTypeData(float r, float g, float b, boolean disableDepthTest, float size, float alpha, int age) {
        super(r, g, b, disableDepthTest, size, alpha, age);
    }

    public HelixParticleTypeData(ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        super(color, disableDepthTest, size, alpha, age);
    }

    public HelixParticleTypeData(ParticleType<HelixParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest, float angle, float radius, float radiusY, float speed) {
        super(particleTypeData, color, disableDepthTest);
        this.angle = angle;
        this.radius = radius;
        this.radiusY = radiusY;
        this.speed = speed;
    }

    public HelixParticleTypeData(ParticleType<HelixParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        super(particleTypeData, color, disableDepthTest, size, alpha, age);
    }


    public HelixParticleTypeData(float r, float g, float b, boolean disableDepthTest, float size, float alpha, int age, float angle, float radius, float radiusY, float speed) {
        this(ModParticles.HELIX_TYPE.get(), new ParticleColor(r, g, b), disableDepthTest, size, alpha, age, angle, radius, radiusY, speed);
    }

    public HelixParticleTypeData(ParticleType<HelixParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest, float size, float alpha, int age, float offset, float radius, float radiusY, float speed) {
        super(particleTypeData, color, disableDepthTest, size, alpha, age);
        this.angle = offset;
        this.radius = radius;
        this.radiusY = radiusY;
        this.speed = speed;
    }

    public static final MapCodec<HelixParticleTypeData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
                    Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
                    Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
                    Codec.BOOL.fieldOf("disableDepthTest").forGetter(d -> d.disableDepthTest),
                    Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                    Codec.FLOAT.fieldOf("alpha").forGetter(d -> d.alpha),
                    Codec.INT.fieldOf("age").forGetter(d -> d.age),
                    Codec.FLOAT.fieldOf("angle").forGetter(d -> d.angle),
                    Codec.FLOAT.fieldOf("radius").forGetter(d -> d.radius),
                    Codec.FLOAT.fieldOf("radiusY").forGetter(d -> d.radiusY),
                    Codec.FLOAT.fieldOf("speed").forGetter(d -> d.speed)
            )
            .apply(instance, HelixParticleTypeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HelixParticleTypeData> STREAM_CODEC = StreamCodec.of(
            HelixParticleTypeData::toNetwork, HelixParticleTypeData::fromNetwork
    );

    public static HelixParticleTypeData fromNetwork(RegistryFriendlyByteBuf buffer) {
        float r = buffer.readFloat();
        float g = buffer.readFloat();
        float b = buffer.readFloat();
        boolean disableDepthTest = buffer.readBoolean();
        float size = buffer.readFloat();
        float alpha = buffer.readFloat();
        int age = buffer.readInt();
        float angle = buffer.readFloat();
        float radius = buffer.readFloat();
        float radiusY = buffer.readFloat();
        float speed = buffer.readFloat();
        return new HelixParticleTypeData(r, g, b, disableDepthTest, size, alpha, age, angle, radius, radiusY, speed);
    }

    public static void toNetwork(RegistryFriendlyByteBuf buf, HelixParticleTypeData data) {
        buf.writeFloat(data.color.getRed());
        buf.writeFloat(data.color.getGreen());
        buf.writeFloat(data.color.getBlue());
        buf.writeBoolean(data.disableDepthTest);
        buf.writeFloat(data.size);
        buf.writeFloat(data.alpha);
        buf.writeInt(data.age);
        buf.writeFloat(data.angle);
        buf.writeFloat(data.radius);
        buf.writeFloat(data.radiusY);
        buf.writeFloat(data.speed);
    }
}
