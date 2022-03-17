package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired once Ars Nouveau has been initialized.
 * Interacting with the Ars Nouveau API should be done in Setup. This includes adding Glyphs, Recipes, Spell Schools, Rituals, Familiars, Etc.
 * PostSetup can be used once all other addons have been loaded. Use this to add your allowed augments to those glyphs if needed.
 */
public class ArsNouveauAPIEvent extends Event {
    public final ArsNouveauAPI api;
    private ArsNouveauAPIEvent(ArsNouveauAPI api) {
        this.api = api;
    }

    public static class Init extends ArsNouveauAPIEvent {
        public Init(ArsNouveauAPI api) {
            super(api);
        }
    }

    public static class PostInit extends ArsNouveauAPIEvent {
        public PostInit(ArsNouveauAPI api) {
            super(api);
        }
    }
}
