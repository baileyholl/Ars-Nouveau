package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;


/**
 * Simplified version of <a href="https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java">ElementalCraft</a>
 */

public class ColorParticleTypeData implements ParticleOptions {

    protected ParticleType<? extends ColorParticleTypeData> type;
    public static final MapCodec<ColorParticleTypeData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ParticleColor.CODEC.fieldOf("color").forGetter(d -> d.color),
                    Codec.BOOL.fieldOf("disableDepthTest").forGetter(d -> d.disableDepthTest),
                    Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                    Codec.FLOAT.fieldOf("alpha").forGetter(d -> d.alpha),
                    Codec.INT.fieldOf("age").forGetter(d -> d.age)
            )
            .apply(instance, ColorParticleTypeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ColorParticleTypeData> STREAM_CODEC = StreamCodec.of(
            ColorParticleTypeData::toNetwork, ColorParticleTypeData::fromNetwork
    );

    public static void toNetwork(RegistryFriendlyByteBuf buf, ColorParticleTypeData data) {
        ParticleColor.STREAM.encode(buf, data.color);
        buf.writeBoolean(data.disableDepthTest);
        buf.writeFloat(data.size);
        buf.writeFloat(data.alpha);
        buf.writeInt(data.age);
    }

    public static ColorParticleTypeData fromNetwork(RegistryFriendlyByteBuf buffer) {
        ParticleColor particleColor = ParticleColor.STREAM.decode(buffer);
        boolean disableDepthTest = buffer.readBoolean();
        float size = buffer.readFloat();
        float alpha = buffer.readFloat();
        int age = buffer.readInt();
        return new ColorParticleTypeData(particleColor, disableDepthTest, size, alpha, age);
    }

    public ParticleColor color;
    public boolean disableDepthTest;
    public float size = .25f;
    public float alpha = 1.0f;
    public int age = 36;

    public ColorParticleTypeData(float r, float g, float b, boolean disableDepthTest, float size, float alpha, int age) {
        this(ModParticles.GLOW_TYPE.get(), new ParticleColor(r, g, b), disableDepthTest, size, alpha, age);
    }

    public ColorParticleTypeData(ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        this(ModParticles.GLOW_TYPE.get(), color, disableDepthTest, size, alpha, age);
    }

    public ColorParticleTypeData(ParticleType<? extends ColorParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest) {
        this(particleTypeData, color, disableDepthTest, 0.25f, 1.0f, 36);
    }

    public ColorParticleTypeData(ParticleType<? extends ColorParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        this.type = particleTypeData;
        this.color = color;
        this.disableDepthTest = disableDepthTest;
        this.size = size;
        this.alpha = alpha;
        this.age = age;
    }


    @Override
    public ParticleType<? extends ColorParticleTypeData> getType() {
        return type;
    }
}
