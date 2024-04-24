package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;

public interface IRewindCallback {

    void onRewind(RewindEvent event);
}
