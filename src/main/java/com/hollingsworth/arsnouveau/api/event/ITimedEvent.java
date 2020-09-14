package com.hollingsworth.arsnouveau.api.event;

public interface ITimedEvent {

    void tick();

    /**
     * If this event should be removed from the queue
     */
    boolean isExpired();
}
