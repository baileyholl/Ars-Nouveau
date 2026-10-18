package com.hollingsworth.arsnouveau.common.entity.statemachine.memory;

import com.google.common.collect.Maps;

import java.util.Map;

public class MemoryMap {
    private Map<MemoryType<?>, Object> memory = Maps.newHashMap();

    public MemoryMap() {

    }

    public <T> void put(MemoryType<T> type, T value) {
        memory.put(type, value);
    }

    public <T> T get(MemoryType<T> type) {
        return (T) memory.get(type);
    }
}
