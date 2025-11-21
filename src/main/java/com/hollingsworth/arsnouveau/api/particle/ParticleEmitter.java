package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.EmitterProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.common.network.PacketBatchedTickEmitter;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@EventBusSubscriber
public class ParticleEmitter {
    private static final Object2ObjectOpenHashMap<UUID, List<ParticleEmitter>> QUEUE = new Object2ObjectOpenHashMap<>();

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleEmitter> STREAM = StreamCodec.ofMember((val, buf) -> {
        Vec3 pos = val.getAdjustedPosition();
        ByteBufCodecs.VECTOR3F.encode(buf, pos.toVector3f());
        if (val.previousPosition == null) {
            val.previousPosition = val.getAdjustedPosition();
        }
        ByteBufCodecs.VECTOR3F.encode(buf, val.previousPosition.toVector3f());
        Vec2 rotation = val.getAdjustedRotation();
        ByteBufCodecs.VECTOR3F.encode(buf, new Vector3f(rotation.x, rotation.y, 0));

        ByteBufCodecs.INT.encode(buf, val.age);
        ParticleMotion.STREAM_CODEC.encode(buf, val.particleConfig);
        ParticleTypes.STREAM_CODEC.encode(buf, val.particleOptions);
        ByteBufCodecs.DOUBLE.encode(buf, val.rand1);
        ByteBufCodecs.DOUBLE.encode(buf, val.rand2);
        ByteBufCodecs.DOUBLE.encode(buf, val.rand3);

    }, (buf) -> {
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
    public PropertyParticleOptions particleOptions;
    public double rand1;
    public double rand2;
    public double rand3;

    public ParticleEmitter(Supplier<Vec3> getPosition, Supplier<Vec2> rot, ParticleMotion particleConfig, ParticleOptions particleOptions) {
        this.position = getPosition;
        this.offset = Vec3.ZERO;
        this.particleConfig = particleConfig;
        this.rotation = rot;
        this.rotationOffset = Vec2.ZERO;
        this.particleOptions = particleOptions instanceof PropertyParticleOptions propertyParticleOptions ? propertyParticleOptions : new PropertyParticleOptions(particleOptions.getType());
        this.rand1 = Math.random();
        this.rand2 = Math.random();
        this.rand3 = Math.random();
        particleConfig.init(this);
    }

    public ParticleEmitter(Supplier<Vec3> getPosition, Supplier<Vec2> rot, TimelineEntryData entryData) {
        this(getPosition, rot, entryData.motion(), entryData.particleOptions());
    }

    public ParticleEmitter(Entity entity, TimelineEntryData timelineEntryData) {
        this(() -> entity.getBoundingBox().getCenter(), entity::getRotationVector, timelineEntryData);
    }

    public void setPositionOffset(Vec3 offset) {
        this.offset = offset;
    }

    public void setPositionOffset(double x, double y, double z) {
        this.offset = new Vec3(x, y, z);
    }

    public Vec3 getPositionOffset() {
        return offset;
    }

    public Vec3 getPosition() {
        return position.get();
    }

    public void setPosition(Vec3 position) {
        this.position = () -> position;
    }

    public Vec3 getAdjustedPosition() {
        return position.get().add(offset);
    }

    public Vec2 getRotation() {
        return rotation.get();
    }

    public Vec2 getAdjustedRotation() {
        return rotation.get().add(rotationOffset);
    }

    public void setRotationOffset(Vec2 offset) {
        this.rotationOffset = offset;
    }

    public void setRotationOffset(float x, float y) {
        this.rotationOffset = new Vec2(x, y);
    }

    public void tick(Level level) {
        if (this.previousPosition == null) {
            this.previousPosition = this.getAdjustedPosition();
        }
        Vec3 pos = getAdjustedPosition();

        Vec2 adjustedRotation = getAdjustedRotation();
        particleOptions.map.set(ParticlePropertyRegistry.EMITTER_PROPERTY.get(), new EmitterProperty(new Vec2(adjustedRotation.x, adjustedRotation.y), age));
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(BlockPos.containing(pos)), false)) {
                QUEUE.compute(player.getUUID(), (a, b) -> {
                    List<ParticleEmitter> list = b == null ? new ArrayList<>() : b;
                    list.add(this);
                    return list;
                });
            }
        } else {
            particleConfig.tick(particleOptions, level, pos.x, pos.y, pos.z, previousPosition.x, previousPosition.y, previousPosition.z);
        }
        this.previousPosition = pos;
        this.age++;
    }

    @SubscribeEvent
    public static void processQueue(ServerTickEvent.Post event) {
        processQueue(event.getServer().getPlayerList());
    }

    public static void processQueue(PlayerList players) {
        if (QUEUE.isEmpty()) {
            return;
        }

        var iter = Object2ObjectMaps.fastIterator(QUEUE);
        while (iter.hasNext()) {
            var entry = iter.next();
            var player = players.getPlayer(entry.getKey());
            var particles = entry.getValue();
            if (player != null && !particles.isEmpty()) {
                player.connection.send(new PacketBatchedTickEmitter(particles));
            }

            iter.remove();
        }
    }
}
