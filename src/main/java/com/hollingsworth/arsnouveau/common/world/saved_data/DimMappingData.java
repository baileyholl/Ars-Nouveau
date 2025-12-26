package com.hollingsworth.arsnouveau.common.world.saved_data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Globally accessible from the overworld data, stores a mapping of jar dimension names to dimension keys.
 */
public class DimMappingData extends SavedData {

    private final Map<String, Entry> DIMENSIONS_BY_NAME = new HashMap<>();
    private final Map<ResourceLocation, Entry> DIMENSIONS_BY_KEY = new HashMap<>();

    public static SavedData.Factory<DimMappingData> factory() {
        return new SavedData.Factory<>(DimMappingData::new, DimMappingData::load, null);
    }

    public Entry getOrCreateByName(String name) {
        if (DIMENSIONS_BY_NAME.containsKey(name)) {
            return DIMENSIONS_BY_NAME.get(name);
        } else {
            Entry entry = new Entry(name, ResourceLocation.fromNamespaceAndPath("ars_nouveau", UUID.randomUUID().toString().toLowerCase()));
            DIMENSIONS_BY_NAME.put(name, entry);
            DIMENSIONS_BY_KEY.put(entry.key, entry);
            return entry;
        }
    }

    public Entry getByKey(ResourceLocation key) {
        return DIMENSIONS_BY_KEY.get(key);
    }

    public static DimMappingData load(CompoundTag tag, HolderLookup.Provider p_323806_) {
        DimMappingData data = new DimMappingData();
        ListTag signalList = tag.getList("DimList", 10);
        for (int i = 0; i < signalList.size(); i++) {
            var signal = new Entry(signalList.getCompound(i));
            data.DIMENSIONS_BY_NAME.put(signal.name, signal);
            data.DIMENSIONS_BY_KEY.put(signal.key, signal);
        }
        return data;
    }


    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
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
                .computeIfAbsent(factory(), "an_dimensions");
    }

    public record Entry(String name, ResourceLocation key) {
        public CompoundTag save(CompoundTag tag) {
            tag.putString("name", name);
            tag.putString("key", key.toString());
            return tag;
        }

        public Entry(CompoundTag tag) {
            this(tag.getString("name"), ResourceLocation.parse(tag.getString("key")));
        }
    }
}
