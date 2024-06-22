package com.hollingsworth.arsnouveau.api.sound;

import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.util.Objects;

public class SpellSound {

    private ResourceLocation id;

    private SoundEvent soundEvent;

    private Component soundName;

    public SpellSound(ResourceLocation id, SoundEvent soundEvent, Component soundName) {
        this.id = id;
        this.soundEvent = soundEvent;
        this.soundName = soundName;
    }

    public SpellSound(SoundEvent soundEvent, Component soundName) {
        this(soundEvent.getLocation(), soundEvent, soundName);
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

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        return tag;
    }

    public static @Nullable SpellSound fromTag(CompoundTag tag) {
        if (!tag.contains("id"))
            return null;

        ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
        return SpellSoundRegistry.getSpellSoundsRegistry().get(id);
    }

    public ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath(this.id.getNamespace(), "textures/sounds/" + this.id.getPath() + ".png");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpellSound that = (SpellSound) o;
        return Objects.equals(id, that.id) && Objects.equals(soundEvent, that.soundEvent) && Objects.equals(soundName, that.soundName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soundEvent, soundName);
    }

    @Override
    public String toString() {
        return "SpellSound{" +
                "id=" + id +
                ", soundEvent=" + soundEvent +
                ", soundName=" + soundName +
                '}';
    }
}
