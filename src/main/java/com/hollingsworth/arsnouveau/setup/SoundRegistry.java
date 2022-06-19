package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod(ArsNouveau.MODID)
public class SoundRegistry {

    @ObjectHolder(value = "fire_family", registryName = "minecraft:sound_events")
    public static SoundEvent FIRE_FAMILY = new SoundEvent(new ResourceLocation(ArsNouveau.MODID, "fire_family"));
    @ObjectHolder(value = "empty", registryName = "minecraft:sound_events")
    public static SoundEvent EMPTY_SOUND_FAMILY = new SoundEvent(new ResourceLocation(ArsNouveau.MODID, "empty"));


    public static SpellSound FIRE_SPELL_SOUND;
    public static SpellSound EMPTY_SPELL_SOUND;

    public static void onSoundRegistry(final IForgeRegistry<SoundEvent> registry) {

        FIRE_SPELL_SOUND = new SpellSound(FIRE_FAMILY, Component.translatable("ars_nouveau.sound.fire_family"));
        EMPTY_SPELL_SOUND = new SpellSound(EMPTY_SOUND_FAMILY, Component.translatable("ars_nouveau.sound.empty"));
        registry.register("fire_family", FIRE_FAMILY);
        registry.register("empty", EMPTY_SOUND_FAMILY);

        ArsNouveauAPI.getInstance().registerSpellSound(FIRE_SPELL_SOUND);
        ArsNouveauAPI.getInstance().registerSpellSound(EMPTY_SPELL_SOUND);
    }

}
