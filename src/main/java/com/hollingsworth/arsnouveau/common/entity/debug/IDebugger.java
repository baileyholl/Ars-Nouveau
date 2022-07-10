package com.hollingsworth.arsnouveau.common.entity.debug;

import java.io.IOException;
import java.io.PrintWriter;

public interface IDebugger {

    void addEntityEvent(DebugEvent event);

    void writeFile(PrintWriter writer) throws IOException;
}
