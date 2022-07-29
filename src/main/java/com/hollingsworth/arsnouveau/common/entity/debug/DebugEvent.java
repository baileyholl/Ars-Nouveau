package com.hollingsworth.arsnouveau.common.entity.debug;

public class DebugEvent {
    public String id;
    public String message;
    public long timestamp;

    public DebugEvent(String id, String message) {
        this.id = id;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        String localTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(timestamp));
        return "[" + localTime + "] " + id + ": " + message;
    }
}
