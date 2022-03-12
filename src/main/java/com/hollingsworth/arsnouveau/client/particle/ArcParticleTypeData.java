package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArcParticleTypeData implements ParticleOptions {

    private ParticleType<ArcParticleTypeData> type;
    public Vec3 source;
    public Vec3 target;
    static final ParticleOptions.Deserializer<ArcParticleTypeData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ArcParticleTypeData fromCommand(ParticleType<ArcParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ArcParticleTypeData(type, deseralizeVec(reader.readString()), deseralizeVec(reader.readString()));
        }

        @Override
        public ArcParticleTypeData fromNetwork(ParticleType<ArcParticleTypeData> type, FriendlyByteBuf buffer) {
            return new ArcParticleTypeData(type, deseralizeVec(buffer.readUtf()), deseralizeVec(buffer.readUtf()));
        }
    };

    public ArcParticleTypeData(ParticleType<ArcParticleTypeData> particleTypeData, Vec3 source, Vec3 target){
        this.type = particleTypeData;
        this.source = source;
        this.target = target;
    }

    @Override
    public ParticleType<ArcParticleTypeData> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeUtf(serializeVec(target));
        packetBuffer.writeUtf(serializeVec(source));
    }

    @Override
    public String writeToString() {
        return type.getRegistryName().toString() + " " + serializeVec(target) + " " + serializeVec(source);
    }

    public String serializeVec(Vec3 vec3d){
        return ""+vec3d.x + "," + vec3d.y + "," + vec3d.z;
    }
    public static Vec3 deseralizeVec(String string){
        String[] arr = string.split(",");
        return new Vec3(Double.parseDouble(arr[0].trim()), Double.parseDouble(arr[1].trim()),Double.parseDouble(arr[2].trim()));
    }
}

