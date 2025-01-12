package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ColoredDynamicTypeData implements ParticleOptions {

    private ParticleType<ColoredDynamicTypeData> type;
    public ParticleColor color;
    float scale;
    int age;

    public static final MapCodec<ColoredDynamicTypeData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
                    Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
                    Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
                    Codec.FLOAT.fieldOf("scale").forGetter(d -> d.scale),
                    Codec.INT.fieldOf("age").forGetter(d -> d.age)
            )
            .apply(instance, ColoredDynamicTypeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ColoredDynamicTypeData> STREAM_CODEC = StreamCodec.of(
            ColoredDynamicTypeData::toNetwork, ColoredDynamicTypeData::fromNetwork
    );

    public static void toNetwork(RegistryFriendlyByteBuf buf, ColoredDynamicTypeData data) {
        buf.writeFloat(data.color.getRed());
        buf.writeFloat(data.color.getGreen());
        buf.writeFloat(data.color.getBlue());
        buf.writeFloat(data.scale);
        buf.writeInt(data.age);
    }

    public static ColoredDynamicTypeData fromNetwork(RegistryFriendlyByteBuf buffer) {
        float r = buffer.readFloat();
        float g = buffer.readFloat();
        float b = buffer.readFloat();
        float scale = buffer.readFloat();
        int age = buffer.readInt();
        return new ColoredDynamicTypeData(r, g, b, scale, age);
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public ColoredDynamicTypeData(float r, float g, float b, float scale, int age) {
        this.type = ModParticles.LINE_TYPE.get();
        this.color = new ParticleColor(r, g, b);
        this.scale = scale;
        this.age = age;
    }

    public ColoredDynamicTypeData(ParticleType<ColoredDynamicTypeData> particleTypeData, ParticleColor color, float scale, int age) {
        this.type = particleTypeData;
        this.color = color;
        this.scale = scale;
        this.age = age;
    }
}
