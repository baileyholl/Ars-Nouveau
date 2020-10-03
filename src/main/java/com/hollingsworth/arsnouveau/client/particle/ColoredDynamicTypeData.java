package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class ColoredDynamicTypeData implements IParticleData {

    private ParticleType<ColoredDynamicTypeData> type;
    public ParticleColor color;
    float scale;
    int age;

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    static final IParticleData.IDeserializer<ColoredDynamicTypeData> DESERIALIZER = new IParticleData.IDeserializer<ColoredDynamicTypeData>() {
        @Override
        public ColoredDynamicTypeData deserialize(ParticleType<ColoredDynamicTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ColoredDynamicTypeData(type, ParticleColor.deserialize(reader.readString()), reader.readFloat(), reader.readInt());
        }

        @Override
        public ColoredDynamicTypeData read(ParticleType<ColoredDynamicTypeData> type, PacketBuffer buffer) {
            return new ColoredDynamicTypeData(type, ParticleColor.deserialize(buffer.readString()), buffer.readFloat(), buffer.readInt());
        }
    };

    public ColoredDynamicTypeData(ParticleType<ColoredDynamicTypeData> particleTypeData, ParticleColor color, float scale, int age){
        this.type = particleTypeData;
        this.color = color;
        this.scale = scale;
        this.age = age;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(color.serialize());
        buffer.writeFloat(scale);
        buffer.writeInt(age);
    }

    @Override
    public String getParameters() {
        return type.getRegistryName().toString() + " " + color.serialize() + " " + scale + " " + age;
    }
}
