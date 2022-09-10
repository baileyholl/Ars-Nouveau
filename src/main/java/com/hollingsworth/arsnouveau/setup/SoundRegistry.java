package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final String DEFAULT_SOUND_LIB = "default_family";
    public static final String GAIA_SOUND_LIB = "gaia_family";
    public static final String TEMPESTRY_SOUND_LIB = "tempestry_family";
    public static final String FIRE_SOUND_LIB = "fire_family";
    public static final String NO_SOUND_LIB = "empty";

    public static final String EA_CHANNEL = "ea_channel";
    public static final String EA_FINISH = "ea_finish";

    public static RegistryObject<SoundEvent> DEFAULT_FAMILY = SOUND_REG.register(DEFAULT_SOUND_LIB, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, DEFAULT_SOUND_LIB)));
    public static RegistryObject<SoundEvent> EMPTY_SOUND_FAMILY = SOUND_REG.register(NO_SOUND_LIB, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, NO_SOUND_LIB)));
    public static RegistryObject<SoundEvent> APPARATUS_CHANNEL = SOUND_REG.register(EA_CHANNEL, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, EA_CHANNEL)));
    public static RegistryObject<SoundEvent> APPARATUS_FINISH = SOUND_REG.register(EA_FINISH, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, EA_FINISH)));

    public static RegistryObject<SoundEvent> GAIA_FAMILY = SOUND_REG.register(GAIA_SOUND_LIB, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, GAIA_SOUND_LIB)));
    public static RegistryObject<SoundEvent> TEMPESTRY_FAMILY = SOUND_REG.register(TEMPESTRY_SOUND_LIB, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, TEMPESTRY_SOUND_LIB)));
    public static RegistryObject<SoundEvent> FIRE_FAMILY = SOUND_REG.register(FIRE_SOUND_LIB, () -> new SoundEvent(new ResourceLocation(ArsNouveau.MODID, FIRE_SOUND_LIB)));

    public static SpellSound DEFAULT_SPELL_SOUND;
    public static SpellSound EMPTY_SPELL_SOUND;
    public static SpellSound GAIA_SPELL_SOUND;
    public static SpellSound TEMPESTRY_SPELL_SOUND;
    public static SpellSound FIRE_SPELL_SOUND;

    public static void onSoundRegistry(final IForgeRegistry<SoundEvent> registry) {

    }

}
