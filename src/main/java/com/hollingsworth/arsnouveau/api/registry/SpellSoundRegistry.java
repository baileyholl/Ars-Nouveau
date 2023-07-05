package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public class SpellSoundRegistry {
    private static ConcurrentHashMap<ResourceLocation, SpellSound> spellSoundsRegistry = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<ResourceLocation, SpellSound> getSpellSoundsRegistry() {
        return spellSoundsRegistry;
    }

    public static SpellSound registerSpellSound(SpellSound sound) {
        return spellSoundsRegistry.put(sound.getId(), sound);
    }
}
