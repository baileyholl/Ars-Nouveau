package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * For queuing deferred or over-time tasks. Tick refers to the Server or Client Tick event.
 */
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventQueue {
    List<ITimedEvent> events;

    public void tick(boolean serverSide){
        if(events == null || events.isEmpty()) {
            return;
        }

        List<ITimedEvent> stale = new ArrayList<>();

        for (ITimedEvent event : events) {
            if (event.isExpired()) {
                stale.add(event);
            } else {
                event.tick(serverSide);
            }
        }
        this.events.removeAll(stale);
    }

    public void addEvent(ITimedEvent event){
        if(events == null)
            events = new ArrayList<>();
        events.add(event);
    }

    public static EventQueue getServerInstance(){
        if(serverQueue == null)
            serverQueue = new EventQueue();
        return serverQueue;
    }

    public static EventQueue getClientQueue(){
        if(clientQueue == null)
            clientQueue = new EventQueue();
        return clientQueue;
    }


    // Tear down on world unload
    public void clear(){
        this.events = null;
    }

    // Split these because our integrated servers are CURSED and both tick.
    private static EventQueue serverQueue;
    private static EventQueue clientQueue;
    private EventQueue(){
        events = new ArrayList<>();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getServerInstance().tick(true);
    }

    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getClientQueue().tick(false);
    }
}
