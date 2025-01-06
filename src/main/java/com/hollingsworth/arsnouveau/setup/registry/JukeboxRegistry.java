package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public class JukeboxRegistry {

    public static final ResourceKey<JukeboxSong> ARIA_BIBLIO = key("aria_biblio");
    public static final ResourceKey<JukeboxSong> WILD_HUNT = key("firel_the_wild_hunt");
    public static final ResourceKey<JukeboxSong> SOUND_OF_GLASS = key("thistle_the_sound_of_glass");


    private static ResourceKey<JukeboxSong> key(String p_345314_) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ArsNouveau.prefix(p_345314_));
    }


}
