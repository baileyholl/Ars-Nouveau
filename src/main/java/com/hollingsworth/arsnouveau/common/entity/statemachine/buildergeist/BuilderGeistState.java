package com.hollingsworth.arsnouveau.common.entity.statemachine.buildergeist;

import com.hollingsworth.arsnouveau.common.entity.BuilderGeist;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IState;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IStateEvent;
import org.jetbrains.annotations.Nullable;

public class BuilderGeistState implements IState<BuilderGeistState> {

    public BuilderGeist geist;
    public int ticksRunning;

    public BuilderGeistState(BuilderGeist geist) {
        this.geist = geist;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Nullable
    @Override
    public BuilderGeistState tick() {
        ticksRunning++;
        return null;
    }

    @Nullable
    @Override
    public BuilderGeistState onEvent(IStateEvent event) {
        return null;
    }
}
