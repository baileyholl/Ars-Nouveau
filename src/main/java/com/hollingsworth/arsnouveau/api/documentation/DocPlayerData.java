package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class DocPlayerData {

    public static ResourceLocation lastOpenedEntry = null;
    public static int lastOpenedPage = 0;
    public static BaseDocScreen previousScreen = null;

    public static List<ResourceLocation> bookmarks = new ArrayList<>();
    public static List<SpellSound> favoriteSounds = new ArrayList<>();

}
