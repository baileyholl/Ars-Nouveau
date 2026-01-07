package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;

import java.util.Objects;
import java.util.Optional;

public record ScryPosData(Optional<GlobalPos> pos) {
    public static Codec<ScryPosData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.optionalFieldOf("global_pos").forGetter(ScryPosData::pos)
    ).apply(instance, ScryPosData::new));

    public ScryPosData(GlobalPos pos) {
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
