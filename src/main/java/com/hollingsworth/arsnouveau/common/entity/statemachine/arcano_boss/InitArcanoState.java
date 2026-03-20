package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;

import com.hollingsworth.nuggets.common.state_machine.IStateEvent;
import org.jetbrains.annotations.Nullable;

public class InitArcanoState extends ArcanoState{

    public InitArcanoState(ArcanoBoss arcanoBoss) {
        super(arcanoBoss);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public @Nullable ArcanoState tick() {
        return null;
    }

    @Override
    public @Nullable ArcanoState onEvent(IStateEvent event) {
        return null;
    }
}
