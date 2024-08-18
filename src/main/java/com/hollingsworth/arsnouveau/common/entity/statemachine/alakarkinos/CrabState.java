package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IState;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IStateEvent;
import org.jetbrains.annotations.Nullable;

public class CrabState implements IState<CrabState> {

    public Alakarkinos alakarkinos;

    public CrabState(Alakarkinos alakarkinos) {
        this.alakarkinos = alakarkinos;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Nullable
    @Override
    public CrabState tick() {
        return null;
    }

    @Nullable
    @Override
    public CrabState onEvent(IStateEvent event) {
        return null;
    }
}
