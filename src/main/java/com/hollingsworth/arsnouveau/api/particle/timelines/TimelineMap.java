package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record TimelineMap(Map<IParticleTimelineType<?>, IParticleTimeline> timelines) {

    public TimelineMap(){
        this(ImmutableMap.of());
    }

    public static Codec<TimelineMap> CODEC = Codec.unboundedMap(IParticleTimelineType.CODEC, IParticleTimeline.CODEC).xmap(
            TimelineMap::new,
            (timelineMap) -> timelineMap.timelines
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TimelineMap> STREAM = StreamCodec.ofMember((val, buf) ->{
        var entries = val.timelines.entrySet();
        buf.writeInt(entries.size());

        for (var entry : entries) {
            IParticleTimelineType.STREAM_CODEC.encode(buf, entry.getKey());
            encodeTimeline(buf, entry.getKey(), entry.getValue());
        }
    }, (buf) -> {
        int size = buf.readInt();
        var immutableMap = ImmutableMap.<IParticleTimelineType<?>, IParticleTimeline>builder();
        for (int i = 0; i < size; i++) {
            IParticleTimelineType<?> type = IParticleTimelineType.STREAM_CODEC.decode(buf);
            IParticleTimeline value = type.streamCodec().decode(buf);
            immutableMap.put(type, value);
        }
        return new TimelineMap(immutableMap.build());
    });

    @NotNull
    public <T extends IParticleTimeline> T get(IParticleTimelineType<T> type) {
        return timelines.containsKey(type) ? (T) timelines.get(type) : type.create();
    }

    public <T extends IParticleTimeline> TimelineMap put(IParticleTimelineType<T> type, T value){
        return new TimelineMap(Util.copyAndPut(timelines, type, value));
    }

    private static <T extends IParticleTimeline> void encodeTimeline(RegistryFriendlyByteBuf buffer, IParticleTimelineType<T> component, Object value) {
        component.streamCodec().encode(buffer, (T)value);
    }
}
