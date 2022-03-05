package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ArsNouveau.MODID)
public class SoundRegistry {

    @ObjectHolder("fire_family")
    public static SoundEvent FIRE_FAMILY = new SoundEvent(new ResourceLocation(ArsNouveau.MODID, "fire_family")).setRegistryName(new ResourceLocation(ArsNouveau.MODID, "fire_family"));


    public static SpellSound FIRE_SPELL_SOUND;

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> soundRegistryEvent) {
            soundRegistryEvent.getRegistry().registerAll(
                   FIRE_FAMILY
            );
            FIRE_SPELL_SOUND = new SpellSound(FIRE_FAMILY, new TranslatableComponent("ars_nouveau.sound.fire_family"));
            ArsNouveauAPI.getInstance().registerSpellSound(FIRE_SPELL_SOUND);
        }
    }

}
