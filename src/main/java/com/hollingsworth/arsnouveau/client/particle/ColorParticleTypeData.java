package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;


/**
 * Simplified verison of ElementalCraft https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java
 */

public class ColorParticleTypeData implements IParticleData {

    private ParticleType<ColorParticleTypeData> type;
    public static final Codec<ColorParticleTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue())
    )
            .apply(instance, ColorParticleTypeData::new));

    public ParticleColor color;


    static final IParticleData.IDeserializer<ColorParticleTypeData> DESERIALIZER = new IParticleData.IDeserializer<ColorParticleTypeData>() {
        @Override
        public ColorParticleTypeData fromCommand(ParticleType<ColorParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ColorParticleTypeData(type, ParticleColor.deserialize(reader.readString()));
        }

        @Override
        public ColorParticleTypeData fromNetwork(ParticleType<ColorParticleTypeData> type, PacketBuffer buffer) {
            return new ColorParticleTypeData(type, ParticleColor.deserialize(buffer.readUtf()));
        }
    };
    public ColorParticleTypeData(float r, float g, float b){
        this.color = new ParticleColor(r, g, b);
        this.type = ModParticles.GLOW_TYPE;
    }

    public ColorParticleTypeData(ParticleType<ColorParticleTypeData> particleTypeData, ParticleColor color){
        this.type = particleTypeData;
        this.color = color;
    }

    @Override
    public ParticleType<ColorParticleTypeData> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(PacketBuffer packetBuffer) {
        packetBuffer.writeUtf(color.serialize());
    }

    @Override
    public String writeToString() {
        return type.getRegistryName().toString() + " " + color.serialize();
    }
}
