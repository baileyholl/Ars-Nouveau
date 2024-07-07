package com.hollingsworth.arsnouveau.api.sound;

import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Objects;

public class SpellSound {

    public static MapCodec<SpellSound> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SoundEvent.CODEC.fieldOf("soundEvent").forGetter(s -> s.soundEvent),
            ComponentSerialization.CODEC.fieldOf("soundName").forGetter(s -> s.soundName)
    ).apply(instance, SpellSound::new));

    public static StreamCodec<RegistryFriendlyByteBuf, SpellSound> STREAM = StreamCodec.of(
            (buf, val) -> buf.writeResourceLocation(val.getId()),
            buf -> SpellSoundRegistry.getSpellSoundsRegistry().get(buf.readResourceLocation())
    );


    private Holder<SoundEvent> soundEvent;

    private Component soundName;

    public SpellSound(Holder<SoundEvent> soundEvent, Component soundName) {
        this.soundEvent = soundEvent;
        this.soundName = soundName;
    }

    public ResourceLocation getId() {
        return soundEvent.unwrapKey().get().location();
    }


    public Holder<SoundEvent> getSoundEvent() {
        return soundEvent;
    }

    public Component getSoundName() {
        return soundName;
    }

    public ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath(this.getId().getNamespace(), "textures/sounds/" + this.getId().getPath() + ".png");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpellSound that = (SpellSound) o;
        return Objects.equals(soundEvent, that.soundEvent) && Objects.equals(soundName, that.soundName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soundEvent, soundName);
    }

    @Override
    public String toString() {
        return "SpellSound{" +
                "soundEvent=" + soundEvent +
                ", soundName=" + soundName +
                '}';
    }
}
