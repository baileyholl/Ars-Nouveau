package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.common.spell.rewind.RewindEntityData;

import java.util.Stack;

public interface IRewindable {

    Stack<RewindEntityData> getMotions();

    boolean isRewinding();

    void setRewinding(boolean rewinding);

}
