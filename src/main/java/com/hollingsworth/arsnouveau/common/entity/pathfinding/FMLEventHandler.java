package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/**
 * Event handler used to catch various forge events.
 */
public class FMLEventHandler {

    public static void onServerStopped(final ServerStoppingEvent event) {
        Pathfinding.shutdown();
        EventQueue.getServerInstance().clear();
    }

    public static void onPlayerLoggedOut(final ClientPlayerNetworkEvent.LoggingOut loggingOut) {
        EventQueue.getClientQueue().clear();
    }
}
