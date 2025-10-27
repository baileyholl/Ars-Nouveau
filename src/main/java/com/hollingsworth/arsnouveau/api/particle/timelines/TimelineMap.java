package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public record TimelineMap(Map<IParticleTimelineType<?>, IParticleTimeline<?>> timelines) {

    public static Codec<TimelineMap> CODEC = Codec.unboundedMap(IParticleTimelineType.CODEC, IParticleTimeline.CODEC).<TimelineMap>xmap(
            TimelineMap::new,
            (timelineMap) -> timelineMap.timelines
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TimelineMap> STREAM = StreamCodec.ofMember((val, buf) -> {
        var entries = val.timelines.entrySet();
        buf.writeInt(entries.size());

        for (var entry : entries) {
            IParticleTimelineType.STREAM_CODEC.encode(buf, entry.getKey());
            encodeTimeline(buf, entry.getKey(), entry.getValue());
        }
    }, (buf) -> {
        int size = buf.readInt();
        var immutableMap = ImmutableMap.<IParticleTimelineType<? extends IParticleTimeline<?>>, IParticleTimeline<?>>builder();
        for (int i = 0; i < size; i++) {
            IParticleTimelineType<?> type = IParticleTimelineType.STREAM_CODEC.decode(buf);
            IParticleTimeline<? extends IParticleTimeline<?>> value = type.streamCodec().decode(buf);
            immutableMap.put(type, value);
        }
        return new TimelineMap(immutableMap.build());
    });

    public TimelineMap() {
        this(ImmutableMap.of());
    }

    @NotNull
    public <T extends IParticleTimeline<T>> T get(IParticleTimelineType<T> type) {
        return timelines.containsKey(type) ? (T) timelines.get(type) : type.create();
    }

    @NotNull
    public <T extends IParticleTimeline<T>> T get(Supplier<IParticleTimelineType<T>> type) {
        return this.get(type.get());
    }

    public <T extends IParticleTimeline<T>> TimelineMap put(IParticleTimelineType<T> type, T value) {
        return new TimelineMap(Util.copyAndPut(timelines, type, value));
    }

    private static <T extends IParticleTimeline<T>> void encodeTimeline(RegistryFriendlyByteBuf buffer, IParticleTimelineType<T> component, Object value) {
        component.streamCodec().encode(buffer, (T) value);
    }

    public MutableTimelineMap mutable() {
        Object copyMap = TimelineMap.CODEC.encodeStart(JavaOps.INSTANCE, this).getOrThrow();
        TimelineMap copyTimeline = CODEC.decode(JavaOps.INSTANCE, copyMap).getOrThrow().getFirst();
        return new MutableTimelineMap(copyTimeline.timelines);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimelineMap that = (TimelineMap) o;
        return Objects.equals(timelines, that.timelines);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timelines);
    }

    // Call on both sides to inspect the mismatched timeline hashcodes
    public void debugPrintHash(Spell spell, Level level) {
        System.out.println(level);
        TimelineMap timelineMap = spell.particleTimeline();
        for (IParticleTimelineType<?> data : timelineMap.timelines().keySet()) {
            System.out.println(timelineMap.get(data).hashCode() + " : " + data);
        }
    }


    public static class MutableTimelineMap {
        private final Map<IParticleTimelineType<?>, IParticleTimeline<? extends IParticleTimeline<?>>> timelines;

        public MutableTimelineMap(Map<IParticleTimelineType<?>, IParticleTimeline<? extends IParticleTimeline<?>>> map) {
            timelines = new HashMap<>();
            for (var entry : map.entrySet()) {
                timelines.put(entry.getKey(), entry.getValue());
            }
        }

        public <T extends IParticleTimeline<T>> T getOrCreate(IParticleTimelineType<T> type) {
            return (T) timelines.computeIfAbsent(type, (key) -> type.create());
        }

        public <T extends IParticleTimeline<T>> IParticleTimeline put(IParticleTimelineType<T> type, T value) {
            return timelines.put(type, value);
        }

        public TimelineMap immutable() {
            TimelineMap createdMap = new TimelineMap(timelines);
            Object copyMap = TimelineMap.CODEC.encodeStart(JavaOps.INSTANCE, createdMap).getOrThrow();
            TimelineMap copyTimeline = CODEC.decode(JavaOps.INSTANCE, copyMap).getOrThrow().getFirst();
            return copyTimeline;
        }
    }
}
