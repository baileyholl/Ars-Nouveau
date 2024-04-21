package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectRewind;

import java.util.Stack;

public interface IRewindable {

    Stack<EffectRewind.Data> getMotions();

    boolean isRewinding();

    void setRewinding(boolean rewinding);

}
