package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RitualRegistry {

    private static ConcurrentHashMap<ResourceLocation, RitualTablet> ritualItemMap = new ConcurrentHashMap<>();

    public static @Nullable AbstractRitual getRitual(ResourceLocation id) {
        AbstractRitual ritual = ANRegistries.RITUAL_TYPES.get(id);
        if (ritual == null) {
            return null;
        }

        try {
            return ritual.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public static Map<ResourceLocation, AbstractRitual> getRitualMap() {
        return ANRegistries.RITUAL_TYPES.entrySet().stream().collect(Collectors.toUnmodifiableMap(e -> e.getKey().location(), Map.Entry::getValue));
    }

    public static Map<ResourceLocation, RitualTablet> getRitualItemMap() {
        return ritualItemMap;
    }

    public static AbstractRitual registerRitual(AbstractRitual ritual) {
        Registry.registerForHolder(ANRegistries.RITUAL_TYPES, ritual.getRegistryName(), ritual);
        return ritual;
    }
}
