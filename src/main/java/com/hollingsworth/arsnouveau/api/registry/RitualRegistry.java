package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RitualRegistry {
    public static final Registry<AbstractRitual> RITUAL_TYPES = new MappedRegistry<>(ResourceKey.createRegistryKey(ArsNouveau.prefix("ritual_types")), Lifecycle.stable());

    private static ConcurrentHashMap<ResourceLocation, RitualTablet> ritualItemMap = new ConcurrentHashMap<>();

    public static @Nullable AbstractRitual getRitual(ResourceLocation id) {
        AbstractRitual ritual = RITUAL_TYPES.get(id);
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
        return RITUAL_TYPES.entrySet().stream().collect(Collectors.toUnmodifiableMap(e -> e.getKey().location(), Map.Entry::getValue));
    }

    public static Map<ResourceLocation, RitualTablet> getRitualItemMap() {
        return ritualItemMap;
    }

    public static AbstractRitual registerRitual(AbstractRitual ritual) {
        Registry.registerForHolder(RITUAL_TYPES, ritual.getRegistryName(), ritual);
        return ritual;
    }
}
