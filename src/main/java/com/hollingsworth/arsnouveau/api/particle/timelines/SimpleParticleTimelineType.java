package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record SimpleParticleTimelineType <T extends IParticleTimeline<T>>(AbstractSpellPart spellPart, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> createDefault) implements IParticleTimelineType<T> {
    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    @Override
    public T create(){
        return createDefault.get();
    }


    @Override
    public AbstractSpellPart getSpellPart() {
        return spellPart;
    }
}
