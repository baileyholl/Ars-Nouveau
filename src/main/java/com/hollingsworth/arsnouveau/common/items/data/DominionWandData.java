package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;

public record DominionWandData(BlockPos storedPos, Direction face, boolean strict, int storedEntityId) {

    public DominionWandData(BlockPos storedPos, Direction face, boolean strict){
        this(storedPos, face, strict, NULL_ENTITY);
    }

    public DominionWandData(int storedEntityId){
        this(BlockPos.ZERO, Direction.DOWN, false, storedEntityId);
    }

    public DominionWandData(){
        this(null, null, false, NULL_ENTITY);
    }

    public static final int NULL_ENTITY = -1;

    public static Codec<DominionWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(DominionWandData::storedPos),
            Direction.CODEC.fieldOf("face").forGetter(DominionWandData::face),
            Codec.BOOL.fieldOf("strict").forGetter(DominionWandData::strict),
            Codec.INT.fieldOf("entityId").forGetter(DominionWandData::storedEntityId)
    ).apply(instance, DominionWandData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, DominionWandData> STREAM = CheatSerializer.create(DominionWandData.CODEC);

    public boolean hasStoredData(){
        return storedPos != null || storedEntityId != -1;
    }

    public @Nullable BlockPos getValidPos(){
        return storedPos == BlockPos.ZERO || storedPos == null ? null : storedPos;
    }

    public int getStoredEntity(){
        return storedEntityId == 0 || storedEntityId == NULL_ENTITY ? NULL_ENTITY : storedEntityId;
    }

    public DominionWandData storePos(@Nullable BlockPos pos){
        return new DominionWandData(pos == null ? null : pos.immutable(), face, strict, storedEntityId);
    }

    public DominionWandData storeEntity(int entityId){
        return new DominionWandData(storedPos, face, strict, entityId);
    }

    public DominionWandData setFace(Direction face){
        return new DominionWandData(storedPos, face, strict, storedEntityId);
    }

    public DominionWandData toggleMode(){
        return new DominionWandData(storedPos, face, !strict, storedEntityId);
    }

}
