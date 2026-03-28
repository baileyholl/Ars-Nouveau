package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpellSoundRegistry {
    private static ConcurrentHashMap<Identifier, SpellSound> spellSoundsRegistry = new ConcurrentHashMap<>();


    public static List<SpellSound> getSpellSounds() {
        return List.copyOf(spellSoundsRegistry.values());
    }

    public static SpellSound registerSpellSound(SpellSound sound) {
        spellSoundsRegistry.put(sound.getId(), sound);
        return sound;
    }

    public static SpellSound get(Identifier loc) {
        return spellSoundsRegistry.get(loc);
    }
}
