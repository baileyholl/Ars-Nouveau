package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.nbt.CompoundTag;

/**
 * A basic timed event for the EventQueue.
 */
public interface ITimedEvent {

    void tick(boolean serverSide);

    /**
     * If this event should be removed from the queue
     */
    boolean isExpired();

    // Methods for sending a timed event as a packet to the client side
    default CompoundTag serialize(CompoundTag tag) {
        if (getID().isEmpty())
            throw new IllegalStateException("Serialize without ID");
        tag.putString("id", getID());
        return tag;
    }


    default Void onPacketHandled() {
        //Client sided
        EventQueue.getClientQueue().addEvent(this);
        return null;
    }

    default String getID() {
        return "";
    }

    default void onServerStopping(){

    }
}
