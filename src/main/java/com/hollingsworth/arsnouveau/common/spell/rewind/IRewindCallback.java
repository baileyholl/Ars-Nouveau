package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;

public interface IRewindCallback {
    /**
     * Called from RewindEvent to rewind the target at the given time
     */
    void onRewind(RewindEvent event);
}
