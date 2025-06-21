package com.hollingsworth.arsnouveau.common.entity.statemachine;

import javax.annotation.Nullable;

public interface IState<T extends IState<?>> {

    /**
     * When the state is first entered.
     */
    void onStart();

    /**
     * When the state is exited.
     */
    void onEnd();

    /**
     * Returns a new state if the state is finished, otherwise returns null.
     */
    @Nullable
    T tick();

    /**
     * Returns a new state if applicable, otherwise returns null.
     */
    @Nullable
    T onEvent(IStateEvent event);
}
