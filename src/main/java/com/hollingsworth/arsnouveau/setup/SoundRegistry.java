package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

public class SoundRegistry {

    public static final String FIRE_FAMILY_LIB = "fire_family";
    public static final String NO_SOUND_LIB = "empty";

    @ObjectHolder(value =  ArsNouveau.MODID + ":" + FIRE_FAMILY_LIB, registryName = "minecraft:sound_events")
    public static SoundEvent FIRE_FAMILY = new SoundEvent(new ResourceLocation(ArsNouveau.MODID, FIRE_FAMILY_LIB));
    @ObjectHolder(value = ArsNouveau.MODID + ":" +NO_SOUND_LIB, registryName = "minecraft:sound_events")
    public static SoundEvent EMPTY_SOUND_FAMILY = new SoundEvent(new ResourceLocation(ArsNouveau.MODID, NO_SOUND_LIB));


    public static SpellSound FIRE_SPELL_SOUND;
    public static SpellSound EMPTY_SPELL_SOUND;

    public static void onSoundRegistry(final IForgeRegistry<SoundEvent> registry) {

        FIRE_SPELL_SOUND = new SpellSound(FIRE_FAMILY, Component.translatable("ars_nouveau.sound.fire_family"));
        EMPTY_SPELL_SOUND = new SpellSound(EMPTY_SOUND_FAMILY, Component.translatable("ars_nouveau.sound.empty"));
        registry.register(FIRE_FAMILY_LIB, FIRE_FAMILY);
        registry.register(NO_SOUND_LIB, EMPTY_SOUND_FAMILY);

        ArsNouveauAPI.getInstance().registerSpellSound(FIRE_SPELL_SOUND);
        ArsNouveauAPI.getInstance().registerSpellSound(EMPTY_SPELL_SOUND);
    }

}
