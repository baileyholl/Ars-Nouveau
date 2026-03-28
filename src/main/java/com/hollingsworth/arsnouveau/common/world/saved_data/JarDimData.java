package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JarDimData extends SavedData {
    public static Codec<Map<UUID, RotPos>> ENTERED_FROM_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, RotPos.CODEC.codec());

    private Map<UUID, RotPos> enteredFrom = new HashMap<>();
    private BlockPos spawnPos = new BlockPos(7, 2, 7);


    public void setEnteredFrom(UUID uuid, GlobalPos pos, Vec2 rot) {
        enteredFrom.put(uuid, new RotPos(pos, rot));
        setDirty();
    }

    public @Nullable RotPos getEnteredFrom(UUID uuid) {
        return enteredFrom.get(uuid);
    }

    public void setSpawnPos(BlockPos pos) {
        this.spawnPos = pos;
        setDirty();
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    // save() is no longer an override of SavedData in 1.21.11; called by the Codec in SavedDataType
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if (!enteredFrom.isEmpty()) {
            compoundTag.put("enteredFrom", ANCodecs.encode(ENTERED_FROM_CODEC, enteredFrom));
        }
        compoundTag.put("spawnPos", ANCodecs.encode(BlockPos.CODEC, spawnPos));
        return compoundTag;
    }

    public static JarDimData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        JarDimData data = new JarDimData();
        if (compoundTag.contains("enteredFrom")) {
            data.enteredFrom = new HashMap<>(ANCodecs.decode(ENTERED_FROM_CODEC, compoundTag.getCompoundOrEmpty("enteredFrom")));
        }
        if (compoundTag.contains("spawnPos")) {
            data.spawnPos = ANCodecs.decode(BlockPos.CODEC, compoundTag.get("spawnPos"));
        }
        return data;
    }

    // SavedDataType replaces SavedData.Factory in 1.21.11
    public static final SavedDataType<JarDimData> TYPE = new SavedDataType<>(
            "jar_data",
            level -> new JarDimData(),
            level -> net.minecraft.nbt.CompoundTag.CODEC.xmap(
                    tag -> JarDimData.load(tag, level.registryAccess()),
                    data -> data.save(new net.minecraft.nbt.CompoundTag(), level.registryAccess())
            )
    );

    public static JarDimData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public record RotPos(GlobalPos pos, Vec2 rot) {

        public static final MapCodec<RotPos> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                GlobalPos.CODEC.fieldOf("pos").forGetter(RotPos::pos),
                ANCodecs.VEC2.fieldOf("rot").forGetter(RotPos::rot)
        ).apply(instance, RotPos::new));
    }

}
