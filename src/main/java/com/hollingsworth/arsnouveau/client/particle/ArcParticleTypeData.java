package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArcParticleTypeData implements IParticleData {

    private ParticleType<ArcParticleTypeData> type;
    public Vector3d source;
    public Vector3d target;
    static final IParticleData.IDeserializer<ArcParticleTypeData> DESERIALIZER = new IParticleData.IDeserializer<ArcParticleTypeData>() {
        @Override
        public ArcParticleTypeData fromCommand(ParticleType<ArcParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ArcParticleTypeData(type, deseralizeVec(reader.readString()), deseralizeVec(reader.readString()));
        }

        @Override
        public ArcParticleTypeData fromNetwork(ParticleType<ArcParticleTypeData> type, PacketBuffer buffer) {
            return new ArcParticleTypeData(type, deseralizeVec(buffer.readUtf()), deseralizeVec(buffer.readUtf()));
        }
    };

    public ArcParticleTypeData(ParticleType<ArcParticleTypeData> particleTypeData, Vector3d source, Vector3d target){
        this.type = particleTypeData;
        this.source = source;
        this.target = target;
    }

    @Override
    public ParticleType<ArcParticleTypeData> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(PacketBuffer packetBuffer) {
        packetBuffer.writeUtf(serializeVec(target));
        packetBuffer.writeUtf(serializeVec(source));
    }

    @Override
    public String writeToString() {
        return type.getRegistryName().toString() + " " + serializeVec(target) + " " + serializeVec(source);
    }

    public String serializeVec(Vector3d vec3d){
        return ""+vec3d.x + "," + vec3d.y + "," + vec3d.z;
    }
    public static Vector3d deseralizeVec(String string){
        String[] arr = string.split(",");
        return new Vector3d(Double.parseDouble(arr[0].trim()), Double.parseDouble(arr[1].trim()),Double.parseDouble(arr[2].trim()));
    }
}

