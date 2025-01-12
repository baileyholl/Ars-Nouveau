package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.common.spell.rewind.RewindEntityData;

import java.util.Stack;

/**
 * Mixin'd onto Entity to store entity state per tick for rewinding
 */
public interface IRewindable {

    Stack<RewindEntityData> getMotions();

    boolean isRewinding();

    void setRewinding(boolean rewinding);

}
