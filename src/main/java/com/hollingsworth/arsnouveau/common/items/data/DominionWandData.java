package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public record DominionWandData(Optional<GlobalPos> storedPos, Optional<Direction> face, boolean strict,
                               int storedEntityId) {

    public DominionWandData() {
        this(Optional.empty(), Optional.empty(), false, NULL_ENTITY);
    }

    public static final int NULL_ENTITY = -1;

    public static Codec<DominionWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.optionalFieldOf("pos").forGetter(DominionWandData::storedPos),
            Direction.CODEC.optionalFieldOf("face").forGetter(DominionWandData::face),
            Codec.BOOL.fieldOf("strict").forGetter(DominionWandData::strict),
            Codec.INT.fieldOf("entityId").forGetter(DominionWandData::storedEntityId)
    ).apply(instance, DominionWandData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, DominionWandData> STREAM = StreamCodec.composite(
            GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional),
            DominionWandData::storedPos,
            Direction.STREAM_CODEC.apply(ByteBufCodecs::optional),
            DominionWandData::face,
            ByteBufCodecs.BOOL,
            DominionWandData::strict,
            ByteBufCodecs.INT,
            DominionWandData::getStoredEntity,
            DominionWandData::new
    );

    public boolean hasStoredData() {
        return storedPos.isPresent() || storedEntityId != -1;
    }

    public @Nullable GlobalPos getValidPos() {
        return storedPos.orElse(null);
    }

    public int getStoredEntity() {
        return storedEntityId == 0 || storedEntityId == NULL_ENTITY ? NULL_ENTITY : storedEntityId;
    }

    public DominionWandData storePos(@Nullable GlobalPos pos) {
        return new DominionWandData(pos == null ? Optional.empty() : Optional.of(new GlobalPos(pos.dimension(), pos.pos().immutable())), face, strict, storedEntityId);
    }

    public DominionWandData storeEntity(int entityId) {
        return new DominionWandData(storedPos, face, strict, entityId);
    }

    public DominionWandData setFace(@Nullable Direction face) {
        return new DominionWandData(storedPos, Optional.ofNullable(face), strict, storedEntityId);
    }

    @Deprecated(forRemoval = true)
    public DominionWandData toggleMode() {
        return new DominionWandData(storedPos, face, !strict, storedEntityId);
    }

    public DominionWandData toggleMode(boolean strict) {
        return new DominionWandData(storedPos, face, strict, storedEntityId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DominionWandData that = (DominionWandData) o;
        return strict == that.strict && storedEntityId == that.storedEntityId && face == that.face && Objects.equals(storedPos, that.storedPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storedPos, face, strict, storedEntityId);
    }
}
