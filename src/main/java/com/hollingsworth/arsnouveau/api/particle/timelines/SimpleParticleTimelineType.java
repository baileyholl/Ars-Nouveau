package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record SimpleParticleTimelineType<T extends IParticleTimeline<T>>(AbstractSpellPart spellPart, MapCodec<T> codec,
                                                                         StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,
                                                                         Supplier<T> createDefault,
                                                                         BiFunction<T, Vec3, ParticleTimelinePreview> previewFactory) implements IParticleTimelineType<T> {
    public SimpleParticleTimelineType(AbstractSpellPart spellPart, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> createDefault) {
        this(spellPart, codec, streamCodec, createDefault, null);
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    @Override
    public T create() {
        return createDefault.get();
    }

    @Override
    public boolean hasPreview() {
        return previewFactory != null;
    }

    @Override
    public Optional<ParticleTimelinePreview> createPreview(T timeline, Vec3 origin) {
        return previewFactory == null ? Optional.empty() : Optional.of(previewFactory.apply(timeline, origin));
    }

    @Override
    public AbstractSpellPart getSpellPart() {
        return spellPart;
    }
}
