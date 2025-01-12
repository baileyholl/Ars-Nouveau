package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpellSoundRegistry {
    private static ConcurrentHashMap<ResourceLocation, SpellSound> spellSoundsRegistry = new ConcurrentHashMap<>();


    public static List<SpellSound> getSpellSounds() {
        return List.copyOf(spellSoundsRegistry.values());
    }

    public static SpellSound registerSpellSound(SpellSound sound) {
        return spellSoundsRegistry.put(sound.getId(), sound);
    }

    public static SpellSound get(ResourceLocation loc) {
        return spellSoundsRegistry.get(loc);
    }
}
