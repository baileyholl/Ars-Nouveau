package com.hollingsworth.arsnouveau.api.event;

import java.util.ArrayList;
import java.util.List;

/**
 * For queuing deferred or over-time tasks. Tick refers to the World Tick event, called on the server side only.
 */
public class EventQueue {
    List<ITimedEvent> events;

    public void tick(){
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
                event.tick();
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
}
