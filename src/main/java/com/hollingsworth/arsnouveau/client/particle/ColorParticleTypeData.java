package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;


/**
 * Simplified verison of ElementalCraft https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java
 */

public class ColorParticleTypeData implements IParticleData {

    private ParticleType<ColorParticleTypeData> type;
    public ParticleColor color;

    static final IParticleData.IDeserializer<ColorParticleTypeData> DESERIALIZER = new IParticleData.IDeserializer<ColorParticleTypeData>() {
        @Override
        public ColorParticleTypeData deserialize(ParticleType<ColorParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ColorParticleTypeData(type, ParticleColor.deserialize(reader.readString()));
        }

        @Override
        public ColorParticleTypeData read(ParticleType<ColorParticleTypeData> type, PacketBuffer buffer) {
            return new ColorParticleTypeData(type, ParticleColor.deserialize(buffer.readString()));
        }
    };

    public ColorParticleTypeData(ParticleType<ColorParticleTypeData> particleTypeData, ParticleColor color){
        this.type = particleTypeData;
        this.color = color;
    }

    @Override
    public ParticleType<ColorParticleTypeData> getType() {
        return type;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(color.serialize());
    }

    @Override
    public String getParameters() {
        return type.getRegistryName().toString() + " " + color.serialize();
    }
}
