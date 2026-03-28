package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class DocPlayerData {

    public static Identifier lastOpenedEntry = null;
    public static int lastOpenedPage = 0;
    public static BaseDocScreen previousScreen = null;

    public static List<Identifier> bookmarks = new ArrayList<>();
    public static List<SpellSound> favoriteSounds = new ArrayList<>();
    public static List<ParticleType<?>> favoriteParticles = new ArrayList<>();

}
