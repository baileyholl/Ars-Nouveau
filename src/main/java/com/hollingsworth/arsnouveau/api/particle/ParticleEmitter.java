package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.EmitterProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.TickEmitterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ParticleEmitter {

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleEmitter> STREAM = StreamCodec.ofMember((val, buf) -> {
        Vec3 pos = val.getAdjustedPosition();
        ByteBufCodecs.VECTOR3F.encode(buf, pos.toVector3f());
        ByteBufCodecs.VECTOR3F.encode(buf, val.previousPosition.toVector3f());
        Vec2 rotation = val.getAdjustedRotation();
        ByteBufCodecs.VECTOR3F.encode(buf, new Vector3f(rotation.x, rotation.y, 0));

        ByteBufCodecs.INT.encode(buf, val.age);
        ParticleMotion.STREAM_CODEC.encode(buf, val.particleConfig);
        ParticleTypes.STREAM_CODEC.encode(buf, val.particleOptions);
        ByteBufCodecs.DOUBLE.encode(buf, val.rand1);
        ByteBufCodecs.DOUBLE.encode(buf, val.rand2);
        ByteBufCodecs.DOUBLE.encode(buf, val.rand3);

    }, (buf) ->{
        Vector3f position = ByteBufCodecs.VECTOR3F.decode(buf);
        Vector3f previousPosition = ByteBufCodecs.VECTOR3F.decode(buf);
        Vector3f rotation = ByteBufCodecs.VECTOR3F.decode(buf);
        int age = ByteBufCodecs.INT.decode(buf);
        ParticleMotion particleConfig = ParticleMotion.STREAM_CODEC.decode(buf);
        ParticleOptions particleOptions = ParticleTypes.STREAM_CODEC.decode(buf);
        Supplier<Vec3> positionSupplier = () -> new Vec3(position.x, position.y, position.z);
        Supplier<Vec2> rotationSupplier = () -> new Vec2(rotation.x, rotation.y);
        double rand1 = ByteBufCodecs.DOUBLE.decode(buf);
        double rand2 = ByteBufCodecs.DOUBLE.decode(buf);
        double rand3 = ByteBufCodecs.DOUBLE.decode(buf);
        ParticleEmitter emitter = new ParticleEmitter(positionSupplier, rotationSupplier, particleConfig, particleOptions);
        emitter.age = age;
        emitter.previousPosition = new Vec3(previousPosition.x, previousPosition.y, previousPosition.z);
        emitter.rand1 = rand1;
        emitter.rand2 = rand2;
        emitter.rand3 = rand3;
        return emitter;
    });

    public Vec3 offset;
    public Supplier<Vec3> position;
    public Vec3 previousPosition;
    public ParticleMotion particleConfig;
    public int age;
    public Supplier<Vec2> rotation;
    public Vec2 rotationOffset;
    public ParticleOptions particleOptions;
    public double rand1;
    public double rand2;
    public double rand3;

    public ParticleEmitter(Supplier<Vec3> getPosition, Supplier<Vec2> rot, ParticleMotion particleConfig, ParticleOptions particleOptions) {
        this.position = getPosition;
        this.offset = Vec3.ZERO;
        this.particleConfig = particleConfig;
        this.rotation = rot;
        this.rotationOffset = Vec2.ZERO;
        this.particleOptions = particleOptions;
        this.rand1 = Math.random();
        this.rand2 = Math.random();
        this.rand3 = Math.random();
        particleConfig.init(this);
    }

    public ParticleEmitter(Supplier<Vec3> getPosition, Supplier<Vec2> rot, TimelineEntryData entryData) {
        this(getPosition, rot, entryData.motion(), entryData.particleOptions());
    }

    public void setPositionOffset(Vec3 offset){
        this.offset = offset;
    }

    public void setPositionOffset(double x, double y, double z){
        this.offset = new Vec3(x, y, z);
    }

    public Vec3 getPositionOffset(){
        return offset;
    }

    public Vec3 getPosition(){
        return position.get();
    }

    public Vec3 getAdjustedPosition(){
        return position.get().add(offset);
    }

    public Vec2 getRotation(){
        return rotation.get();
    }

    public Vec2 getAdjustedRotation(){
        return rotation.get().add(rotationOffset);
    }

    public void setRotationOffset(Vec2 offset){
        this.rotationOffset = offset;
    }

    public void setRotationOffset(float x, float y){
        this.rotationOffset = new Vec2(x, y);
    }

    public void tick(Level level){
        if(this.previousPosition == null){
            this.previousPosition = this.getAdjustedPosition();
        }
        Vec3 pos = getAdjustedPosition();
        if(particleOptions instanceof PropertyParticleOptions propertyParticleOptions){
            Vec2 adjustedRotation = getAdjustedRotation();
            propertyParticleOptions.map.set(ParticlePropertyRegistry.EMITTER_PROPERTY.get(), new EmitterProperty(new Vec2(adjustedRotation.x, adjustedRotation.y), age));
        }
        if(level instanceof ServerLevel serverLevel){
            Networking.sendToNearbyClient(serverLevel, BlockPos.containing(pos), new TickEmitterPacket(this));
        }else {
            particleConfig.tick(particleOptions, level, pos.x, pos.y, pos.z, previousPosition.x, previousPosition.y, previousPosition.z);
        }
        this.previousPosition = pos;
        this.age++;
    }


}
