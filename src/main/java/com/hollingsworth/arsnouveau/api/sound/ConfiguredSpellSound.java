package com.hollingsworth.arsnouveau.api.sound;

import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Objects;

public class ConfiguredSpellSound {
    public static ConfiguredSpellSound EMPTY = new ConfiguredSpellSound(SoundRegistry.EMPTY_SPELL_SOUND, 1, 1); // If the user wants no sound, make it empty.
    public static ConfiguredSpellSound DEFAULT = new ConfiguredSpellSound(SoundRegistry.DEFAULT_SPELL_SOUND, 1, 1); // The default sound to be returned for null casters.

    public @Nullable SpellSound sound;
    public float volume;
    public float pitch;

    public ConfiguredSpellSound(@Nullable SpellSound sound) {
        this(sound, 1, 1);
    }

    public ConfiguredSpellSound(@Nullable SpellSound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("soundTag", sound == null ? new CompoundTag() : sound.serialize());
        tag.putFloat("volume", volume);
        tag.putFloat("pitch", pitch);
        return tag;
    }

    public static ConfiguredSpellSound fromTag(CompoundTag tag) {
        SpellSound sound = SpellSound.fromTag(tag.getCompound("soundTag"));
        float volume = tag.getFloat("volume");
        float pitch = tag.getFloat("pitch");
        return new ConfiguredSpellSound(sound, volume, pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguredSpellSound that = (ConfiguredSpellSound) o;
        return Float.compare(that.volume, volume) == 0 && Float.compare(that.pitch, pitch) == 0 && Objects.equals(sound, that.sound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sound, volume, pitch);
    }

    @Override
    public String toString() {
        return "ConfiguredSpellSound{" +
                "sound=" + sound +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}
