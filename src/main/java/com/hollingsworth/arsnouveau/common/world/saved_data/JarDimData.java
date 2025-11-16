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

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("enteredFrom", ANCodecs.encode(ENTERED_FROM_CODEC, enteredFrom));
        compoundTag.put("spawnPos", ANCodecs.encode(BlockPos.CODEC, spawnPos));
        return compoundTag;
    }

    public static JarDimData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        JarDimData data = new JarDimData();
        data.enteredFrom = new HashMap<>(ANCodecs.decode(ENTERED_FROM_CODEC, compoundTag.getCompound("enteredFrom")));
        data.spawnPos = ANCodecs.decode(BlockPos.CODEC, compoundTag.getCompound("spawnPos"));
        return data;
    }

    public static JarDimData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(JarDimData::new, JarDimData::load, null), "jar_data");
    }

    public record RotPos(GlobalPos pos, Vec2 rot) {

        public static final MapCodec<RotPos> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                GlobalPos.CODEC.fieldOf("pos").forGetter(RotPos::pos),
                ANCodecs.VEC2.fieldOf("rot").forGetter(RotPos::rot)
        ).apply(instance, RotPos::new));
    }

}
