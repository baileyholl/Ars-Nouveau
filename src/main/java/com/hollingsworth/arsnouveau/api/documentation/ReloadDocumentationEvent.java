package com.hollingsworth.arsnouveau.api.documentation;

import net.neoforged.bus.api.Event;

/**
 * Mods should only listen to the AddEntries and Post events.
 */
public class ReloadDocumentationEvent extends Event {


    private ReloadDocumentationEvent() {
    }

    /**
     * Fired after Ars Nouveau has added its own entries. This is fired every time
     * the player joins a world or when the world reloads, listeners should be idempotent.
     * Modifications to entries should only be made in the Post event.
     */
    public static class AddEntries extends ReloadDocumentationEvent {
        public AddEntries() {}
    }

    /**
     * Fired immediately after AddEntries.
     */
    public static class Post extends ReloadDocumentationEvent {
        public Post() {}
    }
}
