package com.hollingsworth.arsnouveau.common.world.saved_data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Globally accessible from the overworld data, stores a mapping of jar dimension names to dimension keys.
 */
public class DimMappingData extends SavedData {

    private final Map<String, Entry> DIMENSIONS_BY_NAME = new HashMap<>();
    private final Map<Identifier, Entry> DIMENSIONS_BY_KEY = new HashMap<>();

    // SavedDataType replaces SavedData.Factory in 1.21.11
    public static final SavedDataType<DimMappingData> TYPE = new SavedDataType<>(
            "an_dimensions",
            level -> new DimMappingData(),
            level -> net.minecraft.nbt.CompoundTag.CODEC.xmap(
                    tag -> DimMappingData.load(tag, level.registryAccess()),
                    data -> data.save(new net.minecraft.nbt.CompoundTag(), level.registryAccess())
            )
    );

    public Entry getOrCreateByName(String name) {
        if (DIMENSIONS_BY_NAME.containsKey(name)) {
            return DIMENSIONS_BY_NAME.get(name);
        } else {
            Entry entry = new Entry(name, Identifier.fromNamespaceAndPath("ars_nouveau", UUID.randomUUID().toString().toLowerCase()));
            DIMENSIONS_BY_NAME.put(name, entry);
            DIMENSIONS_BY_KEY.put(entry.key, entry);
            return entry;
        }
    }

    public Entry getByKey(Identifier key) {
        return DIMENSIONS_BY_KEY.get(key);
    }

    public static DimMappingData load(CompoundTag tag, HolderLookup.Provider p_323806_) {
        DimMappingData data = new DimMappingData();
        // 1.21.11: getList now returns Optional; use getListOrEmpty. getCompound returns Optional too.
        ListTag signalList = tag.getListOrEmpty("DimList");
        for (int i = 0; i < signalList.size(); i++) {
            var signal = new Entry(signalList.getCompoundOrEmpty(i));
            data.DIMENSIONS_BY_NAME.put(signal.name, signal);
            data.DIMENSIONS_BY_KEY.put(signal.key, signal);
        }
        return data;
    }


    @Override
    public boolean isDirty() {
        return true;
    }

    // save() is no longer an override of SavedData in 1.21.11; used by the Codec in SavedDataType
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider pRegistries) {
        ListTag signalList = new ListTag();
        for (var signal : DIMENSIONS_BY_NAME.values()) {
            signalList.add(signal.save(new CompoundTag()));
        }
        pCompoundTag.put("DimList", signalList);
        return pCompoundTag;
    }

    public static DimMappingData from(ServerLevel level) {
        return level.getServer().overworld().getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public record Entry(String name, Identifier key) {
        public CompoundTag save(CompoundTag tag) {
            tag.putString("name", name);
            tag.putString("key", key.toString());
            return tag;
        }

        public Entry(CompoundTag tag) {
            // 1.21.11: getString returns Optional; use getStringOr
            this(tag.getStringOr("name", ""), Identifier.parse(tag.getStringOr("key", "minecraft:overworld")));
        }
    }
}
