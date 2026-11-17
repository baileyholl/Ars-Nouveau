package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IParticleTimelineType<T extends IParticleTimeline<T>> {
    Codec<IParticleTimelineType<? extends IParticleTimeline<?>>> CODEC = Codec.lazyInitialized(ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY::byNameCodec);

    StreamCodec<RegistryFriendlyByteBuf, IParticleTimelineType<?>> STREAM_CODEC = StreamCodec.recursive(
            p_330812_ -> ByteBufCodecs.registry(ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY_KEY)
    );

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    T create();

    default boolean hasPreview() {
        return false;
    }

    default Optional<ParticleTimelinePreview> createPreview(T timeline, Level level) {
        return Optional.empty();
    }

    AbstractSpellPart getSpellPart();

}
