package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.Objects;
import java.util.Optional;

public record ScryPosData(Optional<BlockPos> pos) {
    public static final Codec<ScryPosData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("pos").forGetter(ScryPosData::pos)
    ).apply(instance, ScryPosData::new));

    public ScryPosData(BlockPos pos){
        this(Optional.ofNullable(pos));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScryPosData that = (ScryPosData) o;
        return Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pos);
    }
}
