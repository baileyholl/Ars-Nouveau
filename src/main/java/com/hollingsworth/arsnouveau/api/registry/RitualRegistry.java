package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RitualRegistry {

    private static ConcurrentHashMap<Identifier, AbstractRitual> ritualMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Identifier, RitualTablet> ritualItemMap = new ConcurrentHashMap<>();

    public static @Nullable AbstractRitual getRitual(Identifier id) {
        if (!ritualMap.containsKey(id))
            return null;
        try {
            return ritualMap.get(id).getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Identifier, AbstractRitual> getRitualMap() {
        return ritualMap;
    }

    public static Map<Identifier, RitualTablet> getRitualItemMap() {
        return ritualItemMap;
    }

    public static AbstractRitual registerRitual(AbstractRitual ritual) {
        return ritualMap.put(ritual.getRegistryName(), ritual);
    }
}
