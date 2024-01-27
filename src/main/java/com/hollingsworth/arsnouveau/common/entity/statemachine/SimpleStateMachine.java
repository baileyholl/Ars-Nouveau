package com.hollingsworth.arsnouveau.common.entity.statemachine;

import javax.annotation.Nonnull;

public class SimpleStateMachine<State extends IState, Event extends IStateEvent> {
    public static final boolean DEBUG = true;

    protected State currentState;

    public SimpleStateMachine(@Nonnull State initialState) {
        currentState = initialState;
        currentState.onStart();
    }

    protected void changeState(@Nonnull State nextState) {
        currentState.onEnd();
        currentState = nextState;
        currentState.onStart();
    }

    public void tick() {
        if(currentState == null)
            return;
        IState nextState = currentState.tick();
        if (nextState != null) {
            changeState((State)nextState);
        }
    }

    public void onEvent(Event event) {
        IState nextState = currentState.onEvent(event);
        if (nextState != null) {
            changeState((State) nextState);
        }
    }

    public State getCurrentState(){
        return currentState;
    }
}
