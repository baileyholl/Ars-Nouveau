package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_REG = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);
    public static final String DEFAULT_SOUND_LIB = "fire_family";
    public static final String GAIA_SOUND_LIB = "gaia_family";
    public static final String TEMPESTRY_SOUND_LIB = "tempestry_family";
    public static final String FIRE_SOUND_LIB = "fire_family_2";
    public static final String NO_SOUND_LIB = "empty";

    public static final String EA_CHANNEL = "ea_channel";
    public static final String EA_FINISH = "ea_finish";

    public static DeferredHolder<SoundEvent, SoundEvent> DEFAULT_FAMILY = SOUND_REG.register(DEFAULT_SOUND_LIB, () -> makeSound(DEFAULT_SOUND_LIB));
    public static DeferredHolder<SoundEvent, SoundEvent> EMPTY_SOUND_FAMILY = SOUND_REG.register(NO_SOUND_LIB, () -> makeSound(NO_SOUND_LIB));
    public static DeferredHolder<SoundEvent, SoundEvent> APPARATUS_CHANNEL = SOUND_REG.register(EA_CHANNEL, () -> makeSound(EA_CHANNEL));
    public static DeferredHolder<SoundEvent, SoundEvent> APPARATUS_FINISH = SOUND_REG.register(EA_FINISH, () -> makeSound(EA_FINISH));

    public static DeferredHolder<SoundEvent, SoundEvent> GAIA_FAMILY = SOUND_REG.register(GAIA_SOUND_LIB, () -> makeSound(GAIA_SOUND_LIB));
    public static DeferredHolder<SoundEvent, SoundEvent> TEMPESTRY_FAMILY = SOUND_REG.register(TEMPESTRY_SOUND_LIB, () -> makeSound(TEMPESTRY_SOUND_LIB));
    public static DeferredHolder<SoundEvent, SoundEvent> FIRE_FAMILY = SOUND_REG.register(FIRE_SOUND_LIB, () -> makeSound(FIRE_SOUND_LIB));
    public static DeferredHolder<SoundEvent, SoundEvent> ARIA_BIBLIO = SOUND_REG.register("aria_biblio", () -> makeSound("aria_biblio"));
    public static DeferredHolder<SoundEvent, SoundEvent> WILD_HUNT = SOUND_REG.register("firel_the_wild_hunt", () -> makeSound("firel_the_wild_hunt"));

    public static DeferredHolder<SoundEvent, SoundEvent> SOUND_OF_GLASS = SOUND_REG.register("thistle_the_sound_of_glass", () -> makeSound("thistle_the_sound_of_glass"));
    public static DeferredHolder<SoundEvent, SoundEvent> DOMINION_WAND_FAIL = SOUND_REG.register("dominion_wand_fail", () -> makeSound("dominion_wand_fail"));
    public static DeferredHolder<SoundEvent, SoundEvent> DOMINION_WAND_SUCCESS = SOUND_REG.register("dominion_wand_success", () -> makeSound("dominion_wand_success"));
    public static DeferredHolder<SoundEvent, SoundEvent> DOMINION_WAND_SELECT = SOUND_REG.register("dominion_wand_select", () -> makeSound("dominion_wand_select"));
    public static DeferredHolder<SoundEvent, SoundEvent> DOMINION_WAND_CLEAR = SOUND_REG.register("dominion_wand_clear", () -> makeSound("dominion_wand_clear"));

    public static SpellSound DEFAULT_SPELL_SOUND = new SpellSound(SoundRegistry.DEFAULT_FAMILY, Component.translatable("ars_nouveau.sound.default_family"), ArsNouveau.prefix(DEFAULT_SOUND_LIB));
    public static SpellSound EMPTY_SPELL_SOUND = new SpellSound(SoundRegistry.EMPTY_SOUND_FAMILY, Component.translatable("ars_nouveau.sound.empty"), ArsNouveau.prefix(NO_SOUND_LIB));
    public static SpellSound GAIA_SPELL_SOUND = new SpellSound(SoundRegistry.GAIA_FAMILY, Component.translatable("ars_nouveau.sound.gaia_family"), ArsNouveau.prefix(GAIA_SOUND_LIB));
    public static SpellSound TEMPESTRY_SPELL_SOUND = new SpellSound(SoundRegistry.TEMPESTRY_FAMILY, Component.translatable("ars_nouveau.sound.tempestry_family"), ArsNouveau.prefix(TEMPESTRY_SOUND_LIB));
    public static SpellSound FIRE_SPELL_SOUND = new SpellSound(SoundRegistry.FIRE_FAMILY, Component.translatable("ars_nouveau.sound.fire_family"), ArsNouveau.prefix(FIRE_SOUND_LIB));

    static SoundEvent makeSound(String name) {
        return SoundEvent.createVariableRangeEvent(ArsNouveau.prefix( name));
    }

}
