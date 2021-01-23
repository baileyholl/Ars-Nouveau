package com.hollingsworth.arsnouveau.api.event;

/**
 * A basic timed event for the EventQueue.
 */
public interface ITimedEvent {

    void tick();

    /**
     * If this event should be removed from the queue
     */
    boolean isExpired();

}
