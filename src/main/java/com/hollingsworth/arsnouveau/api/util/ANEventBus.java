package com.hollingsworth.arsnouveau.api.util;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ANEventBus {

    /**
     * Returns true if the event is canceled
     */
    public static <T extends Event & ICancellableEvent> boolean post(T cancellableEvent){
        return NeoForge.EVENT_BUS.post(cancellableEvent).isCanceled();
    }
}
