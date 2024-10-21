package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public record DominionWandData(Optional<BlockPos> storedPos, Optional<Direction> face, boolean strict,
                               int storedEntityId, boolean remove) {

    public DominionWandData() {
        this(Optional.empty(), Optional.empty(), false, NULL_ENTITY, false);
    }

    public static final int NULL_ENTITY = -1;

    public static Codec<DominionWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("pos").forGetter(DominionWandData::storedPos),
            Direction.CODEC.optionalFieldOf("face").forGetter(DominionWandData::face),
            Codec.BOOL.fieldOf("strict").forGetter(DominionWandData::strict),
            Codec.INT.fieldOf("entityId").forGetter(DominionWandData::storedEntityId),
            Codec.BOOL.fieldOf("remove").forGetter(DominionWandData::remove)
    ).apply(instance, DominionWandData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, DominionWandData> STREAM = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional),
            DominionWandData::storedPos,
            Direction.STREAM_CODEC.apply(ByteBufCodecs::optional),
            DominionWandData::face,
            ByteBufCodecs.BOOL,
            DominionWandData::strict,
            ByteBufCodecs.INT,
            DominionWandData::getStoredEntity,
            ByteBufCodecs.BOOL,
            DominionWandData::remove,
            DominionWandData::new
    );

    public boolean hasStoredData() {
        return storedPos.isPresent() || storedEntityId != -1;
    }

    public @Nullable BlockPos getValidPos() {
        return storedPos.orElse(null);
    }

    public int getStoredEntity() {
        return storedEntityId == 0 || storedEntityId == NULL_ENTITY ? NULL_ENTITY : storedEntityId;
    }

    public DominionWandData storePos(@Nullable BlockPos pos) {
        return new DominionWandData(pos == null ? Optional.empty() : Optional.of(pos.immutable()), face, strict, storedEntityId, remove);
    }

    public DominionWandData storeEntity(int entityId) {
        return new DominionWandData(storedPos, face, strict, entityId, remove);
    }

    public DominionWandData setFace(@Nullable Direction face) {
        return new DominionWandData(storedPos, Optional.ofNullable(face), strict, storedEntityId, remove);
    }

    @Deprecated(forRemoval = true)
    public DominionWandData toggleMode() {
        return new DominionWandData(storedPos, face, !strict, storedEntityId, remove);
    }

    public DominionWandData toggleStrictMode(boolean strict) {
        return new DominionWandData(storedPos, face, strict, storedEntityId, remove);
    }

    public DominionWandData toggleRemoveMode(boolean remove) {
        return new DominionWandData(storedPos, face, strict, storedEntityId, remove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DominionWandData that = (DominionWandData) o;
        return strict == that.strict && remove == that.remove && storedEntityId == that.storedEntityId && face == that.face && Objects.equals(storedPos, that.storedPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storedPos, face, strict, storedEntityId);
    }
}
