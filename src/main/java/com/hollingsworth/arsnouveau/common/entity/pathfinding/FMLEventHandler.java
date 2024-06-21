package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/**
 * Event handler used to catch various forge events.
 */
public class FMLEventHandler {
    @SubscribeEvent
    public static void onServerStopped(final ServerStoppingEvent event) {
        Pathfinding.shutdown();
    }
}
