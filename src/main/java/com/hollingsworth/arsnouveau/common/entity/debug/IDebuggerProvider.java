package com.hollingsworth.arsnouveau.common.entity.debug;

public interface IDebuggerProvider {

    IDebugger getDebugger();

     default void addDebugEvent(DebugEvent event){
        getDebugger().addEntityEvent(event);
    }
}
