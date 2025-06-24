package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LevelEntityMap {
    public Map<String, Set<UUID>> entityMap = new ConcurrentHashMap<>();

    public void addEntity(Level level, UUID uuid) {
        addEntity(level.dimension().location().toString(), uuid);
    }

    public void addEntity(String key, UUID uuid) {
        if (!entityMap.containsKey(key))
            entityMap.put(key, ConcurrentHashMap.newKeySet());
        entityMap.get(key).add(uuid);
    }

    public boolean containsEntity(Level level, UUID uuid) {
        return containsEntity(level.dimension().location().toString(), uuid);
    }

    public boolean containsEntity(String key, UUID uuid) {
        if (!entityMap.containsKey(key))
            return false;
        return entityMap.get(key).contains(uuid);
    }

    public void removeEntity(Level level, UUID uuid) {
        removeEntity(level.dimension().location().toString(), uuid);
    }

    public void removeEntity(String key, UUID uuid) {
        if (!entityMap.containsKey(key))
            return;
        entityMap.get(key).remove(uuid);
    }

    public Set<UUID> getEntities(Level level) {
        return getEntities(level.dimension().location().toString());
    }

    public Set<UUID> getEntities(String key) {
        if (!entityMap.containsKey(key))
            return ConcurrentHashMap.newKeySet();
        return entityMap.get(key);
    }
}
