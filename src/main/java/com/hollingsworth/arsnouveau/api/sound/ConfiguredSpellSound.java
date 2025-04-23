package com.hollingsworth.arsnouveau.api.sound;

import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.Objects;

public class ConfiguredSpellSound implements Cloneable{

    public static final MapCodec<ConfiguredSpellSound> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SpellSound.CODEC.codec().optionalFieldOf("sound", SoundRegistry.DEFAULT_SPELL_SOUND).forGetter(s -> s.sound),
            Codec.FLOAT.optionalFieldOf("volume", 1.0f).forGetter(s -> s.volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1.0f).forGetter(s -> s.pitch)
    ).apply(instance, ConfiguredSpellSound::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,ConfiguredSpellSound> STREAM = StreamCodec.of(
            (buf, val) -> {
                SpellSound.STREAM.encode(buf, val.getSound());
                buf.writeFloat(val.getVolume());
                buf.writeFloat(val.getPitch());
            },
            buf -> {
                SpellSound sound = SpellSound.STREAM.decode(buf);
                float volume = buf.readFloat();
                float pitch = buf.readFloat();
                return new ConfiguredSpellSound(sound, volume, pitch);
            }
    );

    public static ConfiguredSpellSound EMPTY = new ConfiguredSpellSound(SoundRegistry.EMPTY_SPELL_SOUND, 1, 1); // If the user wants no sound, make it empty.
    public static ConfiguredSpellSound DEFAULT = new ConfiguredSpellSound(SoundRegistry.DEFAULT_SPELL_SOUND, 1, 1); // The default sound to be returned for null casters.

    private final SpellSound sound;
    private final float volume;
    private final float pitch;

    public ConfiguredSpellSound(SpellSound sound) {
        this(sound, 1, 1);
    }

    public ConfiguredSpellSound(@Nullable SpellSound spellSound, float volume, float pitch) {
        this.sound = spellSound ;
        this.volume = volume;
        this.pitch = pitch;
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

    @Override
    public ConfiguredSpellSound clone() {
        try {
            ConfiguredSpellSound clone = (ConfiguredSpellSound) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public SpellSound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
