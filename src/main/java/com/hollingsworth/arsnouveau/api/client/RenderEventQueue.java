package com.hollingsworth.arsnouveau.api.client;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
// Client version of EventQueue
public class RenderEventQueue {
    List<ITimedEvent> events;

    public void tick(RenderWorldLastEvent evt, PlayerEntity player, float renderPartialTicks){
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
            event.tick(evt, player, renderPartialTicks);
        }
    }

    public void addEvent(ITimedEvent event){
        events.add(event);
    }

    public static RenderEventQueue getInstance(){
        if(eventQueue == null)
            eventQueue = new RenderEventQueue();
        return eventQueue;
    }

    private static RenderEventQueue eventQueue;
    private RenderEventQueue(){
        events = new ArrayList<>();
    }
}
