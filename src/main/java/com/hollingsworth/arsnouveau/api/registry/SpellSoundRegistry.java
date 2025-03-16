package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SpellSoundRegistry {
    public static final Registry<SpellSound> SPELL_SOUNDS = new MappedRegistry<>(ResourceKey.createRegistryKey(ArsNouveau.prefix("spell_sounds")), Lifecycle.stable());

    public static SpellSound registerSpellSound(SpellSound sound) {
        Registry.registerForHolder(SPELL_SOUNDS, sound.getId(), sound);
        return sound;
    }

    public static SpellSound get(ResourceLocation loc) {
        return SPELL_SOUNDS.get(loc);
    }
}
