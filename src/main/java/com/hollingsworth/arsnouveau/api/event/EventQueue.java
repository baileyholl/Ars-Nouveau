package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.client.particle.engine.TimedEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class EventQueue {
    List<ITimedEvent> events;

    public void tick(){
        if(events == null || events.size() == 0) {
            return;
        }

        ListIterator<ITimedEvent> eventListIterator = events.listIterator();
        ITimedEvent event;
        while(eventListIterator.hasNext()){
            event = eventListIterator.next();
            if(event.isExpired()) {
                eventListIterator.remove();
                continue;
            }
            event.tick();
        }
    }
    public void addEvent(ITimedEvent event){
        events.add(event);
    }

    public static EventQueue getInstance(){
        if(eventQueue == null)
            eventQueue = new EventQueue();
        return eventQueue;
    }

    private static EventQueue eventQueue;
    private EventQueue(){
        events = new ArrayList<>();
    }
}
