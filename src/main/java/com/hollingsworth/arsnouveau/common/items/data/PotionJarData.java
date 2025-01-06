package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.Objects;

public record PotionJarData(int fill, PotionContents contents, boolean locked) {

    public static final Codec<PotionJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("fill").forGetter(PotionJarData::fill),
            PotionContents.CODEC.fieldOf("contents").forGetter(PotionJarData::contents),
            Codec.BOOL.fieldOf("locked").forGetter(PotionJarData::locked)
    ).apply(instance, PotionJarData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionJarData> STREAM = StreamCodec.composite(
            ByteBufCodecs.INT,
            PotionJarData::fill,
            PotionContents.STREAM_CODEC,
            PotionJarData::contents,
            ByteBufCodecs.BOOL,
            PotionJarData::locked,
            PotionJarData::new
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotionJarData that = (PotionJarData) o;
        return fill == that.fill && locked == that.locked && Objects.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fill, contents, locked);
    }
}
