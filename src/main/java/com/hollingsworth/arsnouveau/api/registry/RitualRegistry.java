package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class RitualRegistry {
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

    public static void registerRitual(AbstractRitual ritual) {
        Registry.registerForHolder(ANRegistries.RITUAL_TYPES, ritual.getRegistryName(), ritual);
    }

    public static void registerTablet(RitualTablet tablet) {
        if (!ANRegistries.RITUAL_TYPES.containsValue(tablet.ritual)) {
            throw new IllegalStateException("Ritual '" + tablet.ritual.getRegistryName() + "' for '" + tablet.getDescriptionId() + "' is not registered");
        }

        Registry.registerForHolder(ANRegistries.RITUAL_TABLETS, tablet.ritual.getRegistryName(), tablet);
    }
}
