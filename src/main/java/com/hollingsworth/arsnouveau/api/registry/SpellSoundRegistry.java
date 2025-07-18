package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SpellSoundRegistry {
    public static SpellSound registerSpellSound(SpellSound sound) {
        Registry.registerForHolder(ANRegistries.SPELL_SOUNDS, sound.getId(), sound);
        return sound;
    }

    public static SpellSound get(ResourceLocation loc) {
        return ANRegistries.SPELL_SOUNDS.get(loc);
    }
}
