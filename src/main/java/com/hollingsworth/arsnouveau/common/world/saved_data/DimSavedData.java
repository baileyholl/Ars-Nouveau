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

public class DimSavedData extends SavedData {

    private final Map<String, Entry> DIMENSIONS = new HashMap<>();


    public static SavedData.Factory<DimSavedData> factory() {
        return new SavedData.Factory<>(DimSavedData::new, DimSavedData::load, null);
    }

    public Entry getOrCreate(String name) {
        return DIMENSIONS.computeIfAbsent(name, n -> new Entry(n, ResourceLocation.fromNamespaceAndPath("ars_nouveau", UUID.randomUUID().toString().toLowerCase())));
    }


    public static DimSavedData load(CompoundTag tag, HolderLookup.Provider p_323806_) {
        DimSavedData data = new DimSavedData();
        ListTag signalList = tag.getList("DimList", 10);
        for (int i = 0; i < signalList.size(); i++) {
            var signal = new Entry(signalList.getCompound(i));
            data.DIMENSIONS.put(signal.name, signal);
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
        for (var signal : DIMENSIONS.values()) {
            signalList.add(signal.save(new CompoundTag()));
        }
        pCompoundTag.put("DimList", signalList);
        return pCompoundTag;
    }

    public static DimSavedData from(ServerLevel level) {
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
