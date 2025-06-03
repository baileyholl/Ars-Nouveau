package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.IConfigurable;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RitualRegistry {

    private static ConcurrentHashMap<ResourceLocation, AbstractRitual> ritualMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<ResourceLocation, RitualTablet> ritualItemMap = new ConcurrentHashMap<>();

    public static @Nullable AbstractRitual getRitual(ResourceLocation id) {
        if (!ritualMap.containsKey(id))
            return null;
        try {
            return ritualMap.get(id).getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<ResourceLocation, AbstractRitual> getRitualMap() {
        return ritualMap;
    }

    public static Map<ResourceLocation, RitualTablet> getRitualItemMap() {
        return ritualItemMap;
    }

    public static AbstractRitual registerRitual(AbstractRitual ritual) {
        IConfigurable.register(ritual);
        return ritualMap.put(ritual.getRegistryName(), ritual);
    }
}
