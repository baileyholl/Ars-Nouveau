package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;

import com.hollingsworth.nuggets.common.state_machine.IState;
import com.hollingsworth.nuggets.common.state_machine.IStateEvent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ArcanoState implements IState<ArcanoState> {
    public ArcanoBoss arcanoBoss;
    public Level level;

    public ArcanoState(ArcanoBoss arcanoBoss) {
        this.arcanoBoss = arcanoBoss;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Nullable
    @Override
    public ArcanoState tick() {
        return null;
    }

    @Nullable
    @Override
    public ArcanoState onEvent(IStateEvent event) {
        return null;
    }
}
