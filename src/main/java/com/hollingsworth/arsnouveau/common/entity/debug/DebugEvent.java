package com.hollingsworth.arsnouveau.common.entity.debug;

public class DebugEvent {
    public final String id;
    public final String message;

    public DebugEvent(String id, String message) {
        this.id = id;
        this.message = message;
    }
}
