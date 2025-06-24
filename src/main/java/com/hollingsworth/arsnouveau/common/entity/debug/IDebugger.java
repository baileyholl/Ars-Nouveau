package com.hollingsworth.arsnouveau.common.entity.debug;

import java.io.PrintWriter;

public interface IDebugger {

    default void addEntityEvent(DebugEvent event) {
        addEntityEvent(event, false);
    }

    void addEntityEvent(DebugEvent event, boolean storeDuplicate);

    void writeFile(PrintWriter writer);
}
