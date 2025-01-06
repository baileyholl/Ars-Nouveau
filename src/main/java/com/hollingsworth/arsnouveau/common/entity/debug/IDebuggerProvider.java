package com.hollingsworth.arsnouveau.common.entity.debug;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.util.Log;

public interface IDebuggerProvider {

    IDebugger getDebugger();

     default void addDebugEvent(DebugEvent event){
         this.addDebugEvent(event, false);
    }

    default void addDebugEvent(DebugEvent event, boolean storeDuplicate){
        getDebugger().addEntityEvent(event, storeDuplicate);
        if(ArsNouveau.isDebug){
            Log.getLogger().debug(event.toString());
        }
    }
}
