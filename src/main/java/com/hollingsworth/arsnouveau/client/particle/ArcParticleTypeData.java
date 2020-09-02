package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArcParticleTypeData implements IParticleData {

    private ParticleType<ArcParticleTypeData> type;
    public Vec3d source;
    public Vec3d target;
    static final IParticleData.IDeserializer<ArcParticleTypeData> DESERIALIZER = new IParticleData.IDeserializer<ArcParticleTypeData>() {
        @Override
        public ArcParticleTypeData deserialize(ParticleType<ArcParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ArcParticleTypeData(type, deseralizeVec(reader.readString()), deseralizeVec(reader.readString()));
        }

        @Override
        public ArcParticleTypeData read(ParticleType<ArcParticleTypeData> type, PacketBuffer buffer) {
            return new ArcParticleTypeData(type, deseralizeVec(buffer.readString()), deseralizeVec(buffer.readString()));
        }
    };

    public ArcParticleTypeData(ParticleType<ArcParticleTypeData> particleTypeData, Vec3d source, Vec3d target){
        this.type = particleTypeData;
        this.source = source;
        this.target = target;
    }

    @Override
    public ParticleType<ArcParticleTypeData> getType() {
        return type;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(serializeVec(target));
        packetBuffer.writeString(serializeVec(source));
    }

    @Override
    public String getParameters() {
        return type.getRegistryName().toString() + " " + serializeVec(target) + " " + serializeVec(source);
    }

    public String serializeVec(Vec3d vec3d){
        return ""+vec3d.x + "," + vec3d.y + "," + vec3d.z;
    }
    public static Vec3d deseralizeVec(String string){
        String[] arr = string.split(",");
        return new Vec3d(Double.parseDouble(arr[0].trim()), Double.parseDouble(arr[1].trim()),Double.parseDouble(arr[2].trim()));
    }
}

