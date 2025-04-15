package com.hollingsworth.arsnouveau.api.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapRegistry <K, V>{

    public final Map<K, V> map = new ConcurrentHashMap<>();

    public V register(K id, V value){
        return map.put(id, value);
    }
}
