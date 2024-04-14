package com.hollingsworth.arsnouveau.common.entity.debug;

public interface IDebuggerProvider {

    IDebugger getDebugger();

     default void addDebugEvent(DebugEvent event){
         this.addDebugEvent(event, false);
    }

    default void addDebugEvent(DebugEvent event, boolean storeDuplicate){
        getDebugger().addEntityEvent(event, storeDuplicate);
    }
}
