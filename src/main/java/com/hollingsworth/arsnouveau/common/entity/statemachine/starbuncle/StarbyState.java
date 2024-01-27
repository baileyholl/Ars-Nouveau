package com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IState;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IStateEvent;
import org.jetbrains.annotations.Nullable;

public class StarbyState implements IState<StarbyState> {
    public Starbuncle starbuncle;
    public StarbyTransportBehavior behavior;

    public StarbyState(Starbuncle starbuncle, StarbyTransportBehavior behavior) {
        this.starbuncle = starbuncle;
        this.behavior = behavior;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Nullable
    @Override
    public StarbyState tick() {
        return null;
    }

    @Nullable
    @Override
    public StarbyState onEvent(IStateEvent event) {
        return null;
    }
}
