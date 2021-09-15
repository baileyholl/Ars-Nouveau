package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * Event handler used to catch various forge events.
 */
public class FMLEventHandler
{
    @SubscribeEvent
    public static void onServerStopped(final FMLServerStoppingEvent event)
    {
        Pathfinding.shutdown();
    }
}
