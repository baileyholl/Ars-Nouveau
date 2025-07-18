package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import net.minecraft.core.Registry;

public class FamiliarRegistry {
    public static void registerFamiliar(AbstractFamiliarHolder familiar) {
        Registry.registerForHolder(ANRegistries.FAMILIAR_TYPES, familiar.getRegistryName(), familiar);
    }

    public static void registerScript(FamiliarScript script) {
        if (!ANRegistries.FAMILIAR_TYPES.containsValue(script.familiar)) {
            throw new IllegalStateException("Familiar '" + script.familiar.getRegistryName() + "' for '" + script.getDescriptionId() + "' is not registered");
        }

        Registry.registerForHolder(ANRegistries.FAMILIAR_SCRIPTS, script.familiar.getRegistryName(), script);
    }
}
