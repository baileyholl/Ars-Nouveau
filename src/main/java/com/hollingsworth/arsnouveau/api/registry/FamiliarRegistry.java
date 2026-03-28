package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FamiliarRegistry {
    private static ConcurrentHashMap<Identifier, AbstractFamiliarHolder> familiarHolderMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Identifier, FamiliarScript> familiarScriptMap = new ConcurrentHashMap<>();

    public static AbstractFamiliarHolder registerFamiliar(AbstractFamiliarHolder familiar) {
        return familiarHolderMap.put(familiar.getRegistryName(), familiar);
    }

    public static Map<Identifier, AbstractFamiliarHolder> getFamiliarHolderMap() {
        return familiarHolderMap;
    }

    public static Map<Identifier, FamiliarScript> getFamiliarScriptMap() {
        return familiarScriptMap;
    }
}
