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
        ITimedEvent event;
        for(int i = 0; i < events.size(); i++){
            event = events.get(i);
            if(event.isExpired()){
                stale.add(event);
            }else{
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

    public static EventQueue getInstance(){
        if(eventQueue == null)
            eventQueue = new EventQueue();
        return eventQueue;
    }

    // Tear down on world unload
    public void clear(){
        this.events = null;
    }

    private static EventQueue eventQueue;
    private EventQueue(){
        events = new ArrayList<>();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getInstance().tick(true);
    }

    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getInstance().tick(false);
    }
}
