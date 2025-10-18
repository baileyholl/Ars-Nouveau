package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JarDimData extends SavedData {
    public static Codec<Map<UUID, GlobalPos>> ENTERED_FROM_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, GlobalPos.CODEC);

    private Map<UUID, GlobalPos> enteredFrom = new HashMap<>();


    public void setEnteredFrom(UUID uuid, GlobalPos pos) {
        enteredFrom.put(uuid, pos);
        setDirty();
    }

    public @Nullable GlobalPos getEnteredFrom(UUID uuid) {
        return enteredFrom.get(uuid);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("enteredFrom", ANCodecs.encode(ENTERED_FROM_CODEC, enteredFrom));
        return compoundTag;
    }

    public static JarDimData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        JarDimData data = new JarDimData();
        data.enteredFrom = ANCodecs.decode(ENTERED_FROM_CODEC, compoundTag.getCompound("enteredFrom"));
        return data;
    }

    public static JarDimData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(JarDimData::new, JarDimData::load, null), "jar_data");
    }

}
