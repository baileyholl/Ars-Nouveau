package com.hollingsworth.arsnouveau.api.sound;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SpellSound {

    private ResourceLocation id;

    private SoundEvent soundEvent;

    private Component soundName;

    private float volume;

    private float pitch;

    public SpellSound(ResourceLocation id, SoundEvent soundEvent, Component soundName, float volume, float pitch) {
        this.id = id;
        this.soundEvent = soundEvent;
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SpellSound(ResourceLocation id,SoundEvent soundEvent, Component soundName) {
        this(id,soundEvent,soundName,1.0f,1.0f);
    }

    public SpellSound(SoundEvent soundEvent, Component soundName) {
        this(soundEvent.getRegistryName(),soundEvent,soundName, 1.0f, 1.0f);
    }
    public SpellSound(SoundEvent soundEvent, Component soundName, float volume, float pitch) {
        this(soundEvent.getRegistryName(),soundEvent,soundName,volume,pitch);
    }

    public ResourceLocation getId() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
    }

    public Component getSoundName() {
        return soundName;
    }

    public void setSoundName(Component soundName) {
        this.soundName = soundName;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
