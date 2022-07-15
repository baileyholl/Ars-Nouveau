package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Event handler used to catch various forge events.
 */
public class FMLEventHandler {
    @SubscribeEvent
    public static void onServerStopped(final ServerStoppingEvent event) {
        Pathfinding.shutdown();
    }
}
