package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

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

    public static final Codec<HelixParticleTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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

    static final ParticleOptions.Deserializer<HelixParticleTypeData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public HelixParticleTypeData fromCommand(ParticleType<HelixParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new HelixParticleTypeData(type, ParticleColor.fromString(reader.readString()), reader.readBoolean(), 0F, 0.2F, 0.1F, 0.2F);
        }

        @Override
        public HelixParticleTypeData fromNetwork(ParticleType<HelixParticleTypeData> type, FriendlyByteBuf buffer) {
            return new HelixParticleTypeData(type, ParticleColorRegistry.from(buffer.readNbt()), buffer.readBoolean(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    @Override
    public void writeToNetwork(FriendlyByteBuf packetBuffer) {
        super.writeToNetwork(packetBuffer);
        packetBuffer.writeFloat(angle);
        packetBuffer.writeFloat(radius);
        packetBuffer.writeFloat(radiusY);
        packetBuffer.writeFloat(speed);
    }

    @Override
    public String writeToString() {
        return super.writeToString();
    }
}
